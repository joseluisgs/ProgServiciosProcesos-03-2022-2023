package client

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun main() {
    // Creamos el cliente y lo configuramos con sus plugins
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        // Creamos una conexión con el servidor, indicando la ruta y el puerto
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            // Creamos las corutinas para enviar y recibir mensajes
            val messageOutputRoutine = launch { outputMessages() }
            val userInputRoutine = launch { inputMessages() }

            // Esperamos a a que se complete (escriba salir), haya error en la entrada
            userInputRoutine.join()
            // Cancelamos la corutina de entrada de mensajes porque ya no la necesitamos
            messageOutputRoutine.cancelAndJoin()
        }
    }
    client.close()
    logger.debug { "Cliente cerrado" }
    println("Conexión cerrada. ¡Adiós! \uD83D\uDC4B")
}

// Función que se encarga de recibir los mensajes del servidor
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        // Por cada mensaje que recibamos, lo mostramos por pantalla
        logger.debug { "Esperando mensajes del servidor" }
        for (message in incoming) {
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        logger.error(e) { "Error al recibir mensajes del servidor:  ${e.localizedMessage}" }
        println("Error al recibir: ${e.localizedMessage}")
    }
}

// Función que se encarga de enviar los mensajes al servidor
suspend fun DefaultClientWebSocketSession.inputMessages() {
    // Vamos leyendo los mensajes que el usuario escribe por consola
    // Hasta que el usuario escriba "salir"
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("salir", true)) return
        try {
            logger.debug { "Enviando mensaje al servidor: $message" }
            send(message)
        } catch (e: Exception) {
            logger.error(e) { "Error al enviar mensaje al servidor: ${e.localizedMessage}" }
            println("Error el enviar: ${e.localizedMessage}")
            return
        }
    }
}