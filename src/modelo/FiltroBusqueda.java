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
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FiltroBusqueda {
    private String texto = "";
    private String extension = "";
    private long minimo = -1;
    private long maximo = -1;
    private long desde = -1;
    private long hasta = -1;

    public FiltroBusqueda(String texto, String extension, String minKB, String maxKB,
                          String fechaDesde, String fechaHasta) {
        this.texto = texto.trim().toLowerCase();
        this.extension = extension.trim().toLowerCase().replace(".", "");

        if (this.texto.matches(".*[\\\\/:*?\"<>|].*")) {
            throw new IllegalArgumentException("El texto de busqueda tiene caracteres no validos");
        }
        if (!minKB.trim().isEmpty()) {
            minimo = leerKB(minKB);
        }
        if (!maxKB.trim().isEmpty()) {
            maximo = leerKB(maxKB);
        }
        if (minimo != -1 && maximo != -1 && minimo > maximo) {
            throw new IllegalArgumentException("El tamano minimo no puede ser mayor que el maximo");
        }
        if (!fechaDesde.trim().isEmpty()) {
            desde = leerFecha(fechaDesde);
        }
        if (!fechaHasta.trim().isEmpty()) {
            hasta = leerFecha(fechaHasta) + 24L * 60 * 60 * 1000 - 1;
        }
        if (desde != -1 && hasta != -1 && desde > hasta) {
            throw new IllegalArgumentException("La fecha desde no puede ser mayor que la fecha hasta");
        }
    }

    private long leerKB(String valor) {
        double kb;
        try {
            kb = Double.parseDouble(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El tamano debe ser un numero: " + valor);
        }
        if (kb < 0) {
            throw new IllegalArgumentException("El tamano no puede ser negativo");
        }
        return (long) (kb * 1024);
    }

    private long leerFecha(String valor) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        try {
            return formato.parse(valor.trim()).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fecha no valida: " + valor + " (use dd/MM/yyyy)");
        }
    }

    public boolean cumple(File file) {
        String nombre = file.getName().toLowerCase();
        if (!texto.isEmpty() && !nombre.contains(texto)) {
            return false;
        }
        if (!extension.isEmpty()) {
            String ext = Utilidades.getExtension(file);
            if (extension.equals("-")) {
                if (!ext.isEmpty()) {
                    return false;
                }
            } else if (!ext.equals(extension)) {
                return false;
            }
        }
        if (minimo != -1 && file.length() < minimo) {
            return false;
        }
        if (maximo != -1 && file.length() > maximo) {
            return false;
        }
        if (desde != -1 && file.lastModified() < desde) {
            return false;
        }
        if (hasta != -1 && file.lastModified() > hasta) {
            return false;
        }
        return true;
    }
}
