package server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import mu.KotlinLogging
import server.utils.Connection
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {}

// Configuramos el websocket c
fun Application.configureSockets() {
    // configuramos el websocket
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        // lista con las conexiones
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        // ruta para el websocket
        webSocket("/chat") {
            logger.debug { "Nueva conexión recibida" }
            println("¡Nuevo usuario/a!")
            // aumentamos el contador de usuarios
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                // mandamos un mensaje de bienvenida
                logger.debug { "Enviando mensaje de bienvenida" }
                send("✅ ¡Estás en línea! Hay ${connections.count()} usuarios/as en la sala.")
                // bucle para recibir los mensajes son frames
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue // si no hay, pasamos al siguiente
                    val receivedText = frame.readText() // leemos el texto
                    // mandamos el mensaje a todos los usuarios para que lo reciban, con el nombre del usuario
                    logger.debug { "Enviando mensaje a todos los usuarios" }
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                logger.error(e) { "Error en el websocket${e.message}" }
                println(e.localizedMessage)
            } finally {
                // si se desconecta, se elimina de la lista de conexiones
                println("Cerrando $thisConnection \uD83D\uDC4B")
                connections -= thisConnection
            }
        }
    }
}