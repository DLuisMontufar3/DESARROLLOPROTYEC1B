
package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import tutoralerta.modelo.Estudiante;
import tutoralerta.servicio.EstudianteServicio;
import tutoralerta.vista.Estilo.Tipo;

/**
 * Módulo de HU-B1: registrar, listar (ordenado por apellido) y editar estudiantes.
 * Es un panel que se muestra dentro de la ventana principal; el botón
 * "Menú principal" del encabezado regresa al menú sin cerrar la aplicación.
 */
public class PanelEstudiantes extends JPanel {

    private final EstudianteServicio servicio;

    private final JTextField txtCedula = Estilo.campo(14);
    private final JTextField txtNombres = Estilo.campo(14);
    private final JTextField txtApellidos = Estilo.campo(14);
    private final JTextField txtCorreo = Estilo.campo(14);

    private final JButton btnRegistrar = Estilo.botonExito("Registrar estudiante");
    private final JButton btnGuardarCambios = Estilo.botonPrimario("Guardar cambios");
    private final JButton btnNuevo = Estilo.botonNeutro("Limpiar / Nuevo");

    private final JLabel estado = Estilo.etiquetaEstado();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Cédula", "Nombres", "Apellidos", "Correo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = Estilo.crearTabla(modeloTabla, null);

    public PanelEstudiantes(EstudianteServicio servicio, Runnable alVolver) {
        super(new BorderLayout());
        this.servicio = servicio;

        construirPanel(alVolver);
        configurarEventos();
        refrescarTabla();
        modoNuevo();
    }

    private void construirPanel(Runnable alVolver) {
        setBackground(Estilo.FONDO);

        // Formulario: dos columnas de etiqueta + campo
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregarCampo(formulario, 0, 0, "Cédula (10 dígitos):", txtCedula);
        agregarCampo(formulario, 0, 2, "Nombres:", txtNombres);
        agregarCampo(formulario, 1, 0, "Apellidos:", txtApellidos);
        agregarCampo(formulario, 1, 2, "Correo (@gmail.com):", txtCorreo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        botones.setOpaque(false);
        botones.add(btnRegistrar);
        botones.add(btnGuardarCambios);
        botones.add(btnNuevo);

        JPanel pie = new JPanel(new BorderLayout(0, 10));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        pie.add(botones, BorderLayout.NORTH);
        pie.add(estado, BorderLayout.SOUTH);

        JPanel tarjetaFormulario = Estilo.tarjeta();
        tarjetaFormulario.setLayout(new BorderLayout());
        tarjetaFormulario.add(formulario, BorderLayout.CENTER);
        tarjetaFormulario.add(pie, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));
        contenido.add(tarjetaFormulario, BorderLayout.NORTH);
        contenido.add(desplazamiento, BorderLayout.CENTER);

        add(Estilo.encabezadoModulo("Gestión de estudiantes",
                "Registrar, consultar y editar estudiantes del curso",
                Estilo.AZUL, alVolver), BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, int fila, int columna, String texto, JTextField campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.insets = new Insets(6, 4, 6, 10);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = columna;
        c.weightx = 0;
        panel.add(Estilo.etiqueta(texto), c);

        c.gridx = columna + 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, columna == 0 ? 28 : 4);
        panel.add(campo, c);
    }

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> registrar());
        btnGuardarCambios.addActionListener(e -> guardarCambios());
        btnNuevo.addActionListener(e -> modoNuevo());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    private void registrar() {
        String nombreCompleto = (txtNombres.getText().trim() + " "
                + txtApellidos.getText().trim()).trim();
        String cedula = txtCedula.getText().trim();
        try {
            servicio.registrar(txtCedula.getText(), txtNombres.getText(),
                    txtApellidos.getText(), txtCorreo.getText());
            refrescarTabla();
            modoNuevo();
            Estilo.estado(estado, "Estudiante registrado: " + nombreCompleto + ".", Tipo.EXITO);
            Mensajes.exito(this, "Estudiante registrado correctamente",
                    nombreCompleto + " (cédula " + cedula + ") se agregó a la lista del curso.");
        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "No se registró al estudiante. Revise los datos.", Tipo.ERROR);
            Mensajes.error(this, "Los datos no son válidos", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "estudiantes.csv", ex);
        }
    }

    private void guardarCambios() {
        String nombreCompleto = (txtNombres.getText().trim() + " "
                + txtApellidos.getText().trim()).trim();
        try {
            servicio.editar(txtCedula.getText(), txtNombres.getText(),
                    txtApellidos.getText(), txtCorreo.getText());
            refrescarTabla();
            modoNuevo();
            Estilo.estado(estado, "Datos actualizados: " + nombreCompleto + ".", Tipo.EXITO);
            Mensajes.exito(this, "Datos actualizados correctamente",
                    "Los cambios de " + nombreCompleto + " se guardaron.");
        } catch (IllegalArgumentException ex) {
            Estilo.estado(estado, "No se guardaron los cambios. Revise los datos.", Tipo.ERROR);
            Mensajes.error(this, "Los datos no son válidos", ex.getMessage());
        } catch (IOException ex) {
            Mensajes.errorArchivo(this, "estudiantes.csv", ex);
        }
    }

    // Vuelve la tabla a llenarse con la lista ya ordenada por apellido.
    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Estudiante e : servicio.listarOrdenado()) {
            modeloTabla.addRow(new Object[]{
                e.getCedula(), e.getNombres(), e.getApellidos(), e.getCorreo()});
        }
    }

    // Carga el estudiante seleccionado en el formulario; la cédula queda bloqueada.
    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return; // no hay nada seleccionado
        }
        txtCedula.setText((String) modeloTabla.getValueAt(fila, 0));
        txtNombres.setText((String) modeloTabla.getValueAt(fila, 1));
        txtApellidos.setText((String) modeloTabla.getValueAt(fila, 2));
        txtCorreo.setText((String) modeloTabla.getValueAt(fila, 3));

        txtCedula.setEditable(false);
        txtCedula.setBackground(Estilo.FONDO);
        btnRegistrar.setEnabled(false);
        btnGuardarCambios.setEnabled(true);
        Estilo.estado(estado, "Editando a " + txtNombres.getText() + " "
                + txtApellidos.getText() + ". La cédula no se puede modificar.", Tipo.INFO);
    }

    // Modo NUEVO: formulario vacío, listo para registrar.
    private void modoNuevo() {
        tabla.clearSelection();
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCorreo.setText("");

        txtCedula.setEditable(true);
        txtCedula.setBackground(Estilo.BLANCO);
        btnRegistrar.setEnabled(true);
        btnGuardarCambios.setEnabled(false);
        txtCedula.requestFocusInWindow();
    }
}
