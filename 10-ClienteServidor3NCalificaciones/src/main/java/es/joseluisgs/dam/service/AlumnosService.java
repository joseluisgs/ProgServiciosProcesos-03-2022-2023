/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import es.joseluisgs.dam.database.DataBaseController;
import es.joseluisgs.dam.model.Alumno;
import es.joseluisgs.dam.utilis.Utilidad;


/**
 *
 * @author link
 */
public class AlumnosService {
    // Patron Singleton -> Unsa sola instancia
    private static AlumnosService service;

    private AlumnosService() {
        //this.comp = new Compartido();
    }

    public static AlumnosService getInstance() {
        if (service == null) {
            service = new AlumnosService();
        }
        return service;
    }
    
    public ArrayList<Alumno> listarAlumnos(String filtro) {
        ArrayList<Alumno> lista = new ArrayList<Alumno>();
        DataBaseController c = DataBaseController.getInstance();
        try {
            c.open();
            Optional<ResultSet> rs = c.select("Select * from alumnos  where nombre like \"%"+filtro+"%\" order by id");
            c.close();
            if(rs.isPresent()) {
                while (rs.get().next()) {
                    Alumno a = new Alumno(
                            rs.get().getInt("id"),
                            rs.get().getString("nombre"),
                            rs.get().getFloat("calificacion"));
                    lista.add(a);
                }
            }
        } catch (SQLException ex) {
            System.err.println("ControladorAlumno->Error al listar " +ex.getMessage());
        }
        
        return lista;
        
    }
    
    public Alumno datosAlumno(String filtro) {
        ArrayList<Alumno> lista = this.listarAlumnos(filtro);
        if(lista.isEmpty())
            return null;
        else
            return this.listarAlumnos(filtro).get(0);
        
    }
    
    public int numAprobados(String filtro) {
        DataBaseController c = DataBaseController.getInstance();
        try {
            c.open();
            Optional<ResultSet> rs = c.select("Select count(id) as aprobados from alumnos  where nombre like \"%"+filtro+"%\" and calificacion>=5");
            c.close();
            if(rs.isPresent()) {
                while (rs.get().next())
                    return rs.get().getInt("aprobados");
            }
               
        } catch (SQLException ex) {
            System.err.println("ControladorAlumno->Error al listar " +ex.getMessage());
            return 0;
        }
        return 0;
    }
    
    public int numSuspensos(String filtro) {
        DataBaseController c = DataBaseController.getInstance();

        try {
            c.open();
            Optional<ResultSet> rs = c.select("Select count(id) as suspensos from alumnos  where nombre like \"%"+filtro+"%\" and calificacion<5");
            c.close();
            if(rs.isPresent()) {
                while (rs.get().next())
                    return rs.get().getInt("suspensos");

            }
               
        } catch (SQLException ex) {
            System.err.println("ControladorAlumno->Error al listar " +ex.getMessage());
            return 0;
        }
        return 0;
    }
    
    public float media(String filtro) {
        DataBaseController c = DataBaseController.getInstance();

        try {
            c.open();
            Optional<ResultSet> rs = c.select("Select avg(calificacion) as media from alumnos  where nombre like \"%"+filtro+"%\"");
            c.close();
            if(rs.isPresent()) {
                while (rs.get().next())
                    return rs.get().getFloat("media");
            }
               
        } catch (SQLException ex) {
            System.err.println("ControladorAlumno->Error al listar " +ex.getMessage());
            return 0;
        }
        return 0;
    }
    
    public int insertarAlumno(String nombre, float calificacion){
        // Habria que comprbar que no existe antes y bla bla bla bla ba
       if(Utilidad.nuevaInstancia().notaValida(Float.toString(calificacion))){
           DataBaseController c = DataBaseController.getInstance();
           try {
               c.open();
               String query = "INSERT INTO alumnos VALUES (null, ?, ?)";
               ResultSet res = c.insert(query, nombre, calificacion).orElseThrow(() -> new SQLException("Error AlumnosService al insertar Alumno"));
               c.close();
               if (res.first()) {
                   return (int) res.getLong(1);
               }
           } catch (SQLException ex) {
               System.err.println("AlumnoService->Error al insertar " +ex.getMessage());
           }
       }
       return -1;
    }

}
