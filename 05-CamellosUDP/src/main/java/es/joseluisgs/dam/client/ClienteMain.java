/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.client;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author link
 */
public class ClienteMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cliente cliente1=new Cliente("Cliente 1");
        cliente1.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Cliente cliente2=new Cliente("Cliente 2");
        cliente2.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Cliente cliente3=new Cliente("Cliente 3");
        cliente3.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Cliente cliente4=new Cliente("Cliente 4");
        cliente4.start();
    }
    
}
