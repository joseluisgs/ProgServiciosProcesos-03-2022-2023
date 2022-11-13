package server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Request
import models.Response
import monitor.SecaderoLock
import mu.KotlinLogging
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

private val logger = KotlinLogging.logger {}
private val json = Json { prettyPrint = true }

class GestionClientes(private val socket: Socket, private val secadero: SecaderoLock) : Runnable {

    // Para poder leer y escribir en el socket
    // Para leer String, si no sería ObjectInputStream
    private val entrada = DataInputStream(socket.inputStream)

    // Para escribir String si no sería ObjectOutputStream
    private val salida = DataOutputStream(socket.outputStream)

    override fun run() {

        /**
         * Dos filosofías, si queremos podemos estar en un buucle while y recibir/mandar
         * mensajes hasta que el cliente se desconecte, o bien, (como haría FTP)
         * O atender y desconectar (desconexion) en cada petición para liberar recursos (como hace http)
         */

        try {

            socket.setSoLinger(true, 10) //tiempo para que el puerto esté abierto (Opcional)

            // Leemos la peticion mensaje del cliente
            val mensaje = entrada.readUTF()
            // Sacamos la petción del mensaje y la convertimos a objeto
            val peticion = json.decodeFromString<Request<Jamon>>(mensaje)
            logger.debug { "Petición recibida: $peticion" }

            // Analizamos la petición
            when (peticion.type) {
                Request.Type.GET -> {
                    logger.debug { "Petición GET recibida" }
                    // Obtenemos el jamón
                    val jamon = secadero.get()
                    println("Jamon recibido de secadero -> $peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = jamon,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson = json.encodeToString(respuesta)
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeUTF(respuestaJson)
                }

                Request.Type.POST -> {
                    logger.debug { "Petición POST recibida" }
                    // Añadimos el jamón
                    peticion.content?.let { secadero.put(it) }
                    println("Jamon enviado al secadero -> ${peticion.content}")
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = peticion.content,
                        type = Response.Type.OK
                    )
                    // Enviamos la respuesta
                    val respuestaJson = json.encodeToString(respuesta)
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeUTF(respuestaJson)
                }

                // PUT y DELETE no se implementan

                else -> {
                    logger.debug { "Petición no reconocida" }
                    // Creamos la respuesta
                    val respuesta = Response(
                        content = "Petición no reconocida",
                        type = Response.Type.ERROR
                    )
                    val respuestaJson = json.encodeToString(respuesta)
                    logger.debug { "Respuesta: $respuestaJson" }
                    salida.writeUTF(respuestaJson)
                }
            }

        } catch (ex: IOException) {
            logger.error { "Error al atender al cliente: ${ex.message}" }
        } finally {
            // Cerramos todo
            try {
                entrada.close()
                salida.close()
                socket.close()
            } catch (ex: IOException) {
                logger.error { "Error al cerrar los flujos: ${ex.message}" }
            }
        }
    }
}
