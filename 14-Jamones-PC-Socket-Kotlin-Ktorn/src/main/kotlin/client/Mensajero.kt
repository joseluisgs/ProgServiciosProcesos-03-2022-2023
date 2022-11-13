package client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import models.Request
import models.Response
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// Serializador quitar el prettyprint porque al tener saltos de línea no se puede enviar ni leer bien por socket
private val json = Json
// private val jsonPrint = Json { prettyPrint = true }


private const val HOST = "localhost"
private const val PORT = 6969

/**
 * Dos filosofías, si queremos podemos estar en un bucle while y recibir/mandar
 * mensajes hasta que el cliente se desconecte, o bien, (como haría FTP)
 * O atender y desconectar (desconexion) en cada petición para liberar recursos (como hace http)
 * La diferencia es que en el primer caso, el servidor tiene que estar preparado para recibir
 * peticiones de varios clientes a la vez que se liberan, y en el segundo caso, el servidor tiene que estar
 * preparado para recibir peticiones de varios clientes pero no se liberan hasta que el cliente se desconecta
 * y esta desconexión es la que libera los recursos y puede tardar!!!
 */


class Mensajero(val id: String, val tamLote: Int = 3, val maxLotes: Int = 20, val pausa: Int = 3000) {

    suspend fun run() {
        val misJamones = mutableListOf<Jamon>()
        var numLote = 1

        // Indicamos el Dispatcher para el Cliente
        val selectorManager = SelectorManager(Dispatchers.IO)

        while (numLote <= maxLotes) {
            try {
                // Creamos el socket TCP
                val socket = aSocket(selectorManager).tcp()
                    .connect(HOST, PORT) // Nos conectamos al servidor en el puerto 6969

                println("✅ $id Conectado a: ${socket.remoteAddress}")

                // Creamos un canal de entrada y salida
                val entrada = socket.openReadChannel()
                val salida = socket.openWriteChannel(autoFlush = true)

                // Enviamos una petición al servidor
                val request = Request<Jamon>(
                    content = null,
                    type = Request.Type.GET
                )
                val peticion = json.encodeToString(request) + "\n" // Añadimos el salto de línea para que se envíe
                logger.debug { "Petición: $peticion" }
                salida.writeStringUtf8(peticion)

                // Leemos la respuesta del servidor
                val mensaje = entrada.readUTF8Line()
                // Sacamos la respuesta del mensaje y la convertimos a objeto
                val respuesta = json.decodeFromString<Response<Jamon>>(mensaje!!)
                logger.debug { "Respuesta recibida: $respuesta" }

                if (respuesta.type == Response.Type.OK) {
                    println("\uD83D\uDE9A $id: jamon recibido -> ${respuesta.content}")
                    respuesta.content?.let { jamon -> misJamones.add(jamon) }

                    // Si puedo sacar un lote lo imprimo
                    if (misJamones.size % tamLote == 0) {
                        val lote = Lote(idMensajero = id, jamones = misJamones)
                        val loteJson = Json.encodeToString(lote)
                        println("\uD83D\uDCE6 $id: lote ${numLote} creado -> $loteJson")
                        misJamones.clear()
                        numLote++
                    }
                } else {
                    logger.error { "Error al enviar jamón a secadero" }
                }


                withContext(Dispatchers.IO) {
                    salida.close()
                    socket.close()
                }

                // Esperamos un poco
                delay(pausa.toLong())

            } catch (e: Exception) {
                logger.error { "Error al atender al mensajero: ${e.message}" }
                // return of run()
            }
        }
        println("Mensajero $id finalizado")
    }
}