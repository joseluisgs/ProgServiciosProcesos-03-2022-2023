/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextField;

/**
 *
 * @author link
 */
public class AccionEnviar implements ActionListener {

    private Socket socket;
    private JTextField tfMensaje;
    private String usuario;
    private DataOutputStream salidaDatos;

    public AccionEnviar(Socket socket, JTextField tfMensaje, String usuario) {
        this.socket = socket;
        this.tfMensaje = tfMensaje;
        this.usuario = usuario;
        try {
            this.salidaDatos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Error al crear el stream de salida : " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.err.println("El socket no se creo correctamente. ");
        }
    }
    
    // Metodo del evento relizado de manera manual
    // Es el evento que se dispara al pulsar el botón
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            salidaDatos.writeUTF(usuario + ": " + tfMensaje.getText());
            tfMensaje.setText("");
        } catch (IOException ex) {
            System.err.println("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
    }


}
