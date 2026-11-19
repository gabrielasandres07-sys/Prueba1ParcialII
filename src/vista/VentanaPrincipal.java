/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author USER
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import modelo.Estadisticas;
import modelo.NodoArchivo;
import modelo.Utilidades;

public class VentanaPrincipal extends JFrame {

    private JTextField txtRuta = new JTextField(45);
    private JTextField txtTexto = new JTextField(12);
    private JTextField txtExtension = new JTextField(5);
    private JTextField txtMinKB = new JTextField(6);
    private JTextField txtMaxKB = new JTextField(6);
    private JTextField txtDesde = new JTextField(8);
    private JTextField txtHasta = new JTextField(8);
    private JSpinner spnProfundidad = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private JCheckBox chkOcultos = new JCheckBox("Incluir ocultos");

    private JButton btnExaminar = new JButton("Examinar...");
    private JButton btnArbol = new JButton("Cargar arbol");
    private JButton btnAnalizar = new JButton("Estadisticas");
    private JButton btnBuscar = new JButton("Buscar");
    private JButton btnDuplicados = new JButton("Duplicados");
    private JButton btnCancelar = new JButton("Cancelar");

    private JButton btnNuevaCarpeta = new JButton("Nueva carpeta");
    private JButton btnNuevoArchivo = new JButton("Nuevo archivo");
    private JButton btnRenombrar = new JButton("Renombrar");
    private JButton btnCopiar = new JButton("Copiar");
    private JButton btnMover = new JButton("Mover");
    private JButton btnEliminar = new JButton("Eliminar");
    private JButton btnAbrir = new JButton("Abrir en editor");

    private JButton btnExportar = new JButton("Exportar reporte");
    private JButton btnGuardar = new JButton("Guardar");
    private JButton btnBitacora = new JButton("Actualizar bitacora");

    private JProgressBar barra = new JProgressBar();
    private JLabel lblEstado = new JLabel("Listo");

    private JTree arbol = new JTree(new DefaultMutableTreeNode("Sin cargar"));
    private JTabbedPane pestanas = new JTabbedPane();

    private DefaultTableModel modeloEstadisticas;
    private JTextArea txtResumen = new JTextArea();

    private DefaultTableModel modeloResultados;
    private JTable tablaResultados;
    private JLabel lblResultados = new JLabel(" ");

    private JTextArea txtDuplicados = new JTextArea();

    private JLabel lblEditor = new JLabel("Ningun archivo abierto");
    private JTextArea txtEditor = new JTextArea();

    private JTextArea txtBitacora = new JTextArea();

    public VentanaPrincipal() {
        setTitle("Explorador y Gestor de Archivos");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        btnCancelar.setEnabled(false);
        btnGuardar.setEnabled(false);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Directorio raiz:"));
        fila1.add(txtRuta);
        fila1.add(btnExaminar);
        fila1.add(new JLabel("Profundidad (0 = ilimitada):"));
        fila1.add(spnProfundidad);
        fila1.add(chkOcultos);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Buscar:"));
        fila2.add(txtTexto);
        fila2.add(new JLabel("Extension:"));
        fila2.add(txtExtension);
        fila2.add(new JLabel("Min KB:"));
        fila2.add(txtMinKB);
        fila2.add(new JLabel("Max KB:"));
        fila2.add(txtMaxKB);
        fila2.add(new JLabel("Desde (dd/MM/yyyy):"));
        fila2.add(txtDesde);
        fila2.add(new JLabel("Hasta:"));
        fila2.add(txtHasta);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(btnArbol);
        fila3.add(btnAnalizar);
        fila3.add(btnBuscar);
        fila3.add(btnDuplicados);
        fila3.add(btnCancelar);
        barra.setPreferredSize(new Dimension(250, 20));
        barra.setStringPainted(true);
        barra.setString("");
        fila3.add(barra);

        panel.add(fila1);
        panel.add(fila2);
        panel.add(fila3);
        return panel;
    }

    private JSplitPane crearPanelCentral() {
        arbol.setCellRenderer(new RenderArbol());

        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.add(new JLabel(" Estructura del directorio"), BorderLayout.NORTH);
        panelArbol.add(new JScrollPane(arbol), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(4, 2, 4, 4));
        botones.add(btnNuevaCarpeta);
        botones.add(btnNuevoArchivo);
        botones.add(btnRenombrar);
        botones.add(btnEliminar);
        botones.add(btnCopiar);
        botones.add(btnMover);
        botones.add(btnAbrir);
        panelArbol.add(botones, BorderLayout.SOUTH);

        pestanas.addTab("Estadisticas", crearPestanaEstadisticas());
        pestanas.addTab("Busqueda", crearPestanaBusqueda());
        pestanas.addTab("Duplicados", crearPestanaDuplicados());
        pestanas.addTab("Editor", crearPestanaEditor());
        pestanas.addTab("Bitacora", crearPestanaBitacora());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelArbol, pestanas);
        split.setDividerLocation(330);
        return split;
    }

    private JPanel crearPestanaEstadisticas() {
        String[] columnas = {"Categoria", "Cantidad", "Tamano total", "Porcentaje"};
        modeloEstadisticas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloEstadisticas);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(400, 120));

        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        abajo.add(btnExportar);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollTabla, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtResumen), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPestanaBusqueda() {
        String[] columnas = {"Nombre", "Ruta absoluta", "Tamano", "Fecha de modificacion"};
        modeloResultados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                if (columna == 2) {
                    return Long.class;
                }
                if (columna == 3) {
                    return Date.class;
                }
                return String.class;
            }
        };
        tablaResultados = new JTable(modeloResultados);
        tablaResultados.setAutoCreateRowSorter(true);
        tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(400);
        tablaResultados.getColumnModel().getColumn(2).setCellRenderer(new RenderTamano());
        tablaResultados.getColumnModel().getColumn(3).setCellRenderer(new RenderFecha());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
        panel.add(lblResultados, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPestanaDuplicados() {
        txtDuplicados.setEditable(false);
        txtDuplicados.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(txtDuplicados), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaEditor() {
        txtEditor.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(lblEditor);
        arriba.add(btnGuardar);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtEditor), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaBitacora() {
        txtBitacora.setEditable(false);
        txtBitacora.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(btnBitacora);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtBitacora), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(lblEstado);
        return panel;
    }

    public void setControlador(ActionListener c) {
        JButton[] botones = {btnExaminar, btnArbol, btnAnalizar, btnBuscar, btnDuplicados, btnCancelar,
            btnNuevaCarpeta, btnNuevoArchivo, btnRenombrar, btnCopiar, btnMover, btnEliminar, btnAbrir,
            btnExportar, btnGuardar, btnBitacora};
        String[] comandos = {"EXAMINAR", "ARBOL", "ANALIZAR", "BUSCAR", "DUPLICADOS", "CANCELAR",
            "NUEVA_CARPETA", "NUEVO_ARCHIVO", "RENOMBRAR", "COPIAR", "MOVER", "ELIMINAR", "ABRIR",
            "EXPORTAR", "GUARDAR", "BITACORA"};
        for (int i = 0; i < botones.length; i++) {
            botones[i].setActionCommand(comandos[i]);
            botones[i].addActionListener(c);
        }
    }

    public void setTrabajando(boolean trabajando, String mensaje) {
        btnArbol.setEnabled(!trabajando);
        btnAnalizar.setEnabled(!trabajando);
        btnBuscar.setEnabled(!trabajando);
        btnDuplicados.setEnabled(!trabajando);
        btnCancelar.setEnabled(trabajando);
        barra.setIndeterminate(trabajando);
        if (trabajando) {
            barra.setString("Trabajando...");
        } else {
            barra.setString("");
        }
        lblEstado.setText(mensaje);
    }

    public void setEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    public void mostrarArbol(NodoArchivo raiz) {
        DefaultMutableTreeNode nodoRaiz = convertirNodo(raiz);
        arbol.setModel(new DefaultTreeModel(nodoRaiz));
        arbol.expandRow(0);
    }

    private DefaultMutableTreeNode convertirNodo(NodoArchivo nodo) {
        DefaultMutableTreeNode nodoSwing = new DefaultMutableTreeNode(nodo);
        for (NodoArchivo hijo : nodo.getHijos()) {
            nodoSwing.add(convertirNodo(hijo));
        }
        return nodoSwing;
    }

    public File getSeleccionArbol() {
        TreePath path = arbol.getSelectionPath();
        if (path == null) {
            return null;
        }
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (nodo.getUserObject() instanceof NodoArchivo) {
            return ((NodoArchivo) nodo.getUserObject()).getFile();
        }
        return null;
    }

    public File getSeleccionTabla() {
        int fila = tablaResultados.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        int filaModelo = tablaResultados.convertRowIndexToModel(fila);
        return new File((String) modeloResultados.getValueAt(filaModelo, 1));
    }

    public boolean estaEnPestanaBusqueda() {
        return pestanas.getSelectedIndex() == 1;
    }

    public void mostrarEstadisticas(Estadisticas est) {
        modeloEstadisticas.setRowCount(0);
        for (int i = 0; i < Estadisticas.CATEGORIAS.length; i++) {
            Object[] fila = {
                Estadisticas.CATEGORIAS[i],
                est.getCantidad(i),
                Utilidades.tamanoLegible(est.getTamano(i)),
                String.format("%.2f %%", est.getPorcentaje(i))
            };
            modeloEstadisticas.addRow(fila);
        }
        txtResumen.setText(est.reporteTxt());
        txtResumen.setCaretPosition(0);
        pestanas.setSelectedIndex(0);
    }

    public void mostrarResultados(ArrayList<File> resultados) {
        modeloResultados.setRowCount(0);
        for (File f : resultados) {
            Object[] fila = {f.getName(), f.getAbsolutePath(), f.length(), new Date(f.lastModified())};
            modeloResultados.addRow(fila);
        }
        if (resultados.isEmpty()) {
            lblResultados.setText("No se encontraron archivos que coincidan con los criterios.");
        } else {
            lblResultados.setText(resultados.size() + " archivo(s) encontrado(s)");
        }
        pestanas.setSelectedIndex(1);
    }

    public void mostrarDuplicados(HashMap<String, ArrayList<File>> duplicados) {
        if (duplicados.isEmpty()) {
            txtDuplicados.setText("No se encontraron archivos duplicados.");
        } else {
            String texto = "Grupos de duplicados encontrados: " + duplicados.size() + "\n\n";
            for (String clave : duplicados.keySet()) {
                texto += clave + "\n";
                for (File f : duplicados.get(clave)) {
                    texto += "    " + f.getAbsolutePath() + "\n";
                }
                texto += "\n";
            }
            txtDuplicados.setText(texto);
        }
        txtDuplicados.setCaretPosition(0);
        pestanas.setSelectedIndex(2);
    }

    public void mostrarEditor(File file, String contenido) {
        lblEditor.setText("Archivo: " + file.getAbsolutePath());
        txtEditor.setText(contenido);
        txtEditor.setCaretPosition(0);
        btnGuardar.setEnabled(true);
        pestanas.setSelectedIndex(3);
    }

    public String getTextoEditor() {
        return txtEditor.getText();
    }

    public void mostrarBitacora(String texto) {
        txtBitacora.setText(texto);
        pestanas.setSelectedIndex(4);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmar(String mensaje) {
        int r = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    public String pedirTexto(String mensaje, String inicial) {
        return (String) JOptionPane.showInputDialog(this, mensaje, "Datos", JOptionPane.QUESTION_MESSAGE,
                null, null, inicial);
    }

    public String[] pedirNuevoArchivo() {
        JTextField nombre = new JTextField(25);
        JTextArea contenido = new JTextArea(10, 35);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Nombre:"));
        arriba.add(nombre);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(contenido), BorderLayout.CENTER);
        int r = JOptionPane.showConfirmDialog(this, panel, "Nuevo archivo de texto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return null;
        }
        String[] datos = {nombre.getText(), contenido.getText()};
        return datos;
    }

    public File elegirCarpeta(String titulo) {
        JFileChooser chooser = new JFileChooser(txtRuta.getText());
        chooser.setDialogTitle(titulo);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public File elegirArchivoReporte() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte (.txt o .csv)");
        chooser.setSelectedFile(new File("reporte.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public String getRuta() {
        return txtRuta.getText();
    }

    public void setRuta(String ruta) {
        txtRuta.setText(ruta);
    }

    public String getTexto() {
        return txtTexto.getText();
    }

    public void setTexto(String texto) {
        txtTexto.setText(texto);
    }

    public String getExtension() {
        return txtExtension.getText();
    }

    public void setExtension(String extension) {
        txtExtension.setText(extension);
    }

    public String getMinKB() {
        return txtMinKB.getText();
    }

    public void setMinKB(String valor) {
        txtMinKB.setText(valor);
    }

    public String getMaxKB() {
        return txtMaxKB.getText();
    }

    public void setMaxKB(String valor) {
        txtMaxKB.setText(valor);
    }

    public String getDesde() {
        return txtDesde.getText();
    }

    public void setDesde(String valor) {
        txtDesde.setText(valor);
    }

    public String getHasta() {
        return txtHasta.getText();
    }

    public void setHasta(String valor) {
        txtHasta.setText(valor);
    }

    public int getProfundidad() {
        return (Integer) spnProfundidad.getValue();
    }

    public void setProfundidad(int valor) {
        spnProfundidad.setValue(valor);
    }

    public boolean isOcultos() {
        return chkOcultos.isSelected();
    }

    public void setOcultos(boolean valor) {
        chkOcultos.setSelected(valor);
    }

    private class RenderArbol extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Object dato = ((DefaultMutableTreeNode) value).getUserObject();
            if (dato instanceof NodoArchivo) {
                NodoArchivo nodo = (NodoArchivo) dato;
                if (nodo.esCarpeta()) {
                    setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
            }
            return this;
        }
    }

    private class RenderTamano extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            if (value instanceof Long) {
                setText(Utilidades.tamanoLegible((Long) value));
            } else {
                setText("");
            }
        }
    }

    private class RenderFecha extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            if (value instanceof Date) {
                setText(Utilidades.formatearFecha(((Date) value).getTime()));
            } else {
                setText("");
            }
        }
    }
}