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
        socket.openWriteChannel(autoFlush = true) // autoFlush = true, para que se env铆e el mensaje al instante

    /**
     * Dos filosof铆as, si queremos podemos estar en un buucle while y recibir/mandar
     * mensajes hasta que el cliente se desconecte, o bien, (como har铆a FTP)
     * O atender y desconectar (desconexion) en cada petici贸n para liberar recursos (como hace http)
     */

    suspend fun run() {
        try {

            // Leemos la peticion mensaje del cliente
            val mensaje = entrada.readUTF8Line()
            /*if (mensaje == null) {
                logger.debug { "Cliente desconectado" }
                println("馃敶 No hay mensaje. Cliente desconectado")
                desconectar()
            }*/

            // Sacamos la petci贸n del mensaje y la convertimos a objeto
            val peticion = json.decodeFromString<Request<Jamon>>(mensaje!!)
            logger.debug { "Petici贸n recibida: $peticion" }


            // Analizamos la petici贸n
            when (peticion.type) {
                Request.Type.GET -> {
                    logger.debug { "Petici贸n GET recibida" }
                    // Obtenemos el jam贸n
                    val jamon = Secadero.puertaSalida.receive()
                    println("\uD83D\uDD36 Jamon recibido de secadero -> $peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = jamon,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // A帽adimos el salto de l铆nea para que se env铆e
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }

                Request.Type.POST -> {
                    logger.debug { "Petici贸n POST recibida" }
                    // A帽adimos el jam贸n
                    peticion.content?.let { Secadero.puertaEntrada.send(it) }
                    println("\uD83D\uDD37 Jamon enviado al secadero -> ${peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = peticion.content,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // A帽adimos el salto de l铆nea para que se env铆e
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }

                // PUT y DELETE no se implementan

                else -> {
                    logger.debug { "Petici贸n no reconocida" }
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = "Petici贸n no reconocida",
                        type = Response.Type.ERROR
                    )
                    val respuestaJson =
                        json.encodeToString(respuesta) + "\n" // A帽adimos el salto de l铆nea para que se env铆e
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeStringUtf8(respuestaJson)
                }
            }

        } catch (e: Exception) {
            logger.error(e) { "Error en la conexi贸n con: $socket" }
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
