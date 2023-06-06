package server

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Mensaje
import models.Request
import monitor.Cache
import mu.KotlinLogging

/**
 * Te recomiendo que mires el código en pararelo con el de Cliente
 */

private val logger = KotlinLogging.logger {}
private val json = Json


class GestionClientes(private val socket: Socket, private val id: Long) {

    // Creamos un canal de entrada y salida
    private val entrada = socket.openReadChannel()
    private val salida =
        socket.openWriteChannel(autoFlush = true) // autoFlush = true, para que se envíe el mensaje al instante

    suspend fun run() = withContext(Dispatchers.IO) {
        try {

            // Lo primero que tenemos que hacer es mandar los mensajes de la cache
            // Que es lo que espera el cliente
            sendMensajesEnCached()

            // Lo primero es crear la corrutina, para que se ejecute en segundo plano
            // y que siempre emita el último mensaje que se haya recibido
            val sendUltimosMensajesJob = launch { sendUltimoMensaje() }

            val procesamientoMensajesJob = launch {
                logger.debug { "Lanzando corrutina para procesar mensajes" }
                procesamientoMensajes(entrada, salida)
            }

            /* while (true) {

                 logger.debug { "Esperando mensaje..." }
                 val input = entrada.readUTF8Line()
                 logger.debug("GC$id: Recibido mensaje del cliente: $input")
                 // Obtenemos el mensaje
                 val request = json.decodeFromString<Request<Mensaje>>(input!!)

                 if (request.type == Request.Type.LOGOUT) {
                     logger.debug("GC$id: Cliente desconectado")
                     break
                 }

                 // Lanzamos corrutina para atender al cliente

             }*/
            procesamientoMensajesJob.join()
            sendUltimosMensajesJob.cancelAndJoin()

        } catch (e: Exception) {
            logger.error(e) { "Error en la conexión con: $socket" }
        } finally {
            desconectar()
        }
    }

    /**
     * Encargada de procesar los mensajes que llegan del cliente y darle respuesta
     */
    private suspend fun procesamientoMensajes(entrada: ByteReadChannel, salida: ByteWriteChannel) {
        while (!entrada.isClosedForRead && !salida.isClosedForWrite) {
            logger.debug { "Esperando mensaje..." }
            val input = entrada.readUTF8Line()
            logger.debug("GC$id: Recibido mensaje del cliente: $input")
            // Obtenemos el mensaje
            input?.let {
                val request = json.decodeFromString<Request<Mensaje>>(input)

                when (request.type) {
                    Request.Type.LOGOUT -> {
                        logger.debug { "GC$id: Mensaje de tipo LOGOOUT" }
                        println("GC$id: \uD83D\uDC4B Cliente ${request.content?.user} desconectado")
                        return // Salimos de la función
                    }

                    Request.Type.POST -> {
                        logger.debug { "GC$id: Mensaje de tipo POST" }
                        // Añadimos el mensaje a la cache
                        Cache.add(request.content!!)
                    }

                    else -> {
                        logger.debug { "GC$id: Mensaje de tipo no identificado" }
                    }
                }

                delay(100) // Por gusto!!
            }
        }
    }

    /**
     * Envía los mensajes de la cache al cliente
     */
    private suspend fun sendMensajesEnCached() {
        // Obtenemos los mensajes de la cache que puede haber
        val mensajes = mutableListOf<Mensaje>()
        if (Cache.size > 0) {
            val res = Cache.data.take(Cache.size).toList()
            mensajes.addAll(res)
        }

        // Los enviamos al cliente
        val respuestaJson =
            json.encodeToString<List<Mensaje>>(mensajes.toList()) + "\n" // Añadimos el salto de línea para que se envíe
        logger.debug { "GC$id: Enviando mensajes en cache: $respuestaJson" }
        salida.writeStringUtf8(respuestaJson)
    }

    /**
     * Envía el último mensaje que se haya recibido en base al State Flow de cache
     */
    private suspend fun sendUltimoMensaje() {
        // Obtener el último mensaje de la cache y lo envio cada segundo si no ha cambiado
        // Si lo hacemos con stateFlow
        /*var ultimoMensaje: Mensaje? = null
        while (!salida.isClosedForWrite) {
            val mensajeEstado = Cache.mensaje.value
            // Si no es nulo, lo enviamos y si no es igual
            mensajeEstado?.let {
                if (it != ultimoMensaje) {
                    ultimoMensaje = it
                    val respuestaJson = json.encodeToString<Mensaje>(it) + "\n"
                    logger.debug { "GC$id: Enviando ultimo mensaje: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                    ultimoMensaje = it
                }
            }
            delay(1000)
        }*/
        // Si lo hacemos con un shared, no hace falta el while, ni el delay
        // Ni comparar
        Cache.mensaje.collect {
            val respuestaJson = json.encodeToString<Mensaje>(it) + "\n"
            logger.debug { "GC$id: Enviando ultimo mensaje: $respuestaJson" }
            salida.writeStringUtf8(respuestaJson)
        }
    }

    /**
     * Cierra la conexión con el cliente
     */
    private suspend fun desconectar() {
        logger.debug { "GC$id: Desconectando cliente: $socket" }
        withContext(Dispatchers.IO) {
            salida.close()
            socket.close()
        }
    }
}
