/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Observable;
import java.util.Observer;

/**
 * Clase Producto
 * Es la clase Observable. Los cambios que se produzcan serán
 * observados inmediatamente por las clases Observer que se registren
 * desde ésta
*/

public class Producto extends Observable {

	private String nombre;
	private String descripcion;
	private float precio;
	private int stock;
	
	/*
	 *  Único observador
	 *  En el caso de que hubiera varios, podrían guardarse
	 *  como colección
	 */
	private Observer observer;
	
	public Producto() {}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
		
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
		notifyObservers();
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
		if (observer != null) {
			observer.update(this, "stock");
		}

	}
}
