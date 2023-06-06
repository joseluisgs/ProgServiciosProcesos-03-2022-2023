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
public class PatronObserverHilos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TareaObserver observador = new TareaObserver();
        TareaObservable observable = new TareaObservable();

	observable.addObserver(observador);

	Thread hilo = new Thread(observable);

	hilo.start();

    }
    
}
