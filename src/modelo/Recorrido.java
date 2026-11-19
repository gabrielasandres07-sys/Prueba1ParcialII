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
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public class Recorrido {
    private int profundidadMax = 0;
    private boolean incluirOcultos = false;
    private Bitacora bitacora = null;
    private HashSet<String> visitadas = new HashSet<>();
    private volatile int procesados = 0;
    private int sinPermiso = 0;
    private volatile String rutaActual = "";
    private volatile boolean cancelado = false;

    public Recorrido(int profundidadMax, boolean incluirOcultos, Bitacora bitacora) {
        this.profundidadMax = profundidadMax;
        this.incluirOcultos = incluirOcultos;
        this.bitacora = bitacora;
    }

    public boolean puedeBajar(int profundidad) {
        return profundidadMax == 0 || profundidad < profundidadMax;
    }

    public boolean incluir(File file) {
        return incluirOcultos || !file.isHidden();
    }

    public boolean puedeEntrar(File carpeta) {
        if (Files.isSymbolicLink(carpeta.toPath())) {
            bitacora.registrar("OMITIDO_ENLACE", carpeta.getAbsolutePath());
            return false;
        }
        try {
            return visitadas.add(carpeta.getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

    public File[] listar(File carpeta) {
        File[] hijos = carpeta.listFiles();
        if (hijos == null) {
            sinPermiso++;
            bitacora.registrar("OMITIDA_SIN_PERMISO", carpeta.getAbsolutePath());
            return new File[0];
        }
        return hijos;
    }

    public void avisar(File file) {
        procesados++;
        rutaActual = file.getAbsolutePath();
    }

    public void cancelar() {
        cancelado = true;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public int getProcesados() {
        return procesados;
    }

    public String getRutaActual() {
        return rutaActual;
    }

    public int getSinPermiso() {
        return sinPermiso;
    }
}
