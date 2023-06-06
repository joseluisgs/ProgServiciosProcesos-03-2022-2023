/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 *
 * @author link
 */
public class GestionCliente extends Thread {
    private String mensaje;
    private String direccion;

    public GestionCliente(String mensaje) {
        this.mensaje = mensaje;
        this.direccion = "127.0.0.1";
    }
    
     @Override
    public void run(){
        try { 
      InetAddress grupo = InetAddress.getByName(direccion);
      MulticastSocket socket = new MulticastSocket(6789);

      // Se une al grupo
      socket.joinGroup(grupo);

      // Envia el mensaje
      byte[] m = mensaje.getBytes();
      DatagramPacket mensajeSalida = new DatagramPacket(m, m.length, grupo, 6789);
      socket.send(mensajeSalida);

      byte[] bufer = new byte[1000];
      String linea;

      // Se queda a la espera de mensajes al grupo, hasta recibir "Adios"
      while (true) {
	DatagramPacket mensajeEntrada = new DatagramPacket(bufer, bufer.length);
	socket.receive(mensajeEntrada);
	linea = new String(mensajeEntrada.getData(), 0, mensajeEntrada.getLength());
	System.out.println("Recibido:" + linea);
	if (linea.equals("Adios")) break;
      }

      // Si recibe "Adios" abandona el grupo
      socket.leaveGroup(grupo);
    } catch (SocketException e) {
      System.out.println("Socket:" + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO:" + e.getMessage());
    }
    }
    
}
