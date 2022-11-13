package server

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Request
import models.Response
import monitor.Secadero
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val json = Json

class GestionClientes(private val socket: Socket) {

    // Creamos un canal de entrada y salida
    private val entrada = socket.openReadChannel()
    private val salida =
        socket.openWriteChannel(autoFlush = true) // autoFlush = true, para que se envíe el mensaje al instante

    /**
     * Dos filosofías, si queremos podemos estar en un buucle while y recibir/mandar
     * mensajes hasta que el cliente se desconecte, o bien, (como haría FTP)
     * O atender y desconectar (desconexion) en cada petición para liberar recursos (como hace http)
     */

    suspend fun run() {
        try {

            // Leemos la peticion mensaje del cliente
            val mensaje = entrada.readUTF8Line()
            /*if (mensaje == null) {
                logger.debug { "Cliente desconectado" }
                println("🔴 No hay mensaje. Cliente desconectado")
                desconectar()
            }*/

            // Sacamos la petción del mensaje y la convertimos a objeto
            val peticion = json.decodeFromString<Request<Jamon>>(mensaje!!)
            logger.debug { "Petición recibida: $peticion" }


            // Analizamos la petición
            when (peticion.type) {
                Request.Type.GET -> {
                    logger.debug { "Petición GET recibida" }
                    // Obtenemos el jamón
                    val jamon = Secadero.puertaSalida.receive()
                    println("\uD83D\uDD36 Jamon recibido de secadero -> $peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = jamon,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // Añadimos el salto de línea para que se envíe
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }

                Request.Type.POST -> {
                    logger.debug { "Petición POST recibida" }
                    // Añadimos el jamón
                    peticion.content?.let { Secadero.puertaEntrada.send(it) }
                    println("\uD83D\uDD37 Jamon enviado al secadero -> ${peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = peticion.content,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // Añadimos el salto de línea para que se envíe
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }

                // PUT y DELETE no se implementan

                else -> {
                    logger.debug { "Petición no reconocida" }
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = "Petición no reconocida",
                        type = Response.Type.ERROR
                    )
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // Añadimos el salto de línea para que se envíe
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }
            }

        } catch (e: Exception) {
            logger.error(e) { "Error en la conexión con: $socket" }
        } finally {
            desconectar()
        }
    }

    private suspend fun desconectar() {
        logger.debug { "Desconectando cliente: $socket" }
        withContext(Dispatchers.IO) {
            salida.close()
            socket.close()
        }
    }
}
