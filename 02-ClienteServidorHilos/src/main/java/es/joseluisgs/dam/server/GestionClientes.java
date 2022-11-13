/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import es.joseluisgs.dam.model.Ejemplo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que controlará los distintos clientes
 * @author link
 */
public class GestionClientes extends Thread{ 
    private int numCliente;
    private Socket cliente;
    
    public GestionClientes(Socket s, int cliente){
        this.cliente = s;
        this.numCliente = cliente;

    }
    
    @Override
    public void run(){
        
             ObjectInputStream bufferObjetosEntrada = null;
        try {
            System.out.println("Hilo de atención al Cliente: "+numCliente+" de " + cliente.getInetAddress());
            // setSoLinger() a true hace que el cierre del socket espere a que
            // el cliente lea los datos, hasta un máximo de 10 segundos de espera.
            // Si no ponemos esto, el socket se cierra inmediatamente y si el 
            // cliente no ha tenido tiempo de leerlos, los datos se pierden.
            //cliente.setSoLinger (true, 10);
            // Recibimos del cliente, y como nos entra datos es por el input
            bufferObjetosEntrada = new ObjectInputStream(cliente.getInputStream());
            Ejemplo datoEntrada = (Ejemplo) bufferObjetosEntrada.readObject();
            datoEntrada.mostrar();
            System.out.println("Recibido del Cliente '" + datoEntrada.toString() + "'");
            // Se prepara un flujo de salida para objetos y un objeto para enviar al output del cliente
            Ejemplo datoSalida = new Ejemplo();
            ObjectOutputStream bufferObjetosSalida = new ObjectOutputStream(cliente.getOutputStream());
            // Se envia el objeto 
            bufferObjetosSalida.writeObject(datoSalida);
            System.out.println("Enviado al Cliente '" + datoSalida.toString() + "'");
        } catch (IOException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferObjetosEntrada.close();
            } catch (IOException ex) {
                Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
