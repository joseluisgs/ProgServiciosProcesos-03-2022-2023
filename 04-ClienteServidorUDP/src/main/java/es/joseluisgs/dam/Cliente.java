
package es.joseluisgs.dam;

/**
 *
 * @author link
 */
import java.net.*;
import java.io.*;

public class Cliente {

  // Los argumentos proporcionan el mensaje y el nombre del servidor
  public static void main(String args[]) {
      
      String men= "Hola Soy un cliente UDP";

    try {
      DatagramSocket socketUDP = new DatagramSocket();
      byte[] mensaje = men.getBytes();
      InetAddress hostServidor = InetAddress.getByName("localhost");
      int puertoServidor = 6789;

      // Construimos un datagrama para enviar el mensaje al servidor
      DatagramPacket peticion =
        new DatagramPacket(mensaje, men.length(), hostServidor,
                           puertoServidor);

      // Enviamos el datagrama
      socketUDP.send(peticion);

      // Construimos el DatagramPacket que contendrá la respuesta
      byte[] bufer = new byte[1000];
      DatagramPacket respuesta =
        new DatagramPacket(bufer, bufer.length);
      socketUDP.receive(respuesta);

      // Enviamos la respuesta del servidor a la salida estandar
      System.out.println("Respuesta: " + new String(respuesta.getData()).trim());

      // Cerramos el socket
      socketUDP.close();

    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    }
  }
}
