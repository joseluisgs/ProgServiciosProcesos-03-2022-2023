/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.client;

import es.joseluisgs.dam.model.Ejemplo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author link
 */
public class Cliente {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        /* Se crea el socket cliente */
        Socket servidor = new Socket("localhost", 6555);
        System.out.println("Conectado al servidor...");

            //Vamos a mandar un objeto al servidor, por el OutputStream, salida al servidor
            Ejemplo datoSalida = new Ejemplo("prueba", 69);
            ObjectOutputStream bufferObjetosSalida = new ObjectOutputStream(servidor.getOutputStream());
            bufferObjetosSalida.writeObject(datoSalida);
            System.out.println("Enviado al Servidor '" + datoSalida.toString() + "'");

            //Se obtiene un stream de lectura para leer objetos, servidor input
            ObjectInputStream bufferObjetosEntrada = new ObjectInputStream(servidor.getInputStream());

            /* Se lee un Datosocket que nos envía el servidor y se muestra
              * en pantalla */
            Ejemplo datoEntrada = (Ejemplo) bufferObjetosEntrada.readObject();
            datoEntrada.mostrar();
            System.out.println("Recibido del Servidor '" + datoEntrada.toString() + "'");

    }

    
}
