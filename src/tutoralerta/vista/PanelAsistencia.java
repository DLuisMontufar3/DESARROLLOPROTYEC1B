
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import tutoralerta.modelo.Estudiante;
import tutoralerta.modelo.RegistroAsistencia;
import tutoralerta.servicio.AsistenciaServicio;
import tutoralerta.servicio.EstudianteServicio;
import tutoralerta.vista.Estilo.Tipo;

/**
 * Módulo de HU-B2: registrar la asistencia de cada clase.
 * Se muestra dentro de la ventana principal; el botón "Menú principal"
 * del encabezado regresa al menú sin cerrar la aplicación.
 */
public class PanelAsistencia extends JPanel {

    private static final int COL_CEDULA = 0;
    private static final int COL_ESTUDIANTE = 1;
    private static final int COL_FECHA = 2;
    private static final int COL_PRESENTE = 3;

    private final JTextField campoFecha = Estilo.campo(10);
    private final JButton botonCargar = Estilo.botonPrimario("Cargar estudiantes");
    private final JButton botonGuardar = Estilo.botonExito("Guardar asistencia");
    private final JButton botonNuevaSesion = Estilo.botonNeutro("Nueva sesión");

    private final JLabel estado = Estilo.etiquetaEstado();
    private final JLabel contador = new JLabel(" ");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Cédula", "Estudiante", "Fecha", "Presente"}, 0) {
        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == COL_PRESENTE ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == COL_PRESENTE;
        }
    };

    // Las filas de estudiantes ausentes se pintan en rojo suave.
    private final JTable tablaAsistencia = Estilo.crearTabla(modeloTabla,
            (tabla, fila, columna) -> Boolean.FALSE.equals(modeloTabla.getValueAt(fila, COL_PRESENTE))
                    ? Estilo.ROJO_CLARO : null);

    public PanelAsistencia(Runnable alVolver) {
        super(new BorderLayout());
        setBackground(Estilo.FONDO);

        // Fecha de hoy por defecto (se puede cambiar)
        campoFecha.setText(LocalDate.now().toString());

        JPanel filaFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaFecha.setOpaque(false);
        filaFecha.add(Estilo.etiqueta("Fecha (AAAA-MM-DD):"));
        filaFecha.add(campoFecha);
        filaFecha.add(botonCargar);

        JPanel tarjetaSuperior = Estilo.tarjeta();
        tarjetaSuperior.setLayout(new BorderLayout(0, 10));
        tarjetaSuperior.add(filaFecha, BorderLayout.NORTH);
        tarjetaSuperior.add(estado, BorderLayout.SOUTH);

        contador.setFont(Estilo.FUENTE_ESTADO);
        contador.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        JScrollPane desplazamiento = new JScrollPane(tablaAsistencia);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(contador, BorderLayout.NORTH);
        centro.add(desplazamiento, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        botones.setOpaque(false);
        botones.add(botonGuardar);
        botones.add(botonNuevaSesion);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        contenido.add(tarjetaSuperior, BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        add(Estilo.encabezadoModulo("Registro de asistencia",
                "Controlar la asistencia de cada sesión de clase",
                new Color(13, 148, 136), alVolver), BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

        botonCargar.addActionListener(e -> cargarEstudiantes());
        botonGuardar.addActionListener(e -> guardarAsistencia());
        botonNuevaSesion.addActionListener(e -> nuevaSesion());
        modeloTabla.addTableModelListener(e -> actualizarContador());

        Estilo.estado(estado, "Elija la fecha y pulse \"Cargar estudiantes\".", Tipo.INFO);
        actualizarContador();
    }

    private void cargarEstudiantes() {
        String fecha = campoFecha.getText().trim();

        try {
            AsistenciaServicio servicio = new AsistenciaServicio();
            EstudianteServicio estudiantes = new EstudianteServicio();

            List<RegistroAsistencia> asistencia = servicio.obtenerAsistencia(fecha);

            campoFecha.setEditable(false);
            campoFecha.setBackground(Estilo.FONDO);
            modeloTabla.setRowCount(0);

            for (RegistroAsistencia registro : asistencia) {
                Estudiante e = estudiantes.buscarPorCedula(registro.getCedula());
                String nombre = e == null ? "" : e.getApellidos() + " " + e.getNombres();
                modeloTabla.addRow(new Object[]{
                    registro.getCedula(),
                    nombre,
                    registro.getFecha(),
                    registro.isPresente()
                });
            }

            if (asistencia.isEmpty()) {
                Estilo.estado(estado, "No hay estudiantes registrados. Primero registre estudiantes.",
                        Tipo.ALERTA);
            } else {
                Estilo.estado(estado, "Lista del " + fecha + " cargada con " + asistencia.size()
                        + " estudiantes. Desmarque a quienes faltaron.", Tipo.EXITO);
            }

        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "La fecha no es válida.", Tipo.ERROR);
            Mensajes.error(this, "Fecha incorrecta", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "asistencia.csv o estudiantes.csv", ex);
        }
    }

    private void guardarAsistencia() {
        String fecha = campoFecha.getText().trim();

        if (tablaAsistencia.isEditing()) {
            tablaAsistencia.getCellEditor().stopCellEditing();
        }

        if (modeloTabla.getRowCount() == 0) {
            Mensajes.alerta(this, "Primero cargue los estudiantes",
                    "Elija la fecha y pulse \"Cargar estudiantes\" antes de guardar la asistencia.");
            return;
        }

        try {
            // Evitar guardar datos de una fecha diferente
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String fechaFila = modeloTabla.getValueAt(i, COL_FECHA).toString();

                if (!fechaFila.equals(fecha)) {
                    throw new IllegalArgumentException(
                            "La fecha fue modificada. Vuelve a cargar los estudiantes.");
                }
            }

            List<RegistroAsistencia> asistencia = new ArrayList<>();
            int presentes = 0;

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String cedula = modeloTabla.getValueAt(i, COL_CEDULA).toString();
                boolean presente = Boolean.TRUE.equals(modeloTabla.getValueAt(i, COL_PRESENTE));
                if (presente) {
                    presentes++;
                }
                asistencia.add(new RegistroAsistencia(cedula, fecha, presente));
            }
            int ausentes = asistencia.size() - presentes;

            AsistenciaServicio servicio = new AsistenciaServicio();
            servicio.guardarSesion(fecha, asistencia);

            modeloTabla.setRowCount(0);
            campoFecha.setEditable(true);
            campoFecha.setBackground(Estilo.BLANCO);
            Estilo.estado(estado, "Asistencia del " + fecha + " guardada.", Tipo.EXITO);

            Mensajes.exito(this, "Asistencia guardada correctamente",
                    "Sesión del " + fecha + ": " + presentes + " presentes y "
                    + ausentes + " ausentes.");

        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "No se guardó la asistencia.", Tipo.ERROR);
            Mensajes.error(this, "No se pudo guardar la asistencia", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "asistencia.csv", ex);
        }
    }

    private void nuevaSesion() {
        modeloTabla.setRowCount(0);

        campoFecha.setText(LocalDate.now().toString());
        campoFecha.setEditable(true);
        campoFecha.setBackground(Estilo.BLANCO);
        Estilo.estado(estado, "Elija la fecha y pulse \"Cargar estudiantes\".", Tipo.INFO);

        campoFecha.requestFocusInWindow();
    }

    /** Muestra cuántos están presentes (verde) y cuántos ausentes (rojo). */
    private void actualizarContador() {
        int total = modeloTabla.getRowCount();
        if (total == 0) {
            contador.setText(" ");
            return;
        }
        int presentes = 0;
        for (int i = 0; i < total; i++) {
            if (Boolean.TRUE.equals(modeloTabla.getValueAt(i, COL_PRESENTE))) {
                presentes++;
            }
        }
        int ausentes = total - presentes;
        contador.setText("<html><span style='color:#15803D'>Presentes: " + presentes
                + "</span> &nbsp;&nbsp;|&nbsp;&nbsp; <span style='color:#CE2020'>Ausentes: "
                + ausentes + "</span></html>");
    }
}
