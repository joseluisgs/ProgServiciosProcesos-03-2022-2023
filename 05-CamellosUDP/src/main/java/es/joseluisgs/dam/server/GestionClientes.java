/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author link
 */
public class GestionClientes extends Thread {

    private int idCamello;
    private Servidor servidor;
    private DatagramPacket p;
    private DatagramSocket socket;

    public GestionClientes(Servidor s, DatagramPacket p, int idCammello) {
        this.servidor = s;
        this.p = p;
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.idCamello = idCammello;
    }

    @Override
    public void run() {
        byte[] buffer, buffer2 = new byte[1];
        int puerto, longitud;
        InetAddress dir;

        buffer = p.getData(); //obtengo datos
        puerto = p.getPort(); //obtengo puerto origen
        dir = p.getAddress(); //obtengo dir IP
        longitud = p.getLength(); //longitud del mensaje

        try {
            //Empieza la carrera
            String nombres = servidor.getNombresJinetes();
            byte[] bufferEmpezar = nombres.getBytes();
            DatagramPacket pEmpezar = new DatagramPacket(bufferEmpezar, nombres.length(), dir, puerto);
            socket.send(pEmpezar);

            DatagramPacket p2;
            do {
                //Realizamos un avance
                byte avance = avanzar();
                servidor.realizarAvance(idCamello, avance);
                System.out.println(idCamello + " y realizo avance.");
                //Le enviamos al cliente los avances de todos los jinetes.
                byte[] avances = new byte[5];
                copiarArray(avances, servidor.getAvances());
                avances[4] = 0;
                p2 = new DatagramPacket(avances, 5, dir, puerto);
                socket.send(p2);
                System.out.println(idCamello + "  y envio a cliente.");
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } while (!servidor.isFin());
            System.out.println(idCamello + " acabo de enviar.");

            //Esto lo hacemos para filtrar el mensaje.
            byte[] posicionesFinales = new byte[5];
            copiarArray(posicionesFinales, servidor.getPosicionesFinales(idCamello));
            posicionesFinales[4] = -1;
            //Enviamos las posiciones finales de los jinetes.
            p2 = new DatagramPacket(posicionesFinales, 5, dir, puerto);
            socket.send(p2);
            //System.out.println("MI TRABAJO AQUÍ HA TERMINADO. " + idCamello);
        } catch (IOException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public byte avanzar() {
        int n = (int) (Math.random() * 100);
        byte pasos = 0;
        if (n <= 15) {
            pasos = 1;
        } else {
            if (n > 15 && n <= 30) {
                pasos = 2;
            } else {
                if (n > 30 && n <= 40) {
                    pasos = 3;
                } else {
                    if (n > 40 && n <= 48) {
                        pasos = 4;
                    } else {
                        if (n > 48 && n <= 56) {
                            pasos = 5;
                        } else {
                            if (n > 56 && n <= 63) {
                                pasos = 6;
                            } else {
                                if (n > 63 && n <= 70) {
                                    pasos = 7;
                                } else {
                                    if (n > 70 && n <= 80) {
                                        pasos = 8;
                                    } else {
                                        if (n > 80 && n <= 90) {
                                            pasos = 9;
                                        } else {
                                            if (n > 90) {
                                                pasos = 10;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return pasos;
    }

    public void copiarArray(byte[] a, byte[] b) {
        for (int i = 0; i < b.length; i++) {
            a[i] = b[i];
        }
    }

}
