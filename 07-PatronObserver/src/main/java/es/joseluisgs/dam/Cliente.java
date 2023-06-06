/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Clase Cliente
 * En este caso es la clase que observa (Observer)
 * Observa los cambios que se produzcan en otra clase, a la que se conoce como Observable
 */
public class Cliente implements Observer {

	private String codigo;
	private String nombre;
	private String apellidos;
	private String email;
	private Date fechaNacimiento;
	
	public Cliente() {}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	/**
	 * Método que se ejecuta cuando se producen cambios
	 * en la clase observada
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		if (arg.equals("stock"))
			System.out.println("El cliente ha sido notificado de un cambio en el stock");
	}
}
