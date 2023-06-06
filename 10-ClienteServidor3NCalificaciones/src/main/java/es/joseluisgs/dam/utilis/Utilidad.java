/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam.utilis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author link
 */
public class Utilidad {
    private static Utilidad util = null;
    
    public static Utilidad nuevaInstancia() {
        if(util ==null){
            util= new Utilidad();
        }
        return util;
    }
    
    private Utilidad() {
        
    }
    
    // Patrones de texto
    
    public boolean emailValido(String email) {
        String regExpn =
             "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                 +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                   +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                   +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                   +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                   +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();
    }
    
    public boolean telefonoValido(String telefono) {
        String regExpn = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{3})$";

        CharSequence inputStr = telefono;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();
    }
    
    public boolean precioValido(String precio) {
        String regExpn = "\\d*\\.?\\d*";

        CharSequence inputStr = precio;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();
    }
    
    public boolean notaValida(String nota) {
        String regExpn = "^[0-9]+(\\.[0-9]{1,2})?$";

        CharSequence inputStr = nota;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();
    }
    
    public String convertirSHA256(String password) {
	MessageDigest md = null;
	try {
            md = MessageDigest.getInstance("SHA-256");
	} 
	catch (NoSuchAlgorithmException e) {		
            e.printStackTrace();
            return null;
	}
	    
	byte[] hash = md.digest(password.getBytes());
	StringBuffer sb = new StringBuffer();
	    
	for(byte b : hash) {        
		sb.append(String.format("%02x", b));
	}
	    
	return sb.toString();
    }
    
}
