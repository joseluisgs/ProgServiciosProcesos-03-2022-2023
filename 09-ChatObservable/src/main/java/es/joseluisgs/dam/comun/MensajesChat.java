/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.comun;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author link
 */
public class MensajesChat extends Observable{

    private String mensaje;
    
    public MensajesChat(){
    }
    
    public String getMensaje(){
        return mensaje;
    }
    
    public void setMensaje(String mensaje){
        this.mensaje = mensaje;
        // Indica que el mensaje (objeto) ha cambiado dentro del patrón
        this.setChanged();
        // Notifica a los observadores que el mensaje ha cambiado y se lo pasa
        // (Internamente notifyObservers llama al metodo update del observador)
        // Es un patron de diseño para este tipo de aplicaciones. 
        // Notificamos a cada observador pasándole el mensaje
        this.notifyObservers(this.getMensaje());
    }
    
        /**
	 * Método que permite añadir observadores de esta clase
         * En este caso no es obligatorio, porque no lo cambiamos
         * y lo heredamod
	 */
         /*
	@Override
	public void addObserver(Observer observer) {
		this.observer = observer;
	}
        */
    
    
    
    
}