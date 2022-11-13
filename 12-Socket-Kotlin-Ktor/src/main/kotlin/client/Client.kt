import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = runBlocking {
    // Indicamos el Dispatcher para el Cliente
    val selectorManager = SelectorManager(Dispatchers.IO)
    // Creamos el socket TCP
    val socket = aSocket(selectorManager).tcp()
        .connect("127.0.0.1", 6969) // Nos conectamos al servidor en el puerto 6969

    println("Conectado a: ${socket.remoteAddress}")

    // Creamos un canal de entrada y salida
    val receiveChannel = socket.openReadChannel()
    val sendChannel = socket.openWriteChannel(autoFlush = true)


    launch(Dispatchers.IO) {
        // solo esuchamos lo que nos mande el servidor
        escucharServidor(receiveChannel, socket, selectorManager)
    }

    launch {
        // solo enviamos lo que nos mande el usuario
        enviarAlServidor(sendChannel)
    }

}

suspend fun enviarAlServidor(sendChannel: ByteWriteChannel) {
    // Enviamos un mensaje, contínuamene hasta que el usuario escriba salir
    while (true) {
        val miMensaje = readln()
        sendChannel.writeStringUtf8("$miMensaje\n")
    }
}

private suspend fun escucharServidor(
    receiveChannel: ByteReadChannel,
    socket: Socket,
    selectorManager: SelectorManager
) {
    // n-os quedamos en bucle indefindo escuchando lo que nos mande el servidor
    // salismos si el canal se cierra
    while (true) {
        val saludos = receiveChannel.readUTF8Line()
        if (saludos != null) {
            println(saludos)
        } else {
            println("Servidor cerró la conexión")
            socket.close()
            selectorManager.close()
            exitProcess(0)
        }
    }
}
