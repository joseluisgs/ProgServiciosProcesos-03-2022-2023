
package es.joseluisgs.dam.servidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import es.joseluisgs.dam.comun.MensajesChat;

/**
 *
 * @author link
 */
public class Servidor {
    public static void main(String[] args) {
        
        
        int puerto = 6969;
        int maximoConexiones = 10; // Maximo de conexiones simultaneas
        ServerSocket servidor = null; 
        Socket socket = null;
        
        // Para manjar los mensajes
        MensajesChat mensajes = new MensajesChat();
        
        try {
            // Se crea el serverSocket
            servidor = new ServerSocket(puerto, maximoConexiones);
            
            // Bucle infinito para esperar conexiones
            while (true) {
                System.out.println("Servidor a la espera de conexiones...");
                socket = servidor.accept();
                System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");
                
                GestionCliente gc = new GestionCliente(socket, mensajes);
                gc.start();
                
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally{
            try {
                socket.close();
                servidor.close();
            } catch (IOException ex) {
                System.err.println("Error al cerrar el servidor: " + ex.getMessage());
            }
        }
    }
    
}
