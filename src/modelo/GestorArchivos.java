/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class GestorArchivos {
    public static final long MAX_EDITOR = 2 * 1024 * 1024;

    private Bitacora bitacora = null;

    public GestorArchivos(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    public void validarNombre(String nombre) throws ArchivoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoException("El nombre no puede estar vacio");
        }
        if (nombre.matches(".*[\\\\/:*?\"<>|].*")) {
            throw new ArchivoException("El nombre no puede tener: \\ / : * ? \" < > |");
        }
        if (nombre.endsWith(".") || nombre.endsWith(" ")) {
            throw new ArchivoException("El nombre no puede terminar en punto o espacio");
        }
        String base = nombre.split("\\.")[0].toUpperCase();
        if (base.matches("CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9]")) {
            throw new ArchivoException("Nombre reservado por Windows: " + nombre);
        }
    }

    public File crearCarpeta(File padre, String nombre) throws ArchivoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoException("El nombre no puede estar vacio");
        }
        for (String parte : nombre.trim().split("[/\\\\]+")) {
            validarNombre(parte);
        }
        File file = new File(padre, nombre.trim());
        if (file.exists()) {
            throw new ArchivoException("Ya existe: " + file.getName());
        }
        if (!file.mkdirs()) {
            throw new ArchivoException("No se pudo crear la carpeta");
        }
        bitacora.registrar("CREAR_CARPETA", file.getAbsolutePath());
        return file;
    }

    public File crearArchivo(File carpeta, String nombre, String contenido, boolean sobrescribir)
            throws ArchivoException {
        validarNombre(nombre);
        File file = new File(carpeta, nombre.trim());
        if (file.exists() && !sobrescribir) {
            throw new ArchivoException("Ya existe un archivo con ese nombre");
        }
        escribir(file, contenido);
        bitacora.registrar("CREAR_ARCHIVO", file.getAbsolutePath());
        return file;
    }

    public File renombrar(File file, String nuevoNombre) throws ArchivoException {
        validarNombre(nuevoNombre);
        File nuevo = new File(file.getParentFile(), nuevoNombre.trim());
        if (nuevo.exists()) {
            throw new ArchivoException("Ya existe un elemento con ese nombre");
        }
        if (!file.renameTo(nuevo)) {
            throw new ArchivoException("No se pudo renombrar");
        }
        bitacora.registrar("RENOMBRAR", file.getAbsolutePath() + " -> " + nuevo.getName());
        return nuevo;
    }

    public File copiar(File origen, File carpetaDestino, boolean sobrescribir) throws ArchivoException {
        File destino = prepararDestino(origen, carpetaDestino, sobrescribir);
        try {
            copiarRecursivo(origen, destino);
        } catch (IOException e) {
            throw new ArchivoException("Error al copiar: " + e.getMessage());
        }
        bitacora.registrar("COPIAR", origen.getAbsolutePath() + " -> " + destino.getAbsolutePath());
        return destino;
    }

    public File mover(File origen, File carpetaDestino, boolean sobrescribir) throws ArchivoException {
        File destino = prepararDestino(origen, carpetaDestino, sobrescribir);
        if (!origen.renameTo(destino)) {
            try {
                copiarRecursivo(origen, destino);
            } catch (IOException e) {
                throw new ArchivoException("Error al mover: " + e.getMessage());
            }
            borrarRecursivo(origen);
        }
        bitacora.registrar("MOVER", origen.getAbsolutePath() + " -> " + destino.getAbsolutePath());
        return destino;
    }

    public void eliminar(File file) throws ArchivoException {
        if (!file.exists()) {
            throw new ArchivoException("El elemento no existe");
        }
        if (!borrarRecursivo(file)) {
            throw new ArchivoException("No se pudo eliminar");
        }
        bitacora.registrar("ELIMINAR", file.getAbsolutePath());
    }

    public boolean esMuyGrande(File file) {
        return file.length() > MAX_EDITOR;
    }

    public boolean esTexto(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[1024];
            int leidos = in.read(bytes);
            for (int i = 0; i < leidos; i++) {
                if (bytes[i] == 0) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String leerTexto(File file) throws ArchivoException {
        String texto = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
            texto = sb.toString();
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer el archivo: " + e.getMessage());
        }
        bitacora.registrar("ABRIR", file.getAbsolutePath());
        return texto;
    }

    public void guardarTexto(File file, String contenido, String accion) throws ArchivoException {
        escribir(file, contenido);
        bitacora.registrar(accion, file.getAbsolutePath());
    }

    private void escribir(File file, String contenido) throws ArchivoException {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(contenido);
        } catch (IOException e) {
            throw new ArchivoException("No se pudo escribir: " + e.getMessage());
        }
    }

    private File prepararDestino(File origen, File carpetaDestino, boolean sobrescribir)
            throws ArchivoException {
        if (!origen.exists()) {
            throw new ArchivoException("El archivo de origen no existe");
        }
        if (!carpetaDestino.isDirectory()) {
            throw new ArchivoException("El destino debe ser una carpeta");
        }
        File destino = new File(carpetaDestino, origen.getName());
        try {
            String rutaOrigen = origen.getCanonicalPath();
            String rutaDestino = destino.getCanonicalPath();
            if (rutaDestino.equals(rutaOrigen) || rutaDestino.startsWith(rutaOrigen + File.separator)) {
                throw new ArchivoException("No se puede copiar o mover una carpeta dentro de si misma");
            }
        } catch (IOException e) {
            throw new ArchivoException("Ruta no valida");
        }
        if (destino.exists()) {
            if (!sobrescribir) {
                throw new ArchivoException("Ya existe en el destino: " + destino.getName());
            }
            borrarRecursivo(destino);
        }
        return destino;
    }

    private void copiarRecursivo(File origen, File destino) throws IOException {
        if (origen.isDirectory()) {
            destino.mkdirs();
            File[] hijos = origen.listFiles();
            if (hijos == null) {
                throw new IOException("Sin permiso para leer " + origen.getName());
            }
            for (File hijo : hijos) {
                copiarRecursivo(hijo, new File(destino, hijo.getName()));
            }
        } else {
            copiarBytes(origen, destino);
        }
    }

    private void copiarBytes(File origen, File destino) throws IOException {
        try (FileInputStream in = new FileInputStream(origen);
             FileOutputStream out = new FileOutputStream(destino)) {
            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
        }
    }

    private boolean borrarRecursivo(File file) {
        if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
            File[] hijos = file.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    borrarRecursivo(hijo);
                }
            }
        }
        return file.delete();
    }
}

