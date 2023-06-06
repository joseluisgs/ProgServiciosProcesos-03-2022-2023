package es.joseluisgs.dam.clienteservidor.server

import es.joseluisgs.dam.clienteservidor.model.Maquina
import es.joseluisgs.dam.clienteservidor.model.Refresco
import java.net.ServerSocket
import java.net.Socket


fun main() {
    //numero de refrescos a meter
    val cantidad = 100
    //lista de refrescos
    val listaRefrescos = Maquina()
    //creo los refrescos y los añado a la lista
    repeat(cantidad) {
        listaRefrescos.add(Refresco())
    }
    val servidor: ServerSocket
    var cliente: Socket
    val puerto = 6969
    println("Servidor arrancado y esperando conexiones...")
    try {
        servidor = ServerSocket(puerto)
        // aunque no debería hacerse paramos si no hay refrescos!!
        while (listaRefrescos.size() > 0) {
            println("Esperando...")
            cliente = servidor.accept()
            println("La maquina tiene: " + listaRefrescos.size() + " refrescos")
            println("Peticion de cliente -> " + cliente.inetAddress + " --- " + cliente.port)
            println("La maquina tiene: " + listaRefrescos.size() + " refrescos")
            val gc = GestionClientes(cliente, listaRefrescos)
            gc.start()
        }
        println("Servidor finalizado...")
        servidor.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

