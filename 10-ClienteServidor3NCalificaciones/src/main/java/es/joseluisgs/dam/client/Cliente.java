/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import es.joseluisgs.dam.model.Alumno;
import es.joseluisgs.dam.utilis.Utilidad;
import org.apache.commons.lang3.SerializationUtils;


/**
 *
 * @author link
 */
public class Cliente {

    private final int puerto = 6666; // El puerto de la conexión hacia el infierno Socket to Hell!!
    private InetAddress direccion;
    private Socket servidor;
    private boolean salir = false;
    private boolean conectado = false;
    private long TOKEN = -99; // Token de conexion

    // Opciones
    private static final int IDENTIFICAR = 1;
    private static final int SALIR = 2;
    private static final int LISTAR = 3;
    private static final int APROBADOS = 4;
    private static final int SUSPENSOS = 5;
    private static final int MEDIA = 6;
    private static final int DATOS = 7;
    private static final int INSERTAR = 8;

    DataInputStream datoEntrada = null;
    DataOutputStream datoSalida = null;

    public void iniciar() {
        // Antes de nada compruebo la direccion
        comprobarDireccion();
        // Sacamos el menún principal
        menuPrincipal();

    }

    /**
     * Comprueba la dirección hacia el servidor
     */
    private void comprobarDireccion() {
        try {
            // Consigo la dirección
            direccion = InetAddress.getLocalHost(); // dirección local (localhost)
        } catch (UnknownHostException ex) {
            System.err.println("Cliente->ERROR: No encuetra dirección del servidor " + ex.getMessage());
            conectado = false;
            salir = true;
            System.exit(-1);
        }
    }

    /**
     * Conectamos al menún principal
     */
    private void conectarServidor() {
        try {
            // Me conecto
            servidor = new Socket(direccion, puerto);
            datoEntrada = new DataInputStream(servidor.getInputStream());
            datoSalida = new DataOutputStream(servidor.getOutputStream());
            System.out.println("Cliente->Conectado al servidor...");
            this.conectado = true;
        } catch (IOException ex) {
            System.err.println("Cliente->ERROR: Al conectar al servidor " + ex.getMessage());
            conectado = false;
            salir = true;
            System.exit(-1);
        }
    }

    /**
     * Desconectamos del servidor
     *
     */
    private void desconectarServidor() {
        try {
            // Me desconecto
            servidor.close();
            datoEntrada.close();
            datoSalida.close();
            System.out.println("Cliente->Desconectado");
            conectado = false;
            salir = true;
        } catch (IOException ex) {
            System.err.println("Cliente->ERROR: Al desconectar al servidor " + ex.getMessage());
            conectado = false;
            salir = true;
            System.exit(-1);
        }
    }

    /**
     * Menú principal a tratar
     */
    private void menuPrincipal() {
        System.out.println("Bienvenidos a NotaPSP");
        System.out.println("---------------------");
        System.out.println("1.- Identificarse");
        System.out.println("2.- Salir");

        int opcion;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("Inidique opción: ");
            opcion = sc.nextInt();
        } while (opcion < 1 || opcion > 2);

