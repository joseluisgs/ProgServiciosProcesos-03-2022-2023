/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.cliente;

import java.io.DataInputStream;
import java.io.IOException;
import javax.swing.JTextArea;

/**
 *
 * @author link
 */
public class GestionMensajes extends Thread {
    private DataInputStream entradaDatos;
    private String mensaje;
    private boolean conectado = false;
    private JTextArea mensajesChat;

    public GestionMensajes(DataInputStream entradaDatos, JTextArea mensajesChat) {
        this.entradaDatos = entradaDatos;
        this.mensajesChat = mensajesChat;
    }
    
    
    
    @Override
    public void run(){
        // Bucle infinito que recibe mensajes del servidor, se podría sacar en un uhilo
        
        boolean conectado = true;
        while (conectado) {
            try {
                // Recibimos y actualizamos nuestro cuadro de texto
                mensaje = entradaDatos.readUTF();
                mensajesChat.append(mensaje + System.lineSeparator());
            } catch (IOException ex) {
                System.err.println("Error al leer del stream de entrada: " + ex.getMessage());
                conectado = false;
            } catch (NullPointerException ex) {
                System.err.println("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
        
    }
    
    
    
}
