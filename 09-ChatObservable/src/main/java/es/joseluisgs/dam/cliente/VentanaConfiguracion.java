/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.cliente;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author link
 */
public class VentanaConfiguracion extends JDialog{
    
    private JTextField tfUsuario;
    private JTextField tfHost;
    private JTextField tfPuerto;
    
    /**
     * Constructor de la ventana de configuracion inicial
     * 
     * @param padre Ventana padre
     */
    public VentanaConfiguracion(JFrame padre) {
        super(padre, "Configuracion inicial", true);
        
        JLabel lbUsuario = new JLabel("Usuario:");
        JLabel lbHost = new JLabel("Host:");
        JLabel lbPuerto = new JLabel("Puerto:");
        
        tfUsuario = new JTextField();
        tfHost = new JTextField("localhost");
        tfPuerto = new JTextField("5555");
        
        JButton btAceptar = new JButton("Aceptar");
        btAceptar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(20, 20, 0, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        c.add(lbUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(lbHost, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        c.add(lbPuerto, gbc);
        
        gbc.ipadx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        c.add(tfUsuario, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        c.add(tfHost, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        c.add(tfPuerto, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        c.add(btAceptar, gbc);
        
        this.pack(); // Le da a la ventana el minimo tamaño posible
        this.setLocation(450, 200); // Posicion de la ventana
        this.setResizable(false); // Evita que se pueda estirar la ventana
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Deshabilita el boton de cierre de la ventana 
        this.setVisible(true);
    }
    
    
    public String getUsuario(){
        return this.tfUsuario.getText();
    }
    
    public String getHost(){
        return this.tfHost.getText();
    }
    
    public int getPuerto(){
        return Integer.parseInt(this.tfPuerto.getText());
    }

}