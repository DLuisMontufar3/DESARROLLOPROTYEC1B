
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import tutoralerta.servicio.EstudianteServicio;

/**
 * Ventana única de TutorAlerta. Contiene el menú principal y, dentro de la
 * misma ventana, los módulos de estudiantes, asistencia y notas. Cada módulo
 * tiene un botón "Menú principal" para volver sin cerrar la aplicación.
 */
public class MenuPrincipal extends JFrame {

    private static final String TARJETA_MENU = "menu";
    private static final String TARJETA_MODULO = "modulo";
    private static final String TITULO_MENU = "TutorAlerta - Menú principal";

    private static final Color ACENTO_ESTUDIANTES = Estilo.AZUL;
    private static final Color ACENTO_ASISTENCIA = new Color(13, 148, 136);
    private static final Color ACENTO_NOTAS = new Color(99, 102, 241);

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenedor = new JPanel(tarjetas);
    private JPanel moduloActual;

    public MenuPrincipal() {
        configurarVentana();

        contenedor.add(construirMenu(), TARJETA_MENU);
        setContentPane(contenedor);

        setLocationRelativeTo(null);
    }

    private void configurarVentana() {
        setTitle(TITULO_MENU);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Tamaño según el espacio disponible de la pantalla
        Rectangle pantalla = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int ancho = Math.min(pantalla.width,
                Math.max(1100, (int) (pantalla.width * 0.60)));
        int alto = Math.min(pantalla.height,
                Math.max(740, (int) (pantalla.height * 0.78)));

        setSize(ancho, alto);
        setMinimumSize(new Dimension(
                Math.min(1000, pantalla.width),
                Math.min(680, pantalla.height)));
    }

    // ---------- Navegación dentro de la misma ventana ----------

    private void abrirEstudiantes() {
        try {
            EstudianteServicio servicio = new EstudianteServicio();
            mostrarModulo("Gestión de estudiantes",
                    new PanelEstudiantes(servicio, this::volverAlMenu));
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "estudiantes.csv", ex);
        }
    }

    private void abrirAsistencia() {
        mostrarModulo("Registro de asistencia", new PanelAsistencia(this::volverAlMenu));
    }

    private void abrirNotas() {
        mostrarModulo("Registro de notas", new PanelNotas(this::volverAlMenu));
    }

    /** Reemplaza el módulo mostrado (así cada módulo siempre lee los datos al día). */
    private void mostrarModulo(String titulo, JPanel modulo) {
        if (moduloActual != null) {
            contenedor.remove(moduloActual);
        }
        moduloActual = modulo;
        contenedor.add(modulo, TARJETA_MODULO);
        tarjetas.show(contenedor, TARJETA_MODULO);
        setTitle("TutorAlerta - " + titulo);
        contenedor.revalidate();
        contenedor.repaint();
    }

    private void volverAlMenu() {
        tarjetas.show(contenedor, TARJETA_MENU);
        if (moduloActual != null) {
            contenedor.remove(moduloActual);
            moduloActual = null;
        }
        setTitle(TITULO_MENU);
        contenedor.revalidate();
        contenedor.repaint();
    }

    // ---------- Pantalla del menú ----------

    private JPanel construirMenu() {

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Estilo.FONDO);

        // Encabezado
        JPanel encabezado = new JPanel(new GridLayout(3, 1, 0, 8));
        encabezado.setBackground(Estilo.AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 20));

        JLabel titulo = new JLabel("TutorAlerta", SwingConstants.CENTER);
        titulo.setFont(new Font(Estilo.FUENTE_TITULO.getFamily(), Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de seguimiento académico", SwingConstants.CENTER);
        subtitulo.setFont(new Font(Estilo.FUENTE_TITULO.getFamily(), Font.PLAIN, 21));
        subtitulo.setForeground(Color.WHITE);

        JLabel escenario = new JLabel("Escenario B - Sprint 1", SwingConstants.CENTER);
        escenario.setFont(new Font(Estilo.FUENTE_TITULO.getFamily(), Font.PLAIN, 16));
        escenario.setForeground(new Color(214, 229, 244));

        encabezado.add(titulo);
        encabezado.add(subtitulo);
        encabezado.add(escenario);

        // Contenido central
        JPanel contenido = new JPanel(new BorderLayout(0, 22));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(26, 60, 20, 60));

        JLabel instruccion = new JLabel(
                "Seleccione el módulo al que desea ingresar", SwingConstants.CENTER);
        instruccion.setFont(new Font(Estilo.FUENTE_TITULO.getFamily(), Font.BOLD, 20));
        instruccion.setForeground(Estilo.TEXTO_SUAVE);

        JPanel modulos = new JPanel(new GridLayout(3, 1, 0, 18));
        modulos.setOpaque(false);

        JButton botonEstudiantes = crearBoton("Gestión de estudiantes",
                "Registrar, consultar y editar estudiantes", ACENTO_ESTUDIANTES);
        JButton botonAsistencia = crearBoton("Registro de asistencia",
                "Controlar la asistencia de cada sesión", ACENTO_ASISTENCIA);
        JButton botonNotas = crearBoton("Registro de notas",
                "Gestionar evaluaciones y calificaciones", ACENTO_NOTAS);

        botonEstudiantes.addActionListener(e -> abrirEstudiantes());
        botonAsistencia.addActionListener(e -> abrirAsistencia());
        botonNotas.addActionListener(e -> abrirNotas());

        modulos.add(botonEstudiantes);
        modulos.add(botonAsistencia);
        modulos.add(botonNotas);

        contenido.add(instruccion, BorderLayout.NORTH);
        contenido.add(modulos, BorderLayout.CENTER);

        // Pie de página
        JLabel pie = new JLabel("Equipo 4 | Escuela Politécnica Nacional", SwingConstants.CENTER);
        pie.setFont(Estilo.FUENTE_SUBTITULO);
        pie.setForeground(Estilo.TEXTO_SUAVE);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 10, 20, 10));

        panelPrincipal.add(encabezado, BorderLayout.NORTH);
        panelPrincipal.add(contenido, BorderLayout.CENTER);
        panelPrincipal.add(pie, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    private JButton crearBoton(String titulo, String descripcion, Color acento) {

        JButton boton = new JButton(
                "<html><span style='font-size:19pt;color:#173D65'><b>" + titulo + "</b></span>"
                + "<br><span style='font-size:14pt;color:#475569'>" + descripcion + "</span></html>");

        boton.setFont(Estilo.FUENTE_TEXTO); // la descripción va en letra normal; el título lleva <b>
        boton.setBackground(Color.WHITE);
        boton.setForeground(Estilo.AZUL_OSCURO);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 8, 1, 1, acento),
                        BorderFactory.createMatteBorder(0, 0, 0, 0, Estilo.BORDE)),
                BorderFactory.createEmptyBorder(12, 28, 12, 28)));

        // Efecto visual al pasar el cursor
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(Estilo.AZUL_CLARO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }
}
