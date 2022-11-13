/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Observable;
import java.util.Observer;

/**
 * Clase TareaObservable
 * Es la clase Observable. Los cambios que se produzcan serán
 * observados inmediatamente por las clases Observer que se registren
 * desde ésta

 */
public class TareaObservable extends Observable implements Runnable {

	private int valor;
	private boolean funcionando;
	
	/*
	 *  Único observador
	 *  En el caso de que hubiera varios, podrían guardarse
	 *  como colección
	 */
	private Observer observer;
	
	public TareaObservable() {
		
		funcionando = true;
		valor = 0;
	}
	
	public void terminar() {
		
		funcionando = false;
	}

    public int getValor() {
        return valor;
    }

	@Override
	public void run() {
		
		while (funcionando) {
			
			valor++;
			notifyObservers();
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {}
		}
	}
	
	/**
	 * Método que permite añadir observadores de esta clase
	 */
	@Override
	public void addObserver(Observer observer) {
		this.observer = observer;
	}
	
	/**
	 * Método que notifica los cambios a los observadores
	 * de esta clase
	 */
	@Override
	public void notifyObservers() {
		if (observer != null)
			observer.update(this, "valor");
	}
}