        tratarMenuPrincipal(opcion);

    }

    /**
     * Tratamiento del menún principal
     *
     * @param opcion
     */
    private void tratarMenuPrincipal(int opcion) {
        // según la pción
        switch (opcion) {
            case 1:
                identificarUsuario();
                break;
            case 2:
                salirPrograma();
                break;
        }
    }

    /**
     * Menú secundario
     */
    private void menuSecundario() {
        System.out.println("NotaPSP: Opciones");
        System.out.println("-----------------");
        System.out.println("1.- Listar calificaciones");
        System.out.println("2.- Número de Aprobados");
        System.out.println("3.- Número de Suspensos");
        System.out.println("4.- Calificación Media");
        System.out.println("5.- Datos de alumno");
        System.out.println("6.- Insertar de alumno");
        System.out.println("7.- Salir");

        int opcion;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("Inidique opción: ");
            opcion = sc.nextInt();
        } while (opcion < 1 || opcion > 7);

        tratarMenuSecundario(opcion);

    }

    /**
     * Tratar menú secundario
     *
     * @param opcion
     */
    private void tratarMenuSecundario(int opcion) {
        // según la pción
        switch (opcion) {
            case 1:
                listarAlumnos();
                break;
            case 2:
                numAprobados();
                break;
            case 3:
                numSuspensos();
                break;
            case 4:
                notaMedia();
                break;
            case 5:
                datosAlumno();
                break;
            case 6:
                insertarAlumno();
                break;
            case 7:
                salirPrograma();
                break;
        }
        this.menuSecundario();
    }

    /**
     * Indica que queremos salir del programa
     */
    private void salirPrograma() {
        this.salir = true;
        if (TOKEN > 0) {
            // Enviamos al servidor que queremos desconectar
            System.out.println("Cliente->Solicitando Salir");
            // Nos conectamos al servidor
            this.conectarServidor();
            // Salir es la opción 2
            try {
                datoSalida.writeInt(SALIR);
                cerrar(0);
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: Al enviar petición de Salir " + ex.getMessage());
                cerrar(1);
            }
        }
    }

    /**
     * Cierra el programa
     *
     * @param opcion
     */
    private void cerrar(int opcion) {
        if (conectado) {
            this.desconectarServidor();
        }
        System.out.println("Cliente: Fin de programa");
        System.exit(opcion);
    }

    /**
     * Identificar usuario
     */
    private void identificarUsuario() {
        // La usuario admin@admin.com password: 1234
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduzca su email:");
        String email = sc.nextLine();
        System.out.println("Introduzca su password:");
        String password = Utilidad.nuevaInstancia().convertirSHA256(sc.nextLine().toString()); // Ciframos

        // Nos conectamos al servidor
        this.conectarServidor();
        // Si estamos conectados pasamos a identificarnos
        if (this.conectado) {
            System.out.println("Cliente->Solicitando Identificarse");
            try {
                // Indico la opción
                datoSalida.writeInt(IDENTIFICAR);

                // Mando el mail y el password
                datoSalida.writeUTF(email);
                datoSalida.writeUTF(password);

                // Recibimos la respuesta
                boolean correcto = datoEntrada.readBoolean();
                if (correcto) {
                    // Recibimos el token de conexion
                    TOKEN = datoEntrada.readLong();
                    this.menuSecundario();
                } else {
                    System.out.println("Cliente: No se ha podido identificar");
                    //cerramos la conexion
                    this.menuPrincipal();
                }
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo identificarme " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No se ha podido identificar");
            //cerramos la conexion
            this.menuPrincipal();
        }
        this.desconectarServidor();

    }

    /**
     * Número de aprobados
     */
    private void numAprobados() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(APROBADOS);
                int aprobados = datoEntrada.readInt();
                System.out.println("Cliente: El número de alumnos/as aprobados es: " + aprobados);
                // Desconectamos
                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo obtener el numero de aprobados " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

    /**
     * Número de suspensos
     */
    private void numSuspensos() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(SUSPENSOS);
                int suspensos = datoEntrada.readInt();
                System.out.println("Cliente: El número de alumnos/as suspensos es: " + suspensos);
                // Desconectamos
                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo obtener el numero de suspensos " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

    /**
     * Nota media
     */
    private void notaMedia() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(MEDIA);
                float media = datoEntrada.readFloat();
                System.out.println("Cliente: La nota media es: " + media);
                // Desconectamos
                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo obtener la nota media " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

    /**
     * Muestra los datos del alumno
     */
    private void datosAlumno() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(DATOS);
                System.out.println("Cliente: Introduzca el nombre del alumno:");
                Scanner sc = new Scanner(System.in);
                String nombre = sc.nextLine();
                datoSalida.writeUTF(nombre);
                // Desconectamos
                int l = datoEntrada.readInt();
                if (l > 0) {
                    byte[] b = new byte[l];
                    datoEntrada.read(b);
                    Alumno a = (Alumno) SerializationUtils.deserialize(b);
                    System.out.println("ID: " + a.getId());
                    System.out.println("Nombre: " + a.getNombre());
                    System.out.println("Calificación: " + a.getCalificacion());
                } else {
                    System.out.println("Cliente: No existe datos de alumno/a");
                }

                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo obtener alumno " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

    /**
     * Lista los datos de todos los alumnos
     */
    private void listarAlumnos() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(LISTAR);
                // Si queremos filtrar por nombre descomentar
                //System.out.println("Cliente: Introduzca el nombre del alumno:");
                //Scanner sc = new Scanner(System.in);
                String nombre = "";//sc.nextLine();
                datoSalida.writeUTF(nombre);
                // Obtenemos el numero de registros
                int r = datoEntrada.readInt();
                if (r > 0) {
                    ArrayList<Alumno> lista = new ArrayList<Alumno>();
                    for (int i = 0; i < r; i++) {
                        int l = datoEntrada.readInt();
                        byte[] b = new byte[l];
                        datoEntrada.read(b);
                        Alumno a = (Alumno) SerializationUtils.deserialize(b);
                        // podría imprimirlo directamente, ¿pero y si lo vamos a usar para otra cosa?
                        // Me quedo con su lista
                        lista.add(a);
                    }
                    // Recorro la lista
                    for (Alumno a : lista) {
                        System.out.println("ID: " + a.getId());
                        System.out.println("Nombre: " + a.getNombre());
                        System.out.println("Calificación: " + a.getCalificacion());
                        System.out.println("\n");
                    }

                } else {
                    System.out.println("Cliente: No existe datos de alumno/a");
                }

                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo listar alumnos " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

    private void insertarAlumno() {
        if (TOKEN > 0) {
            // Nos conectamos al servidor
            this.conectarServidor();
            try {
                // Indico la opción de aprobados
                datoSalida.writeInt(INSERTAR);
                Scanner sc = new Scanner(System.in);
                System.out.println("Cliente: Introduzca el nombre del alumno:");
                String nombre = sc.nextLine();
                float calificacion = 0;
                do {
                    System.out.println("Cliente: Introduzca su calificacion");
                    calificacion = sc.nextFloat();
                } while (calificacion < 0 || calificacion > 10);
                
                
                //Vamos a enviar los datos sin crear el objeto alumno
                // Aunque también podríamos creralo y mandarlo como lo hemos hecho antes
                datoSalida.writeUTF(nombre);
                datoSalida.writeFloat(calificacion);
                // Esperamos la confirmacion
                int r = 0;
                r = datoEntrada.readInt();
                if (r>=0) {
                    System.out.println("Cliente: Alumno insertado con éxito");
                } else {
                    System.out.println("Cliente: Alumno no insertado");
                    // Recojo el error
                    String error = datoEntrada.readUTF();
                    System.out.println("Cliente: " + error);
                }

                this.desconectarServidor();
            } catch (IOException ex) {
                System.err.println("Cliente->ERROR: No puedo insertar aulumno " + ex.getMessage());
            }
        } else {
            System.out.println("Cliente: No tiene una sesión abierta");
            this.menuPrincipal();
        }
    }

}
