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
import java.util.HashMap;

public class Configuracion {
    private File file = null;
    private HashMap<String, String> datos = new HashMap<>();

    public Configuracion(String dir) {
        file = new File(dir);
    }

    public String get(String clave, String porDefecto) {
        if (datos.containsKey(clave)) {
            return datos.get(clave);
        }
        return porDefecto;
    }

    public void set(String clave, String valor) {
        if (valor == null) {
            valor = "";
        }
        datos.put(clave, valor.trim());
    }

    public void cargar() throws IOException {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                int igual = linea.indexOf('=');
                if (igual > 0) {
                    datos.put(linea.substring(0, igual), linea.substring(igual + 1));
                }
            }
        }
    }

    public void guardar() throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            for (String clave : datos.keySet()) {
                fw.write(clave + "=" + datos.get(clave) + "\n");
            }
        }
    }
}
