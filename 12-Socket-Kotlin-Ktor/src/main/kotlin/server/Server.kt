import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main(args: Array<String>): Unit = runBlocking {
    // Dispacher para el servidor IO
    val selectorManager = SelectorManager(Dispatchers.IO)
    // Creamos el socket TCP
    val serverSocket = aSocket(selectorManager).tcp()
        .bind("127.0.0.1", 6969) // Escuchamos en el puerto 6969

    println("Servidor escuchando en: ${serverSocket.localAddress}")

    // Bucle infinito, porque el servidor siempre está escuchando, y desviamos a corrutinas
    while (true) {
        // Esperamos una conexión
        val socket = serverSocket.accept()
        println("aceptando conexión desde: $socket")

        launch {
            procesarCliente(socket)
        }
    }
}

private suspend fun procesarCliente(socket: Socket) {
    // Creamos un canal de entrada y salida
    val receiveChannel = socket.openReadChannel()
    val sendChannel = socket.openWriteChannel(autoFlush = true)

    // Enviamos un mensaje, creamos el diálogo nada mas conextatrse
    sendChannel.writeStringUtf8("Introduce tu nombre => \n")
    try {
        // Ahora lo único que hacemos es leer lo que nos manden y responder, y si nos mandan salir, cerramos la conexión
        while (true) {
            val nombre = receiveChannel.readUTF8Line()
            sendChannel.writeStringUtf8("Hola, $nombre!\n")
            if (nombre == "salir") {
                println("Cerrando conexión con: $socket")
                withContext(Dispatchers.IO) {
                    socket.close()
                }
                return
            }
        }
    } catch (e: Throwable) {
        withContext(Dispatchers.IO) {
            socket.close()
        }
    }
}
