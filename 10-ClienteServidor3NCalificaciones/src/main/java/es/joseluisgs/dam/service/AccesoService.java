/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import es.joseluisgs.dam.database.DataBaseController;
import es.joseluisgs.dam.utilis.Utilidad;


/**
 *
 * @author link
 */
public class AccesoService {
    // Patron Singleton -> Unsa sola instancia
    private static AccesoService controlador;

    private AccesoService() {
        //this.comp = new Compartido();
    }

    public static AccesoService getInstance() {
        if (controlador == null) {
            controlador = new AccesoService();
        }
        return controlador;
    }
    
    public boolean indetificarUsuario(String email, String password){
        Utilidad u = Utilidad.nuevaInstancia();
        String pass = null;
        if(u.emailValido(email)){
            DataBaseController bd = DataBaseController.getInstance();
            String consulta="Select password from usuarios where tipo ='admin' and email = '"+email+"'";
            try {
                bd.open();
                Optional<ResultSet> rs = bd.select(consulta);
                if(rs.isPresent()) {
                    while (rs.get().next()) {
                        pass = rs.get().getString("password");
                    }
                }
                bd.close();
            } catch (SQLException ex) {
                System.err.println("ControladorBD->"+ex.getMessage());
                return false;
            }
        }
        if(pass!=null && pass.equals(password)){
           return true;
        }else{
            return false;
        }
    }
    
}
