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
public class Refresco implements Serializable {
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Refresco(){
        switch((int) (Math.random() * 7)){
            case 0: nombre = "Coca Cola";
            break;
            case 1: nombre = "Sprite";
            break;
            case 2: nombre = "Fanta";
            break;
            case 3: nombre = "Tónica";
            break;
            case 4: nombre = "Nestea";
            break;
            case 5: nombre = "Cerveza";
            break;
            case 6: nombre = "Zumo";
            break;
            default: nombre = "Agua";
        }
    }
}
