/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;

import es.joseluisgs.dam.model.Alumno;
import es.joseluisgs.dam.service.AccesoService;
import es.joseluisgs.dam.service.AlumnosService;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author link
 */
public class ControlCliente extends Thread {

    private Socket cliente = null;
    private DataInputStream datoEntrada = null;
    private DataOutputStream datoSalida = null;
    private boolean salir = false;
    private long TOKEN = -99; // Token de conexion

    // Opciones
    public static final int IDENTIFICAR = 1;
    public static final int SALIR = 2;
    private static final int LISTAR = 3;
    private static final int APROBADOS = 4;
    private static final int SUSPENSOS = 5;
    private static final int MEDIA = 6;
    private static final int DATOS = 7;
    private static final int INSERTAR = 8;

    public ControlCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public void run() {
        // Trabajamos con ella
        // Para vosotros intentra hacerlo con un while y vereís que pasa ¿Por qué?
        if (salir == false) {
            crearFlujosES();
            tratarConexion();
            cerrarFlujosES();
        } else {
            this.interrupt(); // Me interrumpo y no trabajo
        }
    }

    private void salir() {
        // Cerramos el flujo si salimos
        cerrarFlujosES();
        System.out.println("ServidorGC->Saliendo");
        salir = true;
        this.interrupt();
    }

    /**
     * Creamos los flujos de intercambio
     */
    private void crearFlujosES() {
        try {
            datoEntrada = new DataInputStream(cliente.getInputStream());
            datoSalida = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: crear flujos de entrada y salida " + ex.getMessage());
            salir = true;
        }
    }

    /**
     * Cerramos lso flujos de intercambio
     */
    private void cerrarFlujosES() {
        try {
            datoEntrada.close();
            datoSalida.close();
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: cerrar flujos de entrada y salida " + ex.getMessage());
        }
    }

    /**
     * Tratamos la conexion
     */
    private void tratarConexion() {
        // Escuchamos hasta aburrirnos, es decir, hasta que salgamos
        try {
            // Procesamos la información
            // Por defecto leo mensajes cortos
            int opcion = datoEntrada.readInt();
            // según la pción
            switch (opcion) {
                case IDENTIFICAR:
                    identificarUsuario();
                    break;
                case SALIR:
                    desconectar();
                    break;
                case APROBADOS:
                    numAprobados();
                    break;
                case SUSPENSOS:
                    numSuspensos();
                    break;
                case MEDIA:
                    notaMedia();
                    break;
                case DATOS:
                    datosAlumno();
                    break;
                case LISTAR:
                    listarAlumnos();
                    break;
                case INSERTAR:
                    insertarAlumno();
                    break;
            }

        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: optener tipo de opcion " + ex.getMessage());
        }
    }
    
    /**
     * Desconecamos
     */
    private void desconectar() {
        this.salir = true;
        System.out.println("ServidorGC->Recibido Salir");
    }
    
    /**
     * Indetificamos al usuario
     */
    private void identificarUsuario() {
        try {
            System.out.println("ServidorGC->Procesando identificación");
            String email = datoEntrada.readUTF();
            String password = datoEntrada.readUTF();

            AccesoService a = AccesoService.getInstance();
            // Comprobamos si existe
            if (a.indetificarUsuario(email, password)) {
                datoSalida.writeBoolean(true);
                // Enviamos el token de conexion
                TOKEN = Instant.now().getEpochSecond();
                datoSalida.writeLong(TOKEN);
            } else {
                System.out.println("ServidorGC->Usuario o password incrrectos");
                datoSalida.writeBoolean(false);
            }

        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar identificar cliente " + ex.getMessage());
        }
    }
    
    /**
     * Obtenemos el número de aprobados
     */
    private void numAprobados() {
        System.out.println("ServidorGC->Procesando numero de aprobados");
        AlumnosService c = AlumnosService.getInstance();
        int aprobados = c.numAprobados("");
        try {
            // Lo mandamos
            datoSalida.writeInt(aprobados);
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar numero de aprobados " + ex.getMessage());
        }
    }
    
