
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import tutoralerta.vista.Estilo.Tipo;

/**
 * Módulo de HU-B3: registrar las notas de una evaluación para todo el curso y
 * volver a consultarlas al reabrir la evaluación.
 * Se muestra dentro de la ventana principal; el botón "Menú principal"
 * del encabezado regresa al menú sin cerrar la aplicación.
 */
public class PanelNotas extends JPanel {

    private static final int COL_NOTA = 2;

    private final JTextField campoNombre = Estilo.campo(14);
    private final JTextField campoFecha = Estilo.campo(9);

    private final JButton botonCargar = Estilo.botonPrimario("Cargar estudiantes");
    private final JButton botonGuardar = Estilo.botonExito("Guardar notas");
    private final JButton botonNueva = Estilo.botonNeutro("Nueva evaluación");
    private final JButton botonAbrir = Estilo.botonPrimario("Abrir");

    private final JComboBox<Evaluacion> comboGuardadas = new JComboBox<>();
    private final JLabel estado = Estilo.etiquetaEstado();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Cédula", "Estudiante", "Nota"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == COL_NOTA;
        }
    };

    // La columna de nota se pinta en vivo: verde si es válida, rojo si no lo es.
    private final JTable tabla = Estilo.crearTabla(modeloTabla, (t, fila, columna) -> {
        if (columna != COL_NOTA) {
            return null;
        }
        Object valor = modeloTabla.getValueAt(fila, COL_NOTA);
        String texto = valor == null ? "" : valor.toString().trim();
        if (texto.isEmpty()) {
            return null;
        }
        try {
            NotaServicio.validarNota(texto);
            return Estilo.VERDE_CLARO;
        } catch (IllegalArgumentException ex) {
            return Estilo.ROJO_CLARO;
        }
    });

    // Evaluación que está cargada en la tabla (evita guardar con otro nombre/fecha).
    private String nombreCargado;
    private String fechaCargada;

    public PanelNotas(Runnable alVolver) {
        super(new BorderLayout());
        setBackground(Estilo.FONDO);

        campoFecha.setText(LocalDate.now().toString()); // por defecto hoy, pero modificable
        Estilo.estilizarCombo(comboGuardadas);

        construirPanel(alVolver);
        configurarEventos();
        refrescarGuardadas();
        Estilo.estado(estado, "Escriba el nombre de la evaluación y pulse \"Cargar estudiantes\".",
                Tipo.INFO);
    }

    private void construirPanel(Runnable alVolver) {
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fila1.setOpaque(false);
        fila1.add(Estilo.etiqueta("Nombre de la evaluación:"));
        fila1.add(campoNombre);
        fila1.add(Estilo.etiqueta("Fecha (AAAA-MM-DD):"));
        fila1.add(campoFecha);
        fila1.add(botonCargar);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fila2.setOpaque(false);
        fila2.add(Estilo.etiqueta("Evaluaciones guardadas:"));
        fila2.add(comboGuardadas);
        fila2.add(botonAbrir);

        JPanel filas = new JPanel(new BorderLayout(0, 12));
        filas.setOpaque(false);
        filas.add(fila1, BorderLayout.NORTH);
        filas.add(fila2, BorderLayout.CENTER);

        JPanel tarjetaSuperior = Estilo.tarjeta();
        tarjetaSuperior.setLayout(new BorderLayout(0, 10));
        tarjetaSuperior.add(filas, BorderLayout.NORTH);
        tarjetaSuperior.add(estado, BorderLayout.SOUTH);

        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        botones.setOpaque(false);
        botones.add(botonGuardar);
        botones.add(botonNueva);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        contenido.add(tarjetaSuperior, BorderLayout.NORTH);
        contenido.add(desplazamiento, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        add(Estilo.encabezadoModulo("Registro de notas",
                "Gestionar evaluaciones y calificaciones del curso",
                new Color(99, 102, 241), alVolver), BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

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
                Estilo.estado(estado, "No hay estudiantes registrados.", Tipo.ALERTA);
                Mensajes.alerta(this, "No hay estudiantes registrados",
                        "Primero registre estudiantes en el módulo de gestión de estudiantes.");
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
            campoNombre.setBackground(Estilo.FONDO);
            campoFecha.setBackground(Estilo.FONDO);
            botonCargar.setEnabled(false);
            botonGuardar.setEnabled(true);

            if (existente) {
                Estilo.estado(estado, "Evaluación guardada: puede consultar o corregir sus notas.",
                        Tipo.INFO);
            } else {
                Estilo.estado(estado, "Evaluación nueva: ingrese la nota de cada estudiante "
                        + "(verde = válida, rojo = incorrecta).", Tipo.EXITO);
            }

        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "Revise el nombre y la fecha de la evaluación.", Tipo.ERROR);
            Mensajes.error(this, "Los datos no son válidos", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "notas.csv o evaluaciones.csv", ex);
        }
    }

    private void guardarNotas() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        if (modeloTabla.getRowCount() == 0 || nombreCargado == null) {
            Mensajes.alerta(this, "Primero cargue los estudiantes",
                    "Escriba el nombre de la evaluación y pulse \"Cargar estudiantes\".");
            return;
        }

        Map<String, String> notas = new LinkedHashMap<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object valor = modeloTabla.getValueAt(i, COL_NOTA);
            notas.put(modeloTabla.getValueAt(i, 0).toString(),
                    valor == null ? "" : valor.toString());
        }

        try {
            NotaServicio servicio = new NotaServicio();
            servicio.guardarNotas(nombreCargado, fechaCargada, notas);

            String nombre = nombreCargado;
            String fecha = fechaCargada;
            int cantidad = notas.size();

            refrescarGuardadas();
            nuevaEvaluacion();
            Estilo.estado(estado, "Notas de \"" + nombre + "\" guardadas.", Tipo.EXITO);

            Mensajes.exito(this, "Notas guardadas correctamente",
                    "Se guardaron las notas de " + cantidad + " estudiantes para la evaluación \""
                    + nombre + "\" (" + fecha + ").");

        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "No se guardaron las notas. Corrija las casillas en rojo.", Tipo.ERROR);
            Mensajes.error(this, "Hay notas que corregir", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "notas.csv o evaluaciones.csv", ex);
        }
    }

    /** Reabre una evaluación guardada, elegida de la lista. */
    private void abrirGuardada() {
        Evaluacion elegida = (Evaluacion) comboGuardadas.getSelectedItem();
        if (elegida == null) {
            Mensajes.alerta(this, "No hay evaluaciones guardadas",
                    "Todavía no se ha guardado ninguna evaluación. Cree una nueva y guarde sus notas.");
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
        campoNombre.setBackground(Estilo.BLANCO);
        campoFecha.setBackground(Estilo.BLANCO);
        botonCargar.setEnabled(true);
        botonGuardar.setEnabled(false);
        Estilo.estado(estado, "Escriba el nombre de la evaluación y pulse \"Cargar estudiantes\".",
                Tipo.INFO);
        campoNombre.requestFocusInWindow();
    }

    private void refrescarGuardadas() {
        comboGuardadas.removeAllItems();
        try {
            for (Evaluacion e : new NotaServicio().listarEvaluaciones()) {
                comboGuardadas.addItem(e);
            }
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "evaluaciones.csv", ex);
        }
    }
}
