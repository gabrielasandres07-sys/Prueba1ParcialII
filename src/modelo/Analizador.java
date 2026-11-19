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
import java.util.HashMap;

public class Analizador {

    public NodoArchivo construirArbol(File raiz, Recorrido rec) {
        NodoArchivo nodo = new NodoArchivo(raiz);
        rec.puedeEntrar(raiz);
        llenarNodo(nodo, 0, rec);
        return nodo;
    }

    private void llenarNodo(NodoArchivo nodo, int profundidad, Recorrido rec) {
        if (!rec.puedeBajar(profundidad)) {
            return;
        }
        for (File hijo : rec.listar(nodo.getFile())) {
            if (rec.isCancelado()) {
                return;
            }
            if (!rec.incluir(hijo)) {
                continue;
            }
            rec.avisar(hijo);
            NodoArchivo nodoHijo = new NodoArchivo(hijo);
            nodo.agregarHijo(nodoHijo);
            if (hijo.isDirectory() && rec.puedeEntrar(hijo)) {
                llenarNodo(nodoHijo, profundidad + 1, rec);
            }
        }
    }

    public Estadisticas calcularEstadisticas(File raiz, Recorrido rec) {
        Estadisticas est = new Estadisticas(raiz);
        rec.puedeEntrar(raiz);
        contar(raiz, 0, rec, est);
        est.setSinPermiso(rec.getSinPermiso());
        return est;
    }

    private void contar(File carpeta, int profundidad, Recorrido rec, Estadisticas est) {
        if (!rec.puedeBajar(profundidad)) {
            return;
        }
        for (File hijo : rec.listar(carpeta)) {
            if (rec.isCancelado()) {
                return;
            }
            if (!rec.incluir(hijo)) {
                continue;
            }
            rec.avisar(hijo);
            if (hijo.isDirectory()) {
                String[] contenido = hijo.list();
                boolean vacia = contenido != null && contenido.length == 0;
                est.agregarCarpeta(hijo, profundidad + 1, vacia);
                if (rec.puedeEntrar(hijo)) {
                    contar(hijo, profundidad + 1, rec, est);
                }
            } else {
                est.agregarArchivo(hijo, profundidad + 1);
            }
        }
    }

    public ArrayList<File> buscar(File raiz, FiltroBusqueda filtro, Recorrido rec) {
        ArrayList<File> resultados = new ArrayList<>();
        rec.puedeEntrar(raiz);
        buscarEn(raiz, 0, filtro, rec, resultados);
        return resultados;
    }

    private void buscarEn(File carpeta, int profundidad, FiltroBusqueda filtro,
                          Recorrido rec, ArrayList<File> resultados) {
        if (!rec.puedeBajar(profundidad)) {
            return;
        }
        for (File hijo : rec.listar(carpeta)) {
            if (rec.isCancelado()) {
                return;
            }
            if (!rec.incluir(hijo)) {
                continue;
            }
            rec.avisar(hijo);
            if (hijo.isDirectory()) {
                if (rec.puedeEntrar(hijo)) {
                    buscarEn(hijo, profundidad + 1, filtro, rec, resultados);
                }
            } else if (filtro.cumple(hijo)) {
                resultados.add(hijo);
            }
        }
    }

    public HashMap<String, ArrayList<File>> buscarDuplicados(File raiz, Recorrido rec) {
        HashMap<String, ArrayList<File>> grupos = new HashMap<>();
        rec.puedeEntrar(raiz);
        agrupar(raiz, 0, rec, grupos);

        HashMap<String, ArrayList<File>> duplicados = new HashMap<>();
        for (String clave : grupos.keySet()) {
            if (grupos.get(clave).size() > 1) {
                duplicados.put(clave, grupos.get(clave));
            }
        }
        return duplicados;
    }

    private void agrupar(File carpeta, int profundidad, Recorrido rec,
                         HashMap<String, ArrayList<File>> grupos) {
        if (!rec.puedeBajar(profundidad)) {
            return;
        }
        for (File hijo : rec.listar(carpeta)) {
            if (rec.isCancelado()) {
                return;
            }
            if (!rec.incluir(hijo)) {
                continue;
            }
            rec.avisar(hijo);
            if (hijo.isDirectory()) {
                if (rec.puedeEntrar(hijo)) {
                    agrupar(hijo, profundidad + 1, rec, grupos);
                }
            } else {
                String clave = hijo.getName().toLowerCase() + " | " + hijo.length() + " bytes";
                if (!grupos.containsKey(clave)) {
                    grupos.put(clave, new ArrayList<>());
                }
                grupos.get(clave).add(hijo);
            }
        }
    }
}
