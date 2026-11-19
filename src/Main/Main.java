/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

/**
 *
 * @author USER
 */
import controlador.Controlador;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vista.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            JOptionPane.showMessageDialog(null, "Ocurrio un error: " + error.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo cargar el estilo del sistema");
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            new Controlador(ventana);
            ventana.setVisible(true);
        });
    }
    
    
}
