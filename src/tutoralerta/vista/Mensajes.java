
package tutoralerta.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import tutoralerta.vista.Estilo.Tipo;

/**
 * Ventanas emergentes de TutorAlerta. Cada mensaje tiene:
 *  - un título de ventana descriptivo ("Cambio exitoso", "Error", "Alerta"...),
 *  - un icono de color según el tipo (verde, rojo, amarillo o azul),
 *  - un encabezado que resume lo ocurrido y un detalle opcional.
 */
public final class Mensajes {

    private Mensajes() {
    }

    // ---------- Mensajes de uso común ----------

    /** Operación realizada correctamente (verde). */
    public static void exito(Component padre, String encabezado, String detalle) {
        mostrar(padre, Tipo.EXITO, "Cambio exitoso", encabezado, detalle);
    }

    /** Error que impide continuar, por ejemplo datos no válidos (rojo). */
    public static void error(Component padre, String encabezado, String detalle) {
        mostrar(padre, Tipo.ERROR, "Error", encabezado, detalle);
    }

    /** Error con título de ventana propio (por ejemplo "Error de archivo"). */
    public static void error(Component padre, String tituloVentana,
            String encabezado, String detalle) {
        mostrar(padre, Tipo.ERROR, tituloVentana, encabezado, detalle);
    }

    /** Aviso o precaución (amarillo). */
    public static void alerta(Component padre, String encabezado, String detalle) {
        mostrar(padre, Tipo.ALERTA, "Alerta", encabezado, detalle);
    }

    /** Información general (azul). */
    public static void info(Component padre, String encabezado, String detalle) {
        mostrar(padre, Tipo.INFO, "Información", encabezado, detalle);
    }

    /** Pregunta Sí / No antes de una acción delicada. Devuelve true si se confirma. */
    public static boolean confirmar(Component padre, String encabezado, String detalle) {
        JButton si = Estilo.boton("Sí", Estilo.VERDE, Color.WHITE);
        JButton no = Estilo.botonNeutro("No");
        JOptionPane panel = crear(Tipo.ALERTA, encabezado, detalle, new Object[]{si, no});
        JDialog dialogo = panel.createDialog(padre, "Confirmación");
        cerrarCon(si, "Sí", panel, dialogo);
        cerrarCon(no, "No", panel, dialogo);
        dialogo.setResizable(false);
        dialogo.setVisible(true);
        dialogo.dispose();
        return "Sí".equals(panel.getValue());
    }

    /** Mensaje de error al leer o escribir un archivo de datos. */
    public static void errorArchivo(Component padre, String archivo, Exception ex) {
        ex.printStackTrace();
        error(padre, "Error de archivo", "No se pudo leer o guardar la información",
                "No se pudo acceder a " + archivo + ".\n"
                + "Motivo: " + ex.getClass().getSimpleName() + " - " + ex.getMessage() + "\n\n"
                + "Cierra otros programas que tengan abierto el archivo y revisa que "
                + "no sea de solo lectura.");
    }

    // ---------- Construcción ----------

    private static void mostrar(Component padre, Tipo tipo, String tituloVentana,
            String encabezado, String detalle) {
        JButton aceptar = tipo == Tipo.ALERTA
                ? Estilo.boton("Aceptar", Estilo.AMARILLO, Estilo.TEXTO)
                : Estilo.boton("Aceptar", tipo.fuerte, Color.WHITE);
        JOptionPane panel = crear(tipo, encabezado, detalle, new Object[]{aceptar});
        JDialog dialogo = panel.createDialog(padre, tituloVentana);
        cerrarCon(aceptar, "Aceptar", panel, dialogo);
        dialogo.setResizable(false);
        dialogo.setVisible(true);
        dialogo.dispose();
    }

    /** Hace que el botón registre su valor y cierre el cuadro de mensaje. */
    private static void cerrarCon(JButton boton, String valor, JOptionPane panel, JDialog dialogo) {
        boton.addActionListener(e -> {
            panel.setValue(valor);
            dialogo.setVisible(false);
        });
    }

    /** Arma el cuadro de mensaje (también se usa para probar su aspecto). */
    static JOptionPane crear(Tipo tipo, String encabezado, String detalle, Object[] opciones) {
        JLabel mensaje = new JLabel(html(tipo, encabezado, detalle));
        JOptionPane panel = new JOptionPane(mensaje, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION, new IconoMensaje(tipo), opciones, opciones[0]);
        return panel;
    }

    private static String html(Tipo tipo, String encabezado, String detalle) {
        StringBuilder sb = new StringBuilder("<html><body style='width:430px'>");
        sb.append("<div style='font-size:19pt;color:")
                .append(hex(tipo.texto)).append("'><b>")
                .append(escapar(encabezado)).append("</b></div>");
        if (detalle != null && !detalle.trim().isEmpty()) {
            sb.append("<div style='font-size:15pt;color:#1E293B;margin-top:10px'>")
                    .append(escapar(detalle).replace("\n", "<br>"))
                    .append("</div>");
        }
        return sb.append("</body></html>").toString();
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String hex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    /** Icono de color dibujado por código (no depende del sistema operativo). */
    private static final class IconoMensaje implements Icon {

        private static final int TAMANO = 52;
        private final Tipo tipo;

        IconoMensaje(Tipo tipo) {
            this.tipo = tipo;
        }

        @Override
        public int getIconWidth() {
            return TAMANO;
        }

        @Override
        public int getIconHeight() {
            return TAMANO;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            g2.setColor(tipo.fuerte);

            if (tipo == Tipo.ALERTA) {
                Path2D triangulo = new Path2D.Double();
                triangulo.moveTo(TAMANO / 2.0, 3);
                triangulo.lineTo(TAMANO - 2, TAMANO - 5);
                triangulo.lineTo(2, TAMANO - 5);
                triangulo.closePath();
                g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(triangulo);
                g2.fill(triangulo);
                g2.setColor(Estilo.AMARILLO_OSCURO);
                g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(TAMANO / 2, 20, TAMANO / 2, 33);
                g2.fillOval(TAMANO / 2 - 3, 38, 6, 6);
            } else {
                g2.fillOval(2, 2, TAMANO - 4, TAMANO - 4);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (tipo == Tipo.EXITO) {
                    g2.drawPolyline(new int[]{14, 23, 39}, new int[]{27, 36, 17}, 3);
                } else if (tipo == Tipo.ERROR) {
                    g2.drawLine(16, 16, 36, 36);
                    g2.drawLine(36, 16, 16, 36);
                } else {
                    g2.fillOval(TAMANO / 2 - 3, 13, 6, 6);
                    g2.drawLine(TAMANO / 2, 24, TAMANO / 2, 38);
                }
            }
            g2.dispose();
        }
    }
}
