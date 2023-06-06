package es.joseluisgs.dam.clienteservidor.server

import es.joseluisgs.dam.clienteservidor.model.Maquina
import java.io.DataInputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.logging.Level
import java.util.logging.Logger


class GestionClientes(private val s: Socket, private val listaRefrescos: Maquina) : Thread() {
    private var listaRefrescosEnviar: Maquina
    private var numRefrescos = 0

    override fun run() {
        var bufferObjetos: ObjectOutputStream? = null
        try {
            //System.out.println("Peticion -> "+s.getInetAddress()+" --- "+s.getPort());
            s.setSoLinger(true, 10) //tiempo para que el puerto este abierto

            //recibo el numero de refrescos que quiere el cliente
            val datos = DataInputStream(s.getInputStream())
            numRefrescos = datos.readInt()

            if (listaRefrescos.size() > 0 && listaRefrescos.size() >= numRefrescos) {
                //paso todos los refrescos que solicita a una lista de refrescos para enviar
                for (i in 0 until numRefrescos) {
                    listaRefrescosEnviar.add(listaRefrescos.remove(0))
                }
            } else {
                println("No  hay suficientes Refrescos")
                //paso todos los refrescos que quedan en el servidor a ese cliente
                listaRefrescosEnviar = listaRefrescos.removeAll()
            }

            //le envio la lista de los refrescos que ha pedido
            bufferObjetos = ObjectOutputStream(s.getOutputStream())
            bufferObjetos.writeObject(listaRefrescosEnviar)
            //System.out.println ("Enviado '" + listaRefrescosEnviar.toString()+"'");
            datos.close()
            s.close()
        } catch (ex: IOException) {
            Logger.getLogger(GestionClientes::class.java.name).log(Level.SEVERE, null, ex)
        } finally {
            try {
                bufferObjetos!!.close()
            } catch (ex: IOException) {
                Logger.getLogger(GestionClientes::class.java.name).log(Level.SEVERE, null, ex)
            }
            if (listaRefrescos.size() <= 0) {
                System.exit(0)
                println("Servidor finalizado...")
            }
        }
    }

    init {
        listaRefrescosEnviar = Maquina()
    }
}
