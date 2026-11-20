package tutoralerta.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import tutoralerta.modelo.Estudiante;
import tutoralerta.servicio.EstudianteServicio;

/**
 * Ventana de HU-B1: registrar, listar (ordenado por apellido) y editar estudiantes.
 */
public class VentanaEstudiantes extends JFrame {

    private final EstudianteServicio servicio;

    private final JTextField txtCedula = new JTextField(15);
    private final JTextField txtNombres = new JTextField(15);
    private final JTextField txtApellidos = new JTextField(15);
    private final JTextField txtCorreo = new JTextField(15);

    private final JButton btnRegistrar = new JButton("Registrar");
    private final JButton btnGuardarCambios = new JButton("Guardar cambios");
    private final JButton btnNuevo = new JButton("Limpiar / Nuevo");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Cédula", "Nombres", "Apellidos", "Correo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public VentanaEstudiantes(EstudianteServicio servicio) {
        super("TutorAlerta - Estudiantes");
        this.servicio = servicio;

        construirVentana();
        configurarEventos();
        refrescarTabla();
        modoNuevo();
    }


    private void construirVentana() {
        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.add(new JLabel("Cédula 10 dígitos:"));
        formulario.add(txtCedula);
        formulario.add(new JLabel("Nombres:"));
        formulario.add(txtNombres);
        formulario.add(new JLabel("Apellidos:"));
        formulario.add(txtApellidos);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnGuardarCambios);
        botones.add(btnNuevo);

        JPanel arriba = new JPanel(new BorderLayout(5, 5));
        arriba.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null); 
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
        try {
            servicio.registrar(txtCedula.getText(), txtNombres.getText(),
                    txtApellidos.getText(), txtCorreo.getText());
            refrescarTabla();
            modoNuevo();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            mostrarErrorArchivo(ex);
        }
    }

    private void guardarCambios() {
        try {
            servicio.editar(txtCedula.getText(), txtNombres.getText(),
                    txtApellidos.getText(), txtCorreo.getText());
            refrescarTabla();
            modoNuevo();
            JOptionPane.showMessageDialog(this, "Cambios guardados.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            mostrarErrorArchivo(ex);
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

    //  el estudiante seleccionado y la cédula queda bloqueada.
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
        btnRegistrar.setEnabled(false);
        btnGuardarCambios.setEnabled(true);
    }

    // Modo NUEVO: formulario vacío, listo para registrar.
    private void modoNuevo() {
        tabla.clearSelection();
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCorreo.setText("");

        txtCedula.setEditable(true);
        btnRegistrar.setEnabled(true);
        btnGuardarCambios.setEnabled(false);
        txtCedula.requestFocus();
    }

    private void mostrarErrorArchivo(IOException ex) {
        ex.printStackTrace(); 
        JOptionPane.showMessageDialog(this,
                "No se pudo guardar en el archivo.\n"
                + "Motivo: " + ex.getClass().getSimpleName() + " - " + ex.getMessage() + "\n\n"
                + "Cierra otros programas que tengan abierto estudiantes.csv\n"
                + "y revisa que el archivo no sea de solo lectura.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
