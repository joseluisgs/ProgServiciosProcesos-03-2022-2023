package server

import monitor.SecaderoLock
import mu.KotlinLogging
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}
const val PORT = 6969
fun main() {
    //creo los refrescos y los añado a la lista
    val servidor: ServerSocket
    var cliente: Socket

    var numConnections: Long = 0

    // Mi monitor
    val secadero = SecaderoLock(10)
    // Mi pool de hilos para atender a los clientes
    val pool = Executors.newFixedThreadPool(10)

    try {
        servidor = ServerSocket(PORT)
        logger.info { "Arrancando Servidor en: ${servidor.inetAddress}:${servidor.localPort}" }

        // Empezamos a escuchar hasta que nos aburramos (soy un servidor y solo sirvo sin colgarme)
        while (true) {
            // Me bloqueo hasta que llegue un cliente y lo acepte
            logger.info { "Esperando conexiones" }
            cliente = servidor.accept()
            numConnections++
            println("Cliente $numConnections conectado${cliente.inetAddress} --- ${cliente.port}")

            // Cedemos el socket al hilo para que lo atienda
            val gc = GestionClientes(cliente, secadero)
            pool.execute(gc)
        }
        println("Servidor finalizado...")
        servidor.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
