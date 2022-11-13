/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author link
 */
public class Servidor {
    // Datos de servidor
    private final int puerto = 6666; // El puerto de la conexión hacia el infierno Socket to Hell!!
    private ServerSocket servidorControl = null;
    private Socket cliente = null;
    
    private boolean salir=false;
    
// Patron Singleton -> Unsa sola instancia
    private static Servidor servidor;

    private Servidor() {
        //this.comp = new Compartido();
    }

    public static Servidor getInstance() {
        if (servidor == null) {
            servidor = new Servidor();
        }
        return servidor;
    }
    
    public void iniciarControl(){
        // Prparamos conexion
        prepararConexion();
        // Trabajamos con ella
        tratarConexion();
        //menuPrincipal();
         // Cerramos la conexion
        cerrarConexion();
    }
    
    /**
     * Prepara la conexión del servidor
     */
     private void prepararConexion() {
        try {
            // Nos anunciamos como servidorControl
            servidorControl = new ServerSocket(this.puerto);
            System.out.println("Servidor->Listo. Esperando cliente...");
        } catch (IOException ex) {
            System.err.println("Servidor->ERROR: apertura de puerto " + ex.getMessage());
            System.exit(-1);
        }
    }
     
     /**
      * Cerramos las conexión del servidor
      */
    private void cerrarConexion() {
        try {
            // Cerramos el cliente y el servidorControl
            cliente.close();
            servidorControl.close();
            System.out.println("Servidor->Cerrando la conexión");
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Servidor->ERROR: Cerrar Conexiones" + ex.getMessage());
        }
    }
    
    /**
     * tratamos la conexión con el cliente
     */
    private void tratarConexion() {
        // Escuchamos hasta aburrirnos, es decir, hasta que salgamos
        while (!salir) {
            //Aceptamos la conexion
            aceptarConexion();
            // Procesamos el cliente
            procesarCliente();
        }
    }
    
    /**
     * Proecexamos el cliente con un hilo
     */
    private void procesarCliente() {
        System.out.println("Servidor->Iniciando sistema de control");
        ControlCliente gc = new ControlCliente(cliente);
        gc.start();
    }
    
    /**
     * Aceptamos la conexion
     */
    private void aceptarConexion() {
        // Aceptamos la petición
        try {
            cliente = servidorControl.accept();
            //comp.nuevoCliente();
            System.out.println("Servidor->Llega el cliente");
        } catch (IOException ex) {
            System.err.println("Servidor->ERROR: aceptar conexiones " + ex.getMessage());
        }
    }

    
    
    /**
     * Salimos del programa
     */
    private void salirPrograma() {
         this.salir = true;
         cerrar(0);
    }

    
    /**
     * Cerramos 
     * @param estado 
     */
    private void cerrar(int estado) {
        System.exit(estado);
    }
    
}
