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

public class NodoArchivo {
    private File file = null;
    private ArrayList<NodoArchivo> hijos = new ArrayList<>();

    public NodoArchivo(File file) {
        this.file = file;
    }

    public void agregarHijo(NodoArchivo hijo) {
        hijos.add(hijo);
    }

    public File getFile() {
        return file;
    }

    public ArrayList<NodoArchivo> getHijos() {
        return hijos;
    }

    public boolean esCarpeta() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return file.getName();
    }
}
