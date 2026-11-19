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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Bitacora {
    private File file = null;

    public Bitacora(String dir) {
        file = new File(dir);
    }

    public synchronized void registrar(String accion, String ruta) {
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(Utilidades.fechaActual() + " | " + accion + " | " + ruta + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo escribir en la bitacora");
        }
    }

    public synchronized String leer() throws IOException {
        if (!file.exists()) {
            return "La bitacora esta vacia";
        }
        String texto = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                texto += linea + "\n";
            }
        }
        return texto;
    }
}
