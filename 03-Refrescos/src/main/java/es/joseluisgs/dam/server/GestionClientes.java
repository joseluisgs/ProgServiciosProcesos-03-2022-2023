/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import es.joseluisgs.dam.model.Maquina;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author link
 */
public class GestionClientes extends Thread {

    private Maquina listaRefrescos;
    private Maquina listaRefrescosEnviar;
    private int numRefrescos;
    private Socket s;

    public GestionClientes(Socket s, Maquina listaRefrescos) {
        this.s = s;
        this.listaRefrescos = listaRefrescos;
        listaRefrescosEnviar = new Maquina();
    }

    @Override
    public void run() {

        ObjectOutputStream bufferObjetos = null;
        try {
            //System.out.println("Peticion -> "+s.getInetAddress()+" --- "+s.getPort());
            s.setSoLinger(true, 10);//tiempo para que el puerto este abierto

            //recibo el numero de refrescos que quiere el cliente
            DataInputStream datos = new DataInputStream(s.getInputStream());
            numRefrescos = datos.readInt();

            if (listaRefrescos.size() > 0 && listaRefrescos.size() >= numRefrescos) {
                //paso todos los refrescos que solicita a una lista de refrescos para enviar
                for (int i = 0; i < numRefrescos; i++) {
                    listaRefrescosEnviar.add(listaRefrescos.remove(0));
                }
            } else {
                System.out.println("No  hay suficientes Refrescos");
                //paso todos los refrescos que quedan en el servidor a ese cliente
                listaRefrescosEnviar = listaRefrescos.removeAll();
            }
            //le envio la lista de los refrescos que ha pedido
            bufferObjetos = new ObjectOutputStream(s.getOutputStream());
            bufferObjetos.writeObject(listaRefrescosEnviar);
            //System.out.println ("Enviado '" + listaRefrescosEnviar.toString()+"'");

            datos.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                bufferObjetos.close();
            } catch (IOException ex) {
                Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (listaRefrescos.size() <= 0) {
                System.exit(0);
                System.out.println("Servidor finalizado...");
            }
        }
    }
}
