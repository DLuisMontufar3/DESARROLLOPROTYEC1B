
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Estilo visual común de TutorAlerta: paleta de colores, fuentes y
 * componentes ya configurados, para que todos los módulos se vean igual.
 *
 * Significado de los colores:
 *   verde    = éxito / todo correcto
 *   rojo     = error
 *   amarillo = alerta o aviso
 *   azul     = información y acciones de navegación
 */
public final class Estilo {

    private Estilo() {
    }

    // ---------- Paleta ----------
    public static final Color AZUL_OSCURO = new Color(23, 61, 101);
    public static final Color AZUL = new Color(37, 99, 235);
    public static final Color AZUL_CLARO = new Color(219, 234, 254);

    public static final Color FONDO = new Color(238, 243, 249);
    public static final Color BLANCO = Color.WHITE;
    public static final Color FILA_ALTERNA = new Color(246, 249, 252);
    public static final Color BORDE = new Color(203, 213, 225);
    public static final Color TEXTO = new Color(30, 41, 59);
    public static final Color TEXTO_SUAVE = new Color(71, 85, 105);
    public static final Color GRIS = new Color(100, 116, 139);
    public static final Color DESHABILITADO = new Color(203, 213, 225);

    public static final Color VERDE = new Color(21, 128, 61);
    public static final Color VERDE_CLARO = new Color(220, 252, 231);
    public static final Color ROJO = new Color(206, 32, 32);
    public static final Color ROJO_CLARO = new Color(254, 226, 226);
    public static final Color AMARILLO = new Color(245, 158, 11);
    public static final Color AMARILLO_OSCURO = new Color(146, 64, 14);
    public static final Color AMARILLO_CLARO = new Color(254, 243, 199);

    /** Tipos de mensaje con sus colores. */
    public enum Tipo {
        EXITO(VERDE, VERDE_CLARO, VERDE),
        ERROR(ROJO, ROJO_CLARO, ROJO),
        ALERTA(AMARILLO, AMARILLO_CLARO, AMARILLO_OSCURO),
        INFO(AZUL, AZUL_CLARO, AZUL_OSCURO);

        /** Color fuerte (iconos, bordes). */
        public final Color fuerte;
        /** Color de fondo suave. */
        public final Color claro;
        /** Color del texto sobre fondo claro (con buen contraste). */
        public final Color texto;

        Tipo(Color fuerte, Color claro, Color texto) {
            this.fuerte = fuerte;
            this.claro = claro;
            this.texto = texto;
        }
    }

    // ---------- Fuentes ----------
    private static final String FAMILIA = elegirFamilia();

    public static final Font FUENTE_TITULO = new Font(FAMILIA, Font.BOLD, 28);
    public static final Font FUENTE_SUBTITULO = new Font(FAMILIA, Font.PLAIN, 16);
    public static final Font FUENTE_ETIQUETA = new Font(FAMILIA, Font.BOLD, 16);
    public static final Font FUENTE_TEXTO = new Font(FAMILIA, Font.PLAIN, 16);
    public static final Font FUENTE_BOTON = new Font(FAMILIA, Font.BOLD, 16);
    public static final Font FUENTE_TABLA = new Font(FAMILIA, Font.PLAIN, 15);
    public static final Font FUENTE_TABLA_ENCABEZADO = new Font(FAMILIA, Font.BOLD, 15);
    public static final Font FUENTE_ESTADO = new Font(FAMILIA, Font.BOLD, 15);

    private static String elegirFamilia() {
        try {
            String[] disponibles = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            for (String preferida : new String[]{"Segoe UI", "Calibri", "Arial"}) {
                if (Arrays.asList(disponibles).contains(preferida)) {
                    return preferida;
                }
            }
        } catch (RuntimeException ex) {
            // Si no se pueden listar las fuentes, se usa la lógica por defecto.
        }
        return Font.SANS_SERIF;
    }

    /** Aplica fuentes y colores por defecto a todos los componentes y diálogos. */
    public static void instalarTema() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        UIManager.put("Label.font", FUENTE_TEXTO);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("TextField.font", FUENTE_TEXTO);
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("ComboBox.font", FUENTE_TEXTO);
        UIManager.put("Button.font", FUENTE_BOTON);
        UIManager.put("CheckBox.font", FUENTE_TEXTO);
        UIManager.put("Table.font", FUENTE_TABLA);
        UIManager.put("TableHeader.font", FUENTE_TABLA_ENCABEZADO);
        UIManager.put("ToolTip.font", FUENTE_TEXTO);

