/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilidades {

    public static File validarRuta(String ruta) throws RutaInvalidaException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new RutaInvalidaException("La ruta no puede estar vacia");
        }
        File file = new File(ruta.trim());
        if (!file.exists()) {
            throw new RutaInvalidaException("La ruta no existe: " + ruta);
        }
        if (!file.isDirectory()) {
            throw new RutaInvalidaException("La ruta no es un directorio: " + ruta);
        }
        return file;
    }

    public static String tamanoLegible(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024) {
            return String.format("%.2f MB", mb);
        }
        return String.format("%.2f GB", mb / 1024.0);
    }

    public static String formatearFecha(long fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formato.format(new Date(fecha));
    }

    public static String fechaActual() {
        return formatearFecha(System.currentTimeMillis());
    }

    public static String getExtension(File file) {
        String nombre = file.getName();
        int punto = nombre.lastIndexOf('.');
        if (punto <= 0 || punto == nombre.length() - 1) {
            return "";
        }
        return nombre.substring(punto + 1).toLowerCase();
    }
}

