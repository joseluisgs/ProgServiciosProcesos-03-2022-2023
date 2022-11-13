/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author link
 */
public class Cliente extends Thread {

    private boolean fin;

    public Cliente(String nombre) {
        this.setName(nombre);
        this.fin = false;
    }

    @Override
    public void run() {

        try {
            String mensaje;
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            //puerto origen del socket: el que encuentre libre.
            DatagramSocket s = new DatagramSocket();
            DatagramPacket p, p2;
            byte[] buffer2 = new byte[256];

            System.out.println("Puerto de origen reservado por el cliente: " + s.getLocalPort());

            //do{
            //System.out.print("Dame el nombre del jinete: ");
            //mensaje=teclado.readLine();
            mensaje = this.getName();
            // lo convierte a vector de bytes
            byte[] buffer = mensaje.getBytes();
            //ahora construyo el paquete, especifico destino
            //Ahora creamos el datagrama que será enviado por el socket 's'.
            p = new DatagramPacket(buffer, mensaje.length(), InetAddress.getLocalHost(), 5555);
            //p=new DatagramPacket(buffer, mensaje.length(), InetAddress.getByName("172.17.209.120"), 1600);

            s.send(p); //envio datagrama

            //Acuse de recibo
            int puerto, longitud;
            InetAddress dir;
            p2 = new DatagramPacket(buffer2, 256);
            s.receive(p2);
            buffer2 = p2.getData(); //obtengo datos
            puerto = p2.getPort(); //obtengo puerto origen
            dir = p2.getAddress(); //obtengo dir IP
            longitud = p2.getLength(); //longitud del mensaje
            mensaje = new String(buffer2, 0, longitud); //texto del mensaje
            //System.out.println("El servidor:" + dir + ":" + puerto + " >  ha confirmado el mensaje anterior con el mensaje: " + mensaje);

            if (mensaje.equals("aceptado")) {
                System.out.println("He entrado en la carrera.");
            } else {
                //Esto no llega a ejecutarse porque una vez entrados los 4 los demás se quedan 
                //esperando un mensaje.
                System.out.println("No he entrado en la carrera.");
            }
            
            byte[] bufferEmpezar = new byte[256];
            DatagramPacket pEmpezar = new DatagramPacket(bufferEmpezar, 256);
            s.receive(pEmpezar);
            mensaje=new String(pEmpezar.getData(), 0, pEmpezar.getLength());
            System.out.println("Empieza la carrera.");

            //Mostramos la ventana de la carrera
            ClienteVentanaCarrera v=new ClienteVentanaCarrera();
            v.setNombresJinetes(mensaje);
            v.setVisible(true);
            
            byte[] bufferPosiciones = new byte[5];
            do {
                p2 = new DatagramPacket(bufferPosiciones, 5, InetAddress.getLocalHost(), 1600);
                //p2=new DatagramPacket(bufferPosiciones, 5, InetAddress.getByName("172.17.209.9"), 1600);
                s.receive(p2);

                if (bufferPosiciones[4] != -1) {
                    //System.out.println("Posiciones: " + bufferPosiciones[0] + ", " + bufferPosiciones[1] + ", " + bufferPosiciones[2] + ", " + bufferPosiciones[3]);
                    v.avance(bufferPosiciones);
                } else {
                    //System.out.println("Posiciones finales: " + bufferPosiciones[0] + ", " + bufferPosiciones[1] + ", " + bufferPosiciones[2] + ", " + bufferPosiciones[3]);
                    v.setPosicionesFinales(bufferPosiciones);
                }
            } while (bufferPosiciones[4]!=-1);

        } catch (Exception e) {

        }
    }
}