        UIManager.put("OptionPane.messageFont", FUENTE_TEXTO);
        UIManager.put("OptionPane.buttonFont", FUENTE_BOTON);
        UIManager.put("OptionPane.background", BLANCO);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("Panel.background", BLANCO);
    }

    // ---------- Componentes ----------

    /** Botón plano con color de fondo, esquinas redondeadas y efecto al pasar el cursor. */
    public static JButton boton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setUI(new BotonRedondeado());
        b.setFont(FUENTE_BOTON);
        b.setForeground(letra);
        b.setBackground(fondo);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(fondo.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(fondo);
                }
            }
        });
        b.addPropertyChangeListener("enabled", ev -> {
            b.setBackground(b.isEnabled() ? fondo : DESHABILITADO);
            b.setCursor(Cursor.getPredefinedCursor(
                    b.isEnabled() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        });
        return b;
    }

    /** Botón de color verde: acciones que guardan o confirman. */
    public static JButton botonExito(String texto) {
        return boton(texto, VERDE, BLANCO);
    }

    /** Botón azul: acciones normales (cargar, abrir, editar). */
    public static JButton botonPrimario(String texto) {
        return boton(texto, AZUL, BLANCO);
    }

    /** Botón gris: acciones neutras (limpiar, nuevo). */
    public static JButton botonNeutro(String texto) {
        return boton(texto, GRIS, BLANCO);
    }

    /** Campo de texto grande con borde visible. */
    public static JTextField campo(int columnas) {
        JTextField c = new JTextField(columnas);
        c.setFont(FUENTE_TEXTO);
        c.setForeground(TEXTO);
        c.setBackground(BLANCO);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 40));
        return c;
    }

    /** Lista desplegable con la fuente grande. */
    public static <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setFont(FUENTE_TEXTO);
        combo.setBackground(BLANCO);
        combo.setPreferredSize(new Dimension(Math.max(280, combo.getPreferredSize().width), 40));
    }

    /** Etiqueta de campo en negrita y tamaño legible. */
    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_ETIQUETA);
        l.setForeground(TEXTO);
        return l;
    }

    /** Panel blanco con borde, para agrupar formularios y tablas. */
    public static JPanel tarjeta() {
        JPanel p = new JPanel();
        p.setBackground(BLANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return p;
    }

    /** Etiqueta de estado (franja de color bajo los botones). */
    public static JLabel etiquetaEstado() {
        JLabel l = new JLabel(" ");
        l.setFont(FUENTE_ESTADO);
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return l;
    }

    /** Muestra un texto en la etiqueta de estado con el color del tipo indicado. */
    public static void estado(JLabel etiqueta, String texto, Tipo tipo) {
        if (texto == null || texto.trim().isEmpty()) {
            etiqueta.setText(" ");
            etiqueta.setOpaque(false);
            etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            return;
        }
        etiqueta.setText(texto);
        etiqueta.setForeground(tipo.texto);
        etiqueta.setBackground(tipo.claro);
        etiqueta.setOpaque(true);
        etiqueta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, tipo.fuerte),
                BorderFactory.createEmptyBorder(8, 10, 8, 12)));
    }

    /** Define de qué color pintar una celda (null = color normal). */
    public interface ColorCelda {
        Color de(JTable tabla, int fila, int columna);
    }

    /** Tabla con encabezado de color, filas alternadas y letra grande. */
    public static JTable crearTabla(TableModel modelo, ColorCelda colorCelda) {
        JTable tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int fila, int col) {
                Component c = super.prepareRenderer(r, fila, col);
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }
                if (isRowSelected(fila)) {
                    c.setBackground(AZUL_CLARO);
                    c.setForeground(TEXTO);
                } else {
                    Color especial = colorCelda == null ? null : colorCelda.de(this, fila, col);
                    c.setBackground(especial != null ? especial
                            : (fila % 2 == 0 ? BLANCO : FILA_ALTERNA));
                    c.setForeground(TEXTO);
                }
                return c;
            }
        };
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(34);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(TEXTO);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean sel, boolean foco, int fila, int col) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, col);
                setBackground(AZUL_OSCURO);
                setForeground(BLANCO);
                setFont(FUENTE_TABLA_ENCABEZADO);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        });
        return tabla;
    }

    /**
     * Encabezado de un módulo: título, subtítulo y el botón para volver al menú
     * principal sin cerrar la ventana.
     */
    public static JPanel encabezadoModulo(String titulo, String subtitulo,
            Color acento, Runnable alVolver) {
        JPanel texto = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        texto.setOpaque(false);

        JLabel t = new JLabel(titulo);
        t.setFont(FUENTE_TITULO);
        t.setForeground(BLANCO);

        JLabel s = new JLabel(subtitulo);
        s.setFont(FUENTE_SUBTITULO);
        s.setForeground(new Color(214, 229, 244));

        texto.add(t);
        texto.add(s);

        JButton volver = boton("\u2190  Menú principal", AZUL, BLANCO);
        volver.setToolTipText("Volver al menú principal sin cerrar la aplicación");
        volver.addActionListener(e -> alVolver.run());

        // El botón va en un contenedor centrado para no estirarse a lo alto del encabezado
        JPanel zonaBoton = new JPanel(new java.awt.GridBagLayout());
        zonaBoton.setOpaque(false);
        zonaBoton.add(volver);

        JPanel fila = new JPanel(new BorderLayout(20, 0));
        fila.setBackground(AZUL_OSCURO);
        fila.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        fila.add(texto, BorderLayout.CENTER);
        fila.add(zonaBoton, BorderLayout.EAST);

        JPanel franja = new JPanel();
        franja.setBackground(acento);
        franja.setPreferredSize(new Dimension(10, 5));

        JPanel todo = new JPanel(new BorderLayout());
        todo.add(fila, BorderLayout.CENTER);
        todo.add(franja, BorderLayout.SOUTH);
        return todo;
    }

    /** Aspecto de los botones: fondo redondeado y letra gris cuando está deshabilitado. */
    private static final class BotonRedondeado extends BasicButtonUI {

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
            FontMetrics fm = g.getFontMetrics(b.getFont());
            g.setFont(b.getFont());
            g.setColor(b.isEnabled() ? b.getForeground() : new Color(100, 116, 139));
            g.drawString(text, textRect.x, textRect.y + fm.getAscent());
        }
    }
}
