package server

import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.tcp.TcpServer
import io.rsocket.kotlin.transport.ktor.tcp.TcpServerTransport
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private const val PORT = 8080
private const val HOST_NAME = "localhost"

class Server {

    // Se crea el servidor
    private val transport = TcpServerTransport(HOST_NAME, PORT)
    private val connector = RSocketServer {
        // Podemos configurar otras opciones o dejar las por defecto usando RSocketServer()
        logger.info("✅ Server started")
        maxFragmentSize = 1024

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start() {
        val server: TcpServer = connector.bind(transport) {
            println("✅ Server ready with RSocket")
            // Handler para peticiones y respuestas
            peticionesRespuestas()
        }
        server.handlerJob.join() // hacemos el join!
    }

    // Handler para peticiones y respuestas
    private fun peticionesRespuestas() = RSocketRequestHandler {
        //handler for request/response
        requestResponse { request: Payload ->
            println("Server -> ${request.data.readText()}") //print request payload data
            delay(500) // work emulation
            // return response payload
            buildPayload {
                data("\uD83D\uDE4B Hello from server")
            }
        }
        //handler for request/stream con flow
        requestStream { request: Payload ->
            println("Server -> ${request.data.readText()}") //print request payload data
            flow {
                repeat(10) { i ->
                    emit(
                        buildPayload {
                            data("Server stream response: $i")
                        }
                    )
                }
            }
        }
    }
}