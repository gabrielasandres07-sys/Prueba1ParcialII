/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author USER
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import modelo.Analizador;
import modelo.ArchivoException;
import modelo.Bitacora;
import modelo.Configuracion;
import modelo.Estadisticas;
import modelo.FiltroBusqueda;
import modelo.GestorArchivos;
import modelo.NodoArchivo;
import modelo.Recorrido;
import modelo.RutaInvalidaException;
import modelo.Utilidades;
import vista.VentanaPrincipal;

public class Controlador implements ActionListener {

    private VentanaPrincipal vista = null;
    private Bitacora bitacora = new Bitacora("bitacora.log");
    private Configuracion config = new Configuracion("config.txt");
    private Analizador analizador = new Analizador();
    private GestorArchivos gestor = new GestorArchivos(bitacora);

    private Recorrido recorrido = null;
    private Timer timer = null;
    private Estadisticas ultimasEstadisticas = null;
    private File archivoEditor = null;

    public Controlador(VentanaPrincipal vista) {
        this.vista = vista;
        vista.setControlador(this);
        cargarConfiguracion();

        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });

        timer = new Timer(200, e -> actualizarProgreso());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            switch (e.getActionCommand()) {
                case "EXAMINAR":
                    examinar();
                    break;
                case "ARBOL":
                case "ANALIZAR":
                case "BUSCAR":
                case "DUPLICADOS":
                    iniciarTrabajo(e.getActionCommand());
                    break;
                case "CANCELAR":
                    cancelar();
                    break;
                case "NUEVA_CARPETA":
                    nuevaCarpeta();
                    break;
                case "NUEVO_ARCHIVO":
                    nuevoArchivo();
                    break;
                case "RENOMBRAR":
                    renombrar();
                    break;
                case "COPIAR":
                    copiarOMover(false);
                    break;
                case "MOVER":
                    copiarOMover(true);
                    break;
                case "ELIMINAR":
                    eliminar();
                    break;
                case "ABRIR":
                    abrirEditor();
                    break;
                case "GUARDAR":
                    guardarEditor();
                    break;
                case "EXPORTAR":
                    exportarReporte();
                    break;
                case "BITACORA":
                    verBitacora();
                    break;
            }
        } catch (RutaInvalidaException | ArchivoException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado: " + ex.getMessage());
        }
    }

    private void examinar() {
        File carpeta = vista.elegirCarpeta("Seleccione el directorio raiz");
        if (carpeta != null) {
            vista.setRuta(carpeta.getAbsolutePath());
        }
    }

    private void iniciarTrabajo(String tipo) throws RutaInvalidaException {
        File raiz = Utilidades.validarRuta(vista.getRuta());
        FiltroBusqueda filtro = null;
        if (tipo.equals("BUSCAR")) {
            filtro = crearFiltro();
        }
        guardarConfiguracion();

        recorrido = new Recorrido(vista.getProfundidad(), vista.isOcultos(), bitacora);
        bitacora.registrar(tipo, raiz.getAbsolutePath());
        vista.setTrabajando(true, "Iniciando...");
        timer.start();

        Trabajo trabajo = new Trabajo(tipo, raiz, filtro, recorrido);
        trabajo.execute();
    }

    private FiltroBusqueda crearFiltro() {
        boolean todoVacio = vista.getTexto().trim().isEmpty() && vista.getExtension().trim().isEmpty()
                && vista.getMinKB().trim().isEmpty() && vista.getMaxKB().trim().isEmpty()
                && vista.getDesde().trim().isEmpty() && vista.getHasta().trim().isEmpty();
        if (todoVacio) {
            throw new IllegalArgumentException("Escriba un texto de busqueda o llene al menos un filtro");
        }
        return new FiltroBusqueda(vista.getTexto(), vista.getExtension(), vista.getMinKB(),
                vista.getMaxKB(), vista.getDesde(), vista.getHasta());
    }

    private void cancelar() {
        if (recorrido != null) {
            recorrido.cancelar();
            vista.setEstado("Cancelando...");
        }
    }

    private void actualizarProgreso() {
        if (recorrido != null) {
            vista.setEstado("Procesados: " + recorrido.getProcesados() + "   " + recorrido.getRutaActual());
        }
    }

    private void terminarTrabajo(String mensaje) {
        timer.stop();
        vista.setTrabajando(false, mensaje);
    }

    private class Trabajo extends SwingWorker<Object, Void> {
        private String tipo;
        private File raiz;
        private FiltroBusqueda filtro;
        private Recorrido rec;

        public Trabajo(String tipo, File raiz, FiltroBusqueda filtro, Recorrido rec) {
            this.tipo = tipo;
            this.raiz = raiz;
            this.filtro = filtro;
            this.rec = rec;
        }

        @Override
        protected Object doInBackground() {
            if (tipo.equals("ARBOL")) {
                return analizador.construirArbol(raiz, rec);
            } else if (tipo.equals("ANALIZAR")) {
                return analizador.calcularEstadisticas(raiz, rec);
            } else if (tipo.equals("BUSCAR")) {
                return analizador.buscar(raiz, filtro, rec);
            } else {
                return analizador.buscarDuplicados(raiz, rec);
            }
        }

        @Override
        protected void done() {
            try {
                Object resultado = get();
                if (rec.isCancelado()) {
                    terminarTrabajo("Operacion cancelada. Procesados: " + rec.getProcesados());
                    bitacora.registrar("CANCELADO_" + tipo, raiz.getAbsolutePath());
                    vista.mostrarMensaje("Operacion cancelada por el usuario");
                    return;
                }
                mostrarResultado(tipo, resultado);
                String mensaje = "Terminado. Elementos procesados: " + rec.getProcesados();
                if (rec.getSinPermiso() > 0) {
                    mensaje += "   Carpetas sin permiso omitidas: " + rec.getSinPermiso();
                }
                terminarTrabajo(mensaje);
            } catch (InterruptedException | ExecutionException ex) {
                terminarTrabajo("Error");
                vista.mostrarError("Error durante el recorrido: " + ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void mostrarResultado(String tipo, Object resultado) {
        if (tipo.equals("ARBOL")) {
            vista.mostrarArbol((NodoArchivo) resultado);
        } else if (tipo.equals("ANALIZAR")) {
            ultimasEstadisticas = (Estadisticas) resultado;
            vista.mostrarEstadisticas(ultimasEstadisticas);
        } else if (tipo.equals("BUSCAR")) {
            vista.mostrarResultados((ArrayList<File>) resultado);
        } else {
            vista.mostrarDuplicados((HashMap<String, ArrayList<File>>) resultado);
        }
    }

    private File getSeleccion() {
        File seleccion = null;
        if (vista.estaEnPestanaBusqueda()) {
            seleccion = vista.getSeleccionTabla();
        }
        if (seleccion == null) {
            seleccion = vista.getSeleccionArbol();
        }
        return seleccion;
    }

    private File getSeleccionObligatoria() throws ArchivoException {
        File seleccion = getSeleccion();
        if (seleccion == null) {
            throw new ArchivoException("Primero seleccione un archivo o carpeta en el arbol o en la tabla");
        }
        if (!seleccion.exists()) {
            throw new ArchivoException("El elemento ya no existe, vuelva a cargar el arbol");
        }
        return seleccion;
    }

    private File getCarpetaDestino() throws RutaInvalidaException {
        File seleccion = getSeleccion();
        if (seleccion == null) {
            return Utilidades.validarRuta(vista.getRuta());
        }
        if (seleccion.isDirectory()) {
            return seleccion;
        }
        return seleccion.getParentFile();
    }

    private void recargarArbol() throws RutaInvalidaException {
        iniciarTrabajo("ARBOL");
    }

    private void nuevaCarpeta() throws RutaInvalidaException, ArchivoException {
        File padre = getCarpetaDestino();
        String nombre = vista.pedirTexto("Nombre de la carpeta (puede usar a/b/c para anidadas)\nSe creara en: "
                + padre.getAbsolutePath(), "");
        if (nombre == null) {
            return;
        }
        File nueva = gestor.crearCarpeta(padre, nombre);
        vista.mostrarMensaje("Carpeta creada:\n" + nueva.getAbsolutePath());
        recargarArbol();
    }

    private void nuevoArchivo() throws RutaInvalidaException, ArchivoException {
        File carpeta = getCarpetaDestino();
        String[] datos = vista.pedirNuevoArchivo();
        if (datos == null) {
            return;
        }
        gestor.validarNombre(datos[0]);
        File destino = new File(carpeta, datos[0].trim());
        boolean sobrescribir = false;
        if (destino.exists()) {
            if (!vista.confirmar("El archivo " + destino.getName() + " ya existe.\nDesea sobrescribirlo?")) {
                return;
            }
            sobrescribir = true;
        }
        File nuevo = gestor.crearArchivo(carpeta, datos[0], datos[1], sobrescribir);
        vista.mostrarMensaje("Archivo creado:\n" + nuevo.getAbsolutePath());
        recargarArbol();
    }

    private void renombrar() throws ArchivoException, RutaInvalidaException {
        File file = getSeleccionObligatoria();
        String nombre = vista.pedirTexto("Nuevo nombre para " + file.getName() + ":", file.getName());
        if (nombre == null) {
            return;
        }
        gestor.renombrar(file, nombre);
        recargarArbol();
    }

    private void copiarOMover(boolean mover) throws ArchivoException, RutaInvalidaException {
        File origen = getSeleccionObligatoria();
        String accion = "copiar";
        if (mover) {
            accion = "mover";
        }
        File carpeta = vista.elegirCarpeta("Carpeta destino para " + accion + " " + origen.getName());
        if (carpeta == null) {
            return;
        }
        File destino = new File(carpeta, origen.getName());
        boolean sobrescribir = false;
        if (destino.exists()) {
            if (!vista.confirmar(destino.getName() + " ya existe en el destino.\nDesea sobrescribirlo?")) {
                return;
            }
            sobrescribir = true;
        }
        if (mover) {
            gestor.mover(origen, carpeta, sobrescribir);
        } else {
            gestor.copiar(origen, carpeta, sobrescribir);
        }
        vista.mostrarMensaje("Listo: " + destino.getAbsolutePath());
        recargarArbol();
    }

    private void eliminar() throws ArchivoException, RutaInvalidaException {
        File file = getSeleccionObligatoria();
        File raiz = Utilidades.validarRuta(vista.getRuta());
        if (file.getAbsolutePath().equals(raiz.getAbsolutePath())) {
            throw new ArchivoException("No se puede eliminar el directorio raiz");
        }
        String tipo = "el archivo";
        if (file.isDirectory()) {
            tipo = "la carpeta (y todo su contenido)";
        }
        if (!vista.confirmar("Seguro que desea eliminar " + tipo + ":\n" + file.getAbsolutePath()
                + "\n\nEsta accion no se puede deshacer.")) {
            return;
        }
        gestor.eliminar(file);
        vista.mostrarMensaje("Eliminado correctamente");
        recargarArbol();
    }

    private void abrirEditor() throws ArchivoException {
        File file = getSeleccionObligatoria();
        if (file.isDirectory()) {
            throw new ArchivoException("Seleccione un archivo, no una carpeta");
        }
        if (gestor.esMuyGrande(file)) {
            vista.mostrarError("El archivo es demasiado grande para el editor ("
                    + Utilidades.tamanoLegible(file.length()) + "). El maximo es 2 MB.");
            return;
        }
        if (!gestor.esTexto(file)) {
            vista.mostrarError("El archivo no parece ser de texto, no se puede abrir en el editor");
            return;
        }
        String contenido = gestor.leerTexto(file);
        archivoEditor = file;
        vista.mostrarEditor(file, contenido);
    }

    private void guardarEditor() throws ArchivoException {
        if (archivoEditor == null) {
            throw new ArchivoException("No hay ningun archivo abierto");
        }
        if (!vista.confirmar("Se sobrescribira el archivo:\n" + archivoEditor.getAbsolutePath() + "\nDesea continuar?")) {
            return;
        }
        gestor.guardarTexto(archivoEditor, vista.getTextoEditor(), "GUARDAR");
        vista.mostrarMensaje("Archivo guardado");
    }

    private void exportarReporte() throws ArchivoException {
        if (ultimasEstadisticas == null) {
            throw new ArchivoException("Primero calcule las estadisticas");
        }
        File file = vista.elegirArchivoReporte();
        if (file == null) {
            return;
        }
        String nombre = file.getName().toLowerCase();
        if (!nombre.endsWith(".txt") && !nombre.endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }
        if (file.exists() && !vista.confirmar("El archivo ya existe. Desea sobrescribirlo?")) {
            return;
        }
        String contenido;
        if (file.getName().toLowerCase().endsWith(".csv")) {
            contenido = ultimasEstadisticas.reporteCsv();
        } else {
            contenido = ultimasEstadisticas.reporteTxt();
        }
        gestor.guardarTexto(file, contenido, "EXPORTAR_REPORTE");
        vista.mostrarMensaje("Reporte guardado en:\n" + file.getAbsolutePath());
    }

    private void verBitacora() {
        try {
            vista.mostrarBitacora(bitacora.leer());
        } catch (IOException e) {
            vista.mostrarError("No se pudo leer la bitacora: " + e.getMessage());
        }
    }

    private void cargarConfiguracion() {
        try {
            config.cargar();
        } catch (IOException e) {
            vista.mostrarError("No se pudo leer config.txt");
        }
        vista.setRuta(config.get("ruta", ""));
        vista.setTexto(config.get("texto", ""));
        vista.setExtension(config.get("extension", ""));
        vista.setMinKB(config.get("minKB", ""));
        vista.setMaxKB(config.get("maxKB", ""));
        vista.setDesde(config.get("desde", ""));
        vista.setHasta(config.get("hasta", ""));
        vista.setOcultos(config.get("ocultos", "false").equals("true"));
        try {
            vista.setProfundidad(Integer.parseInt(config.get("profundidad", "0")));
        } catch (NumberFormatException e) {
            vista.setProfundidad(0);
        }
    }

    private void guardarConfiguracion() {
        config.set("ruta", vista.getRuta());
        config.set("texto", vista.getTexto());
        config.set("extension", vista.getExtension());
        config.set("minKB", vista.getMinKB());
        config.set("maxKB", vista.getMaxKB());
        config.set("desde", vista.getDesde());
        config.set("hasta", vista.getHasta());
        config.set("profundidad", "" + vista.getProfundidad());
        config.set("ocultos", "" + vista.isOcultos());
        try {
            config.guardar();
        } catch (IOException e) {
            vista.mostrarError("No se pudo guardar config.txt");
        }
    }

    private void cerrar() {
        if (recorrido != null) {
            recorrido.cancelar();
        }
        guardarConfiguracion();
        System.exit(0);
    }
}
