/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.model;

import java.io.Serializable;

/**
 *
 * @author link
 */
public class Ejemplo implements Serializable { //La clase que vamos a enviar por el socket tendremos que
                                               //serializarla, por lo tanto tenemos que porner este implements.
    
    private String cad;
    private int n;
    
    public Ejemplo(String cad, int n){
        this.cad=cad;
        this.n=n;
    }
    
    public Ejemplo(){
        cad="hola";
        n=9;
    }

    @Override
    public String toString() {
        return "Ejemplo: " + "cad=" + cad + ", n=" + n + '}';
    }
    
    
    
    public void mostrar(){
        System.out.println("Cadena: " + cad);
        System.out.println("Número: " + n);
    }
}
