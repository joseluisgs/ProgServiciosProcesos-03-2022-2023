import client.Client
import kotlinx.coroutines.*
import server.Server

fun main(args: Array<String>): Unit = runBlocking {
    println("Hola Reactive Sockets!")
    
    val serverJob = launch(Dispatchers.IO) {
        val server = Server()
        server.start()
    }

    // Esperamos a que se levante el servidor
    delay(1000)

    // Lista de clientes
    val clientJobsList = (1..4).map {
        launch(Dispatchers.IO) {
            val client = Client(it)
            client.start()
        }
    }.toMutableList()


    // Esperamos a que terminen
    clientJobsList.joinAll()
    serverJob.join()

}

fun myCallback(body: (Int) -> Int) {
    println("Callback")
    body(1)
}