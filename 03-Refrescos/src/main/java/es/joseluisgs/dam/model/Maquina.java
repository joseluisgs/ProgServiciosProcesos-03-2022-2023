/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author link
 */
public class Maquina implements Serializable{
     private ArrayList<Refresco> listaRefrescos;
    
    public Maquina(){
        listaRefrescos = new ArrayList<Refresco>();
    }
    
    public synchronized void add(Refresco r){
        listaRefrescos.add(r);
    }
    
    public synchronized Refresco get(int i){
        return listaRefrescos.get(i);
    }
    
    public synchronized Refresco remove(int i){
        return listaRefrescos.remove(i);
    }
    
    public synchronized Maquina removeAll(){
        Maquina lista = new Maquina();
        while(listaRefrescos.size() > 0){
            lista.add(listaRefrescos.remove(0));
        }
        return lista;
    }
    
    public synchronized int size(){
        return listaRefrescos.size();
    }
}
