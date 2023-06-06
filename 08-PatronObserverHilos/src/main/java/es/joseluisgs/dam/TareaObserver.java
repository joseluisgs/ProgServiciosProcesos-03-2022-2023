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
import java.util.Observable;
import java.util.Observer;

/**
 * Clase TareaObserver
 * En este caso es la clase que observa (Observer)
 * Observa los cambios que se produzcan en otra clase, a la que se conoce como Observable
 */
public class TareaObserver implements Runnable, Observer {

	private int valor;
	private boolean funcionando;
	
	public TareaObserver() {
		
		funcionando = true;
		valor = 0;
	}
	
	public void terminar() {
		funcionando = false;
	}

	@Override
	public void run() {

	}
	
	/**
	 * Método que se ejecuta cuando se producen cambios
	 * en la clase observada
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		if (arg.equals("valor")) {
			System.out.println("El hilo observable ha modificado el valor a " + ((TareaObservable) o).getValor());
			valor = ((TareaObservable) o).getValor();
			if (valor == 10) {
				System.out.println("El hilo observable ha terminado");
				terminar();
				System.exit(0);
			}
		}
	}
}
