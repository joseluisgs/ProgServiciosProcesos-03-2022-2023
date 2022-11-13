package server.utils

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

// Clase que representa una conexión, es un monitor del número de usuario
class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    val name = "Usuario${lastId.getAndIncrement()}"
}