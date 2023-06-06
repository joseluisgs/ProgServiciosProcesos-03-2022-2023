/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 *
 * @author link
 */
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastUDP {
  public static void main(String args[]) {
      String mensaje = "Hola soy el miembro del grupo->";
    
    for(int i=0;i<5;i++){
        try {
            Thread.sleep(300);
            GestionCliente gc = new GestionCliente((mensaje+i));
            gc.start();
        } catch (InterruptedException ex) {
            Logger.getLogger(MulticastUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
  }
}
