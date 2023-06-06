import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import server.GestionClientes

private val logger = KotlinLogging.logger {}
private const val PORT = 6969

fun main(args: Array<String>): Unit = runBlocking {
    var numConnections: Long = 0

    // Dispacher para el servidor IO
    val selectorManager = SelectorManager(Dispatchers.IO)

    // Creamos el socket TCP
    val serverSocket = aSocket(selectorManager)
        .tcp() // TCP
        .bind("127.0.0.1", PORT) // Escuchamos en el puerto 6969

    logger.debug { "Servidor escuchando en: ${serverSocket.localAddress} $PORT" }
    println("✅ Servidor escuchando en: ${serverSocket.localAddress}")

    // Bucle infinito, porque el servidor siempre está escuchando, y desviamos a corrutinas
    while (true) {
        // Nos suspendemos hasta que llegue una conexión
        val socket = serverSocket.accept()
        numConnections++

        logger.debug { "Nueva conexión: ${socket.remoteAddress}" }
        println("✳ Cliente $numConnections conectado desde: ${socket.remoteAddress}")


        // Desviamos a una corrutina
        launch {
            GestionClientes(socket).run()
        }
    }
}

