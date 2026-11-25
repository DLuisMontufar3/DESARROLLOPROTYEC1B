
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import tutoralerta.modelo.Estudiante;
import tutoralerta.modelo.Evaluacion;
import tutoralerta.modelo.Nota;
import tutoralerta.servicio.EstudianteServicio;
import tutoralerta.servicio.NotaServicio;

/**
 * Ventana de HU-B3: registrar las notas de una evaluación para todo el curso
 * y volver a consultarlas al reabrir la evaluación.
 */
public class VentanaNotas extends JFrame {

    private final JTextField campoNombre = new JTextField(14);
    private final JTextField campoFecha = new JTextField(
            LocalDate.now().toString(), 10); // por defecto hoy, pero modificable

    private final JButton botonCargar = new JButton("Cargar estudiantes");
    private final JButton botonGuardar = new JButton("Guardar notas");
    private final JButton botonNueva = new JButton("Nueva evaluación");
    private final JButton botonAbrir = new JButton("Abrir");

    private final JComboBox<Evaluacion> comboGuardadas = new JComboBox<>();
    private final JLabel estado = new JLabel(" ");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Cédula", "Estudiante", "Nota"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == 2;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // Evaluación que está cargada en la tabla (evita guardar con otro nombre/fecha).
    private String nombreCargado;
    private String fechaCargada;

    public VentanaNotas() {
        setTitle("TutorAlerta - Registro de Notas");
        java.awt.Rectangle pantalla = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        setSize(
        Math.min(pantalla.width,Math.max(1100, (int) (pantalla.width * 0.40))),Math.min(pantalla.height,Math.max(700, (int) (pantalla.height * 0.45))));

        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        construirVentana();
        configurarEventos();
        refrescarGuardadas();
    }

    private void construirVentana() {
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Nombre de la evaluación:"));
        fila1.add(campoNombre);
        fila1.add(new JLabel("Fecha (AAAA-MM-DD):"));
        fila1.add(campoFecha);
        fila1.add(botonCargar);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Evaluaciones guardadas:"));
        fila2.add(comboGuardadas);
        fila2.add(botonAbrir);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(fila1, BorderLayout.NORTH);
        superior.add(fila2, BorderLayout.CENTER);
        superior.add(estado, BorderLayout.SOUTH);
        superior.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel inferior = new JPanel();
        inferior.add(botonGuardar);
        inferior.add(botonNueva);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        botonGuardar.setEnabled(false);
    }

    private void configurarEventos() {
        botonCargar.addActionListener(e -> cargarEvaluacion());
        botonGuardar.addActionListener(e -> guardarNotas());
        botonNueva.addActionListener(e -> nuevaEvaluacion());
        botonAbrir.addActionListener(e -> abrirGuardada());
    }

    /** Muestra a todos los estudiantes; si la evaluación existe, con sus notas. */
    private void cargarEvaluacion() {
        String nombre = campoNombre.getText().trim();
        String fecha = campoFecha.getText().trim();

        try {
            NotaServicio servicio = new NotaServicio();
            EstudianteServicio estudiantes = new EstudianteServicio();

            List<Nota> notas = servicio.obtenerNotas(nombre, fecha);

            if (notas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero debes registrar estudiantes (HU-B1).",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean existente = servicio.buscarEvaluacion(nombre, fecha) != null;

            modeloTabla.setRowCount(0);
            for (Nota nota : notas) {
                Estudiante e = estudiantes.buscarPorCedula(nota.getCedula());
                String texto = nota.getValor() == null
                        ? "" : nota.getValor().toPlainString();
                modeloTabla.addRow(new Object[]{
                    nota.getCedula(),
                    e.getApellidos() + " " + e.getNombres(),
                    texto
                });
            }

            nombreCargado = nombre;
            fechaCargada = fecha;
            campoNombre.setEditable(false);
            campoFecha.setEditable(false);
            botonCargar.setEnabled(false);
            botonGuardar.setEnabled(true);
            estado.setText(existente
                    ? "Evaluación guardada: puedes consultar o corregir sus notas."
                    : "Evaluación nueva: ingresa la nota de cada estudiante.");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            mostrarErrorArchivo(ex);
        }
    }

    private void guardarNotas() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        if (modeloTabla.getRowCount() == 0 || nombreCargado == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero debes cargar los estudiantes.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> notas = new LinkedHashMap<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object valor = modeloTabla.getValueAt(i, 2);
            notas.put(modeloTabla.getValueAt(i, 0).toString(),
                    valor == null ? "" : valor.toString());
        }

        try {
            NotaServicio servicio = new NotaServicio();
            servicio.guardarNotas(nombreCargado, fechaCargada, notas);

            JOptionPane.showMessageDialog(this, "Notas guardadas correctamente.");
            refrescarGuardadas();
            nuevaEvaluacion();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            mostrarErrorArchivo(ex);
        }
    }

    /** Reabre una evaluación guardada, elegida de la lista. */
    private void abrirGuardada() {
        Evaluacion elegida = (Evaluacion) comboGuardadas.getSelectedItem();
        if (elegida == null) {
            JOptionPane.showMessageDialog(this,
                    "Todavía no hay evaluaciones guardadas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        campoNombre.setText(elegida.getNombre());
        campoFecha.setText(elegida.getFecha());
        cargarEvaluacion();
    }

    private void nuevaEvaluacion() {
        modeloTabla.setRowCount(0);
        nombreCargado = null;
        fechaCargada = null;

        campoNombre.setText("");
        campoFecha.setText(LocalDate.now().toString());
        campoNombre.setEditable(true);
        campoFecha.setEditable(true);
        botonCargar.setEnabled(true);
        botonGuardar.setEnabled(false);
        estado.setText(" ");
        campoNombre.requestFocusInWindow();
    }

    private void refrescarGuardadas() {
        comboGuardadas.removeAllItems();
        try {
            List<Evaluacion> guardadas = new NotaServicio().listarEvaluaciones();
            for (Evaluacion e : new ArrayList<>(guardadas)) {
                comboGuardadas.addItem(e);
            }
        } catch (IOException ex) {
            mostrarErrorArchivo(ex);
        }
    }

    private void mostrarErrorArchivo(IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "No se pudo leer o guardar el archivo de notas.\n"
                + "Motivo: " + ex.getClass().getSimpleName() + " - " + ex.getMessage() + "\n\n"
                + "Cierra otros programas que tengan abierto notas.csv\n"
                + "y revisa que el archivo no sea de solo lectura.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
