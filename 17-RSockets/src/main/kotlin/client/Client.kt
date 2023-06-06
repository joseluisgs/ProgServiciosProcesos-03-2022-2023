package client

import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.tcp.TcpClientTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

private const val PORT = 8080
private const val HOST_NAME = "localhost"

class Client(val idServer: Int) {

    private val id = UUID.randomUUID().toString().substring(0, 5)

    private val transport = TcpClientTransport(HOST_NAME, PORT)
    private val connector = RSocketConnector {
        // conguración del cliente, si no podemos usar RSocketConnector() por defecto
        logger.info("Client started: $idServer-$id")
    }

    suspend fun start() {
        // conectamos al servidor
        val rsocket = connector.connect(transport).also {
            println("\uD83D\uDD35 Client $id ready with RSocket")
        }
        // hacemos una petición y esperamos la respuesta
        saludar(rsocket)
        reactiveStreamFlow(rsocket)
    }

    // Handler para stream
    private suspend fun reactiveStreamFlow(rsocket: RSocket) {
        //request stream
        val stream: Flow<Payload> = rsocket.requestStream(
            buildPayload {
                data("Hola desde Stream. Client $idServer-$id")
            }
        )

        // Mostramos los 5 primeros elementos
        stream.take(5).collect { payload: Payload ->
            println("Client $idServer-$id -> ${payload.data.readText()}")
        }
    }

    // Handler para saludar
    private suspend fun saludar(rsocket: RSocket) {
        val response = rsocket.requestResponse(
            buildPayload {
                data("\uD83D\uDC4B Hello from client $idServer-$id")
            }
        )
        // Mostramos la respuesta
        println("Client  $idServer-$id -> ${response.data.readText()}")
    }

}