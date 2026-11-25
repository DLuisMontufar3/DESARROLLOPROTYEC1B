
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import tutoralerta.servicio.EstudianteServicio;

public class MenuPrincipal extends JFrame {

    // Paleta de colores de TutorAlerta
    private static final Color AZUL_PRINCIPAL =
            new Color(23, 61, 101);

    private static final Color AZUL_SECUNDARIO =
            new Color(37, 99, 235);

    private static final Color FONDO =
            new Color(241, 245, 249);

    private static final Color TEXTO_SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color BORDE =
            new Color(203, 213, 225);

    public MenuPrincipal() {

        configurarVentana();
        construirInterfaz();

        setLocationRelativeTo(null);
    }

    private void configurarVentana() {

        setTitle("TutorAlerta - Menú principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Obtener el espacio disponible de la pantalla
        Rectangle pantalla = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int ancho = Math.min(
                pantalla.width,
                Math.max(800, (int) (pantalla.width * 0.40))
        );

        int alto = Math.min(
                pantalla.height,
                Math.max(600, (int) (pantalla.height * 0.45))
        );

        setSize(ancho, alto);

        setMinimumSize(new Dimension(
                Math.min(700, pantalla.width),
                Math.min(550, pantalla.height)
        ));
    }

    private void construirInterfaz() {

        JPanel panelPrincipal =
                new JPanel(new BorderLayout());

        panelPrincipal.setBackground(FONDO);

        // Encabezado
        JPanel encabezado = new JPanel(
                new GridLayout(3, 1, 0, 8)
        );

        encabezado.setBackground(AZUL_PRINCIPAL);

        encabezado.setBorder(
                BorderFactory.createEmptyBorder(25, 20, 25, 20)
        );

        JLabel titulo = new JLabel(
                "TutorAlerta",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font(
                "SansSerif", Font.BOLD, 38
        ));

        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Sistema de seguimiento académico",
                SwingConstants.CENTER
        );

        subtitulo.setFont(new Font(
                "SansSerif", Font.PLAIN, 20
        ));

        subtitulo.setForeground(Color.WHITE);

        JLabel escenario = new JLabel(
                "Escenario B - Sprint 1",
                SwingConstants.CENTER
        );

        escenario.setFont(new Font(
                "SansSerif", Font.PLAIN, 15
        ));

        escenario.setForeground(new Color(214, 229, 244));

        encabezado.add(titulo);
        encabezado.add(subtitulo);
        encabezado.add(escenario);

        // Contenido central
        JPanel contenido = new JPanel(
                new BorderLayout(0, 20)
        );

        contenido.setBackground(FONDO);

        contenido.setBorder(
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        );

        JLabel instruccion = new JLabel(
                "Seleccione el módulo al que desea ingresar",
                SwingConstants.CENTER
        );

        instruccion.setFont(new Font(
                "SansSerif", Font.PLAIN, 18
        ));

        instruccion.setForeground(TEXTO_SECUNDARIO);

        JPanel modulos = new JPanel(
                new GridLayout(3, 1, 0, 18)
        );

        modulos.setOpaque(false);

        JButton botonEstudiantes = crearBoton(
                "Gestión de estudiantes",
                "Registrar, consultar y editar estudiantes"
        );

        JButton botonAsistencia = crearBoton(
                "Registro de asistencia",
                "Controlar la asistencia de cada sesión"
        );

        JButton botonNotas = crearBoton(
                "Registro de notas",
                "Gestionar evaluaciones y calificaciones"
        );

        // Se conserva la navegación original
        botonEstudiantes.addActionListener(e -> {
            try {
                EstudianteServicio servicio =
                        new EstudianteServicio();

                new VentanaEstudiantes(servicio)
                        .setVisible(true);

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al cargar estudiantes: "
                                + ex.getMessage(),
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

        modulos.add(botonEstudiantes);
        modulos.add(botonAsistencia);
        modulos.add(botonNotas);

        contenido.add(instruccion, BorderLayout.NORTH);
        contenido.add(modulos, BorderLayout.CENTER);

        // Pie de página
        JLabel pie = new JLabel(
                "Equipo 4 | Escuela Politécnica Nacional",
                SwingConstants.CENTER
        );

        pie.setFont(new Font(
                "SansSerif", Font.PLAIN, 14
        ));

        pie.setForeground(TEXTO_SECUNDARIO);

        pie.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 20, 10)
        );

        panelPrincipal.add(encabezado, BorderLayout.NORTH);
        panelPrincipal.add(contenido, BorderLayout.CENTER);
        panelPrincipal.add(pie, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private JButton crearBoton(
            String titulo,
            String descripcion
    ) {

        JButton boton = new JButton(
                "<html>"
                + "<span style='font-size:18pt; "
                + "color:#173D65;'>"
                + "<b>" + titulo + "</b></span>"
                + "<br>"
                + "<span style='font-size:13pt; "
                + "color:#64748B;'>"
                + descripcion
                + "</span></html>"
        );

        boton.setFont(new Font(
                "SansSerif", Font.PLAIN, 18
        ));

        boton.setBackground(Color.WHITE);
        boton.setForeground(AZUL_PRINCIPAL);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);

        boton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 5, 1, 1, AZUL_SECUNDARIO
                        ),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(
                                        0, 0, 0, 1, BORDE
                                ),
                                BorderFactory.createEmptyBorder(
                                        10, 25, 10, 25
                                )
                        )
                )
        );

        // Efecto visual al pasar el cursor
        boton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(
                    java.awt.event.MouseEvent e
            ) {
                boton.setBackground(
                        new Color(219, 234, 254)
                );
            }

            @Override
            public void mouseExited(
                    java.awt.event.MouseEvent e
            ) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }
}
