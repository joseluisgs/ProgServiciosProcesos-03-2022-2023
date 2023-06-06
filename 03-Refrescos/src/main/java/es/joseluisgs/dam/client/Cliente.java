/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.client;

import es.joseluisgs.dam.model.Maquina;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author link
 */
public class Cliente {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InetAddress direccion;
        Socket servidor;
        int puerto = 5555;
        System.out.println("¿Cuantos refrescos quieres?");
        Scanner sc = new Scanner(System.in);
        int numRefrescosPedir = sc.nextInt();

        try {
            direccion = InetAddress.getLocalHost(); // dirección local
            //direccion = InetAddress.getByName("172.17.209.114");
            servidor = new Socket(direccion, puerto);//equivalente al send
            System.out.println("Conectado a Servidor ...");

            //le envio el numero de refrescos que quiero
            DataOutputStream numRefrescos = new DataOutputStream(servidor.getOutputStream());
            numRefrescos.writeInt(numRefrescosPedir);

            //recibo un array con las lista de refrescos
            ObjectInputStream listaRe = new ObjectInputStream(servidor.getInputStream());
            Maquina listaRefrescos = (Maquina) listaRe.readObject();

            if (listaRefrescos.size() > 0) {
                for (int i = 0; i < listaRefrescos.size(); i++) {
                    System.out.println("Ha llegado un refresco de -> " + listaRefrescos.get(i).getNombre());
                }
            } else {
                System.out.println("Ya no hay más refrescos");
            }

            numRefrescos.close();
            listaRe.close();
            servidor.close();
            System.out.println("Desconectado ...");
        } catch (Exception e) {
            System.err.println("Servidor desconectado");
            //e.printStackTrace();
        }

    }
}