    /**
     * Obtenemos el número de suspensos
     */
    private void numSuspensos() {
        System.out.println("ServidorGC->Procesando numero de suspensos");
        AlumnosService c = AlumnosService.getInstance();
        int suspensos = c.numSuspensos("");
        try {
            // Lo mandamos
            datoSalida.writeInt(suspensos);
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar numero de suspensos " + ex.getMessage());
        }
    }
    
     /**
     * Obtenemos la nota media
     */
    private void notaMedia() {
        System.out.println("ServidorGC->Procesando nota media");
        AlumnosService c = AlumnosService.getInstance();
        float media = c.media("");
        try {
            // Lo mandamos
            datoSalida.writeFloat(media);
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar nota media " + ex.getMessage());
        }
    }
    
    /**
     * Obtenemos los datos de un alumno
     */
    private void datosAlumno() {
        System.out.println("ServidorGC->Procesando datos de alumno");
        String nombre;
        try {
            nombre = datoEntrada.readUTF();
            AlumnosService c = AlumnosService.getInstance();
            Alumno a = c.datosAlumno(nombre);
            byte[] b= null;
            if(a!=null){
                b = SerializationUtils.serialize(a);
                datoSalida.writeInt(b.length);
                datoSa     lida.write(b);
                // Alé ya lo hemos hecho. EstodatoSalida.write(b); es lo que hace JAVA
                // cuando a un objeto le dceimos que serializable y usamos
                //bufferObjetosSalida = new ObjectOutputStream(servidor.getOutputStream());
                // bufferObjetosSalida.writeObject();
            }else{
                datoSalida.writeInt(0);
            }
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar dato de alumno " + ex.getMessage());
        }
    }
        
        
    /**
     * Listar los datos de los alumnos
     */
    private void listarAlumnos() {
        System.out.println("ServidorGC->Procesando listado de alumnos");
        String nombre;
        try {
            // Filtro
            nombre = datoEntrada.readUTF();
            AlumnosService c = AlumnosService.getInstance();
            ArrayList<Alumno> lista = c.listarAlumnos(nombre);
            
            byte[] b= null;
            if(lista.size()>0){
                // Voy a mandar el tamaño para que lo sepan
                // Como las listas a veces pueden ser muy grandes voy a mandar uno a uno
                datoSalida.writeInt(lista.size());
                for(Alumno a: lista){
                    b= SerializationUtils.serialize(a);
                    datoSalida.writeInt(b.length);
                    datoSalida.write(b);
                }
                //System.out.println(a.toString());
                //b = SerializationUtils.serialize(a);
                //datoSalida.writeInt(b.length);
                //datoSalida.write(b);
                // Alé ya lo hemos hecho. EstodatoSalida.write(b); es lo que hace JAVA
                // cuando a un objeto le dceimos que serializable y usamos
                //bufferObjetosSalida = new ObjectOutputStream(servidor.getOutputStream());
                // bufferObjetosSalida.writeObject();
            }else{
                datoSalida.writeInt(0);
            }
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: procesar listados de alumnos " + ex.getMessage());
        }
        
        
        
    }

    private void insertarAlumno() {
        System.out.println("ServidorGC->Procesando insertar Alumno");
        
        try {
            String nombre = datoEntrada.readUTF();
            float calificacion = datoEntrada.readFloat();
            AlumnosService c = AlumnosService.getInstance();
            int res = c.insertarAlumno(nombre, calificacion);
            if(res>=0){
                datoSalida.writeInt(res);
            }
            else{
                String error="";
                if(res==-1){
                    error = "Nota Invalida";
                }
                datoSalida.writeUTF(error);
            }
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: insertar aumno " + ex.getMessage());
        }
    }

}
