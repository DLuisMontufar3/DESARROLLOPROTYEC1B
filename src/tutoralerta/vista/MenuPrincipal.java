

package tutoralerta.vista;

import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import tutoralerta.servicio.EstudianteServicio;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {

        setTitle("TutorAlerta - Menú principal");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 15));

        JLabel titulo = new JLabel(
                "Bienvenido a TutorAlerta",
                SwingConstants.CENTER
        );

        JButton botonEstudiantes =
                new JButton("Gestión de estudiantes");

        JButton botonAsistencia =
                new JButton("Registro de asistencia");

        JButton botonNotas =
                new JButton("Registro de notas");

        botonEstudiantes.addActionListener(e -> {
            try {
                EstudianteServicio servicio = new EstudianteServicio();

                new VentanaEstudiantes(servicio).setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al cargar estudiantes: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        botonAsistencia.addActionListener(e -> {
            new VentanaAsistencia().setVisible(true);
        });

        botonNotas.addActionListener(e -> {
            new VentanaNotas().setVisible(true);
        });

        panel.add(titulo);
        panel.add(botonEstudiantes);
        panel.add(botonAsistencia);
        panel.add(botonNotas);

        add(panel);
    }
}
