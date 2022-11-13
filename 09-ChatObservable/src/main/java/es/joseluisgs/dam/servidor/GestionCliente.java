/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.servidor;

import es.joseluisgs.dam.comun.MensajesChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Lo resulevo por paso de mensajes
 * @author link
 */
public class GestionCliente extends Thread implements Observer{
    
    private Socket socket; 
    private MensajesChat mensajes;
    // Para la entrada y salida de datos a través de la red
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    
    public GestionCliente (Socket socket, MensajesChat mensajes){
        this.socket = socket;
        this.mensajes = mensajes;
        
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
            salidaDatos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            
        }
    }
    
    @Override
    public void run(){
        String mensajeRecibido;
        boolean conectado = true;
        
        // Se apunta a la lista de observadores de mensajes para que todos estén al tanto
        // Es decir, es la propio hilo el que se apunta, para ser avisado
        mensajes.addObserver(this);
        
        while (conectado) {
            try {
                // Lee un mensaje enviado por el cliente
                // El hilo de gestion de este cliente se queda esperando hasta que llegue el mensaje
                mensajeRecibido = entradaDatos.readUTF();
                // Pone el mensaje recibido en mensajes para que se notifique 
                // a sus observadores que hay un nuevo mensaje.
                // Como hay un cambio en el mensaje, se disparan los métodos
                // del patron oservador y observable
                mensajes.setMensaje(mensajeRecibido);
            } catch (IOException ex) {
                System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
                conectado = false; 
                // Si se ha producido un error al recibir datos del cliente se cierra la conexion con el.
                try {
                    entradaDatos.close();
                    salidaDatos.close();
                } catch (IOException ex2) {
                    System.err.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
                }
            }
        }   
    }
    
        /**
	 * Método que se ejecuta cuando se producen cambios
	 * en la clase observada
	 */
	@Override
    
    public void update(Observable o, Object arg) {
        try {
            // Envia el mensaje al cliente, al detectar que ha cambiado
            salidaDatos.writeUTF(arg.toString());
        } catch (IOException ex) {
            System.err.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
}
