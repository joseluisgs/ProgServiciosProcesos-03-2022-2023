/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author link
 */
public class Servidor {

    private int numMaxJinetes;
    private ArrayList<DatagramPacket> jinetes;  //DatagramPackets de los jinetes.
    private byte contadorPosicionFinal;  //Dice cuál es el siguiente puesto.
    private byte[] posicionesFinales;   //Posiciones finales de los jinetes.
    private byte[] avances;  //Avances de los 4 jinetes.
    private int numJinetesListos;   //Número de jinetes que están listos para continuar.
    private int numJinetesAcabado;  //Número de jinetes que han acabado la carrera.
    private boolean fin;
    private String nombresJinetes;
    
    public void ejecutarServidor() {
        numMaxJinetes = 4;
        contadorPosicionFinal = 1;
        numJinetesAcabado = 0;
        nombresJinetes="";
        
        fin=false;
        
        posicionesFinales = new byte[4];
        posicionesFinales[0] = 0;
        posicionesFinales[1] = 0;
        posicionesFinales[2] = 0;
        posicionesFinales[3] = 0;

        avances = new byte[4];
        avances[0] = 0;
        avances[1] = 0;
        avances[2] = 0;
        avances[3] = 0;

        numJinetesListos = 0;

        try {
            jinetes = new ArrayList();

            System.out.println("Comienza la ejecucion del servidor. Escuchando...");

            byte[] buffer = new byte[256], buffer2;
            DatagramSocket s = new DatagramSocket(5555); // puerto de eco
            DatagramPacket p, p2;

            int puerto, longitud;
            InetAddress dir;
            String mensaje;

            //Esperamos los jinetes
            do {
                p = new DatagramPacket(buffer, 256);
                s.receive(p); //espero un datagrama, se queda esperando hasta que llega un datagrama.

                buffer = p.getData(); //obtengo datos
                puerto = p.getPort(); //obtengo puerto origen
                dir = p.getAddress(); //obtengo dir IP
                longitud = p.getLength(); //longitud del mensaje
                mensaje = new String(buffer, 0, longitud); //texto del mensaje
                this.nombresJinetes=this.nombresJinetes.concat(mensaje+",");
                System.out.println("Eco recibido:" + dir + ":" + puerto + " > ");

                //Lanzo acuse de recibo
                mensaje = "aceptado";
                // lo convierte a vector de bytes
                buffer2 = mensaje.getBytes();
                //ahora construyo el paquete, especifico destino
                //Ahora creamos el datagrama que será enviado por el socket 's'.
                p2 = new DatagramPacket(buffer2, mensaje.length(), dir, puerto);
                //DatagramPacket p=new DatagramPacket(buffer, mensaje.length(), InetAddress.getByAddress(buffer), 1050);
                //p2=new DatagramPacket(buffer2, mensaje.length(), InetAddress.getByName("172.17.209.9"), 1600);
                //DatagramPacket p=new DatagramPacket(buffer, mensaje.length(), InetAddress.getByName("fernando-Toshiba"),1050);
                s.send(p2); //envio datagrama 
                jinetes.add(p);
            } while (jinetes.size() < numMaxJinetes);

            //Empezamos los hilos que manejan los clientes.
            int i = 0;
            for (DatagramPacket dp : jinetes) {
                GestionClientes h = new GestionClientes(this, dp, i);
                h.start();
                i++;
            }

            //Durante la carrera
            do {
                synchronized (this) {
                    wait();
                }

                numJinetesListos = 0;

                synchronized (this) {
                    notifyAll();
                }
            } while (numJinetesAcabado < numMaxJinetes);

            fin=true;
            
            //Posiciones finales
            synchronized (this) {
                wait();
            }

            synchronized (this) {
                notifyAll();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Se le añade el avance al camello en el vector avances.
     *
     * @param posicion Posición del vector.
     * @param avance Cantidad del avance.
     */
    public synchronized void realizarAvance(int posicion, byte avance) {
        if ((this.avances[posicion] + avance) < 100) {
            this.avances[posicion] += avance;
        } else {
            if (this.avances[posicion] < 100) {
                this.avances[posicion] = 100;
                this.numJinetesAcabado++;
            }
        }
        numJinetesListos++;

        //Si todos los hilos están listos para continuar se despierta al servidor.
        if (numJinetesListos == numMaxJinetes) {
            notifyAll();
        } else {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Devuelve el vector con las avances de los camellos.
     *
     * @return
     */
    public synchronized byte[] getAvances() {
        return this.avances;
    }

    /**
     * Devuelve el vector con las posiciones finales de los jinetes.
     *
     * @param idCamello Id del jinete.
     * @return
     */
    public synchronized byte[] getPosicionesFinales(int idCamello) {
        this.posicionesFinales[idCamello] = contadorPosicionFinal;
        contadorPosicionFinal++;

        if (contadorPosicionFinal == numMaxJinetes+1) {
            notifyAll();
        } else {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.posicionesFinales;
    }

    public int getNumJinetesAcabado() {
        return this.numJinetesAcabado;
    }

    public int getNumMaxJinetes() {
        return this.numMaxJinetes;
    }

    public synchronized void aumentarNumJinetesAcabado() {
        this.numJinetesAcabado++;
    }
    
    public boolean isFin(){
        return this.fin;
    }
    
    public String getNombresJinetes(){
        return this.nombresJinetes;
    }
}
