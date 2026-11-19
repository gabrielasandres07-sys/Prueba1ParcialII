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
import java.util.ArrayList;

public class Estadisticas {
    public static final String[] CATEGORIAS = {".txt", ".java", ".pdf", "Otros"};

    private File raiz = null;
    private int[] cantidades = new int[4];
    private long[] tamanos = new long[4];
    private int totalArchivos = 0;
    private long tamanoTotal = 0;
    private File masGrande = null;
    private File masReciente = null;
    private int profundidadMax = 0;
    private int numCarpetas = 0;
    private ArrayList<File> carpetasVacias = new ArrayList<>();
    private int sinPermiso = 0;

    public Estadisticas(File raiz) {
        this.raiz = raiz;
    }

    public static int getCategoria(File file) {
        String ext = Utilidades.getExtension(file);
        if (ext.equals("txt")) {
            return 0;
        } else if (ext.equals("java")) {
            return 1;
        } else if (ext.equals("pdf")) {
            return 2;
        }
        return 3;
    }

    public void agregarArchivo(File file, int profundidad) {
        int cat = getCategoria(file);
        cantidades[cat]++;
        tamanos[cat] += file.length();
        totalArchivos++;
        tamanoTotal += file.length();

        if (masGrande == null || file.length() > masGrande.length()) {
            masGrande = file;
        }
        if (masReciente == null || file.lastModified() > masReciente.lastModified()) {
            masReciente = file;
        }
        if (profundidad > profundidadMax) {
            profundidadMax = profundidad;
        }
    }

    public void agregarCarpeta(File carpeta, int profundidad, boolean vacia) {
        numCarpetas++;
        if (vacia) {
            carpetasVacias.add(carpeta);
        }
        if (profundidad > profundidadMax) {
            profundidadMax = profundidad;
        }
    }

    public double getPorcentaje(int cat) {
        if (totalArchivos == 0) {
            return 0;
        }
        return cantidades[cat] * 100.0 / totalArchivos;
    }

    public String reporteTxt() {
        String texto = "REPORTE DE ESTADISTICAS\n";
        texto += "Fecha y hora: " + Utilidades.fechaActual() + "\n";
        texto += "Carpeta: " + raiz.getAbsolutePath() + "\n\n";
        for (int i = 0; i < CATEGORIAS.length; i++) {
            texto += CATEGORIAS[i] + ": " + cantidades[i] + " archivos, "
                    + Utilidades.tamanoLegible(tamanos[i]) + ", "
                    + String.format("%.2f", getPorcentaje(i)) + "%\n";
        }
        texto += "\nTotal de archivos: " + totalArchivos + "\n";
        texto += "Tamano total: " + Utilidades.tamanoLegible(tamanoTotal) + "\n";
        texto += "Archivo mas grande: " + getRuta(masGrande) + "\n";
        texto += "Archivo mas reciente: " + getRuta(masReciente) + "\n";
        texto += "Profundidad maxima: " + profundidadMax + "\n";
        texto += "Numero de carpetas: " + numCarpetas + "\n";
        texto += "Carpetas vacias: " + carpetasVacias.size() + "\n";
        texto += "Carpetas sin permiso: " + sinPermiso + "\n";
        return texto;
    }

    public String reporteCsv() {
        String texto = "Fecha;" + Utilidades.fechaActual() + "\n";
        texto += "Carpeta;" + raiz.getAbsolutePath() + "\n";
        texto += "Categoria;Cantidad;Tamano;Porcentaje\n";
        for (int i = 0; i < CATEGORIAS.length; i++) {
            texto += CATEGORIAS[i] + ";" + cantidades[i] + ";"
                    + Utilidades.tamanoLegible(tamanos[i]) + ";"
                    + String.format("%.2f", getPorcentaje(i)) + "\n";
        }
        texto += "Total archivos;" + totalArchivos + "\n";
        texto += "Tamano total;" + Utilidades.tamanoLegible(tamanoTotal) + "\n";
        texto += "Mas grande;" + getRuta(masGrande) + "\n";
        texto += "Mas reciente;" + getRuta(masReciente) + "\n";
        texto += "Profundidad maxima;" + profundidadMax + "\n";
        texto += "Carpetas;" + numCarpetas + "\n";
        texto += "Carpetas vacias;" + carpetasVacias.size() + "\n";
        return texto;
    }

    private String getRuta(File file) {
        if (file == null) {
            return "Ninguno";
        }
        return file.getAbsolutePath();
    }

    public File getRaiz() {
        return raiz;
    }

    public int getCantidad(int cat) {
        return cantidades[cat];
    }

    public long getTamano(int cat) {
        return tamanos[cat];
    }

    public int getTotalArchivos() {
        return totalArchivos;
    }

    public long getTamanoTotal() {
        return tamanoTotal;
    }

    public File getMasGrande() {
        return masGrande;
    }

    public File getMasReciente() {
        return masReciente;
    }

    public int getProfundidadMax() {
        return profundidadMax;
    }

    public int getNumCarpetas() {
        return numCarpetas;
    }

    public ArrayList<File> getCarpetasVacias() {
        return carpetasVacias;
    }

    public int getSinPermiso() {
        return sinPermiso;
    }

    public void setSinPermiso(int sinPermiso) {
        this.sinPermiso = sinPermiso;
    }
}
