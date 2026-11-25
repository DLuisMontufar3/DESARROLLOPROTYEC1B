
package tutoralerta.vista;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import tutoralerta.modelo.RegistroAsistencia;
import tutoralerta.servicio.AsistenciaServicio;


public class VentanaAsistencia extends JFrame {

    private JTextField campoFecha;
    private JTable tablaAsistencia;
    private DefaultTableModel modeloTabla;
    private JButton botonCargar;
    private JButton botonGuardar;
    private JButton botonNuevaSesion;
    

private void guardarAsistencia() {

    String fecha = campoFecha.getText().trim();

    if (tablaAsistencia.isEditing()) {
        tablaAsistencia.getCellEditor().stopCellEditing();
    }

    if (modeloTabla.getRowCount() == 0) {
        JOptionPane.showMessageDialog(
                this,
                "Primero debes cargar los estudiantes.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        // Evitar guardar datos de una fecha diferente
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String fechaFila = modeloTabla.getValueAt(i, 1).toString();

            if (!fechaFila.equals(fecha)) {
                throw new IllegalArgumentException(
                        "La fecha fue modificada. Vuelve a cargar los estudiantes."
                );
            }
        }

        List<RegistroAsistencia> asistencia = new java.util.ArrayList<>();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {

            String cedula = modeloTabla.getValueAt(i, 0).toString();

            boolean presente = Boolean.TRUE.equals(
                    modeloTabla.getValueAt(i, 2)
            );

            asistencia.add(new RegistroAsistencia(
                    cedula,
                    fecha,
                    presente
            ));
        }

        AsistenciaServicio servicio = new AsistenciaServicio();
        servicio.guardarSesion(fecha, asistencia);

        JOptionPane.showMessageDialog(
                this,
                "Asistencia guardada correctamente."
        );

        modeloTabla.setRowCount(0);
        campoFecha.setEditable(true);

    } catch (IllegalArgumentException ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );

    } catch (IOException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Error al guardar asistencia: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
   
    
    
private void cargarEstudiantes() {

    String fecha = campoFecha.getText().trim();

    try {
        AsistenciaServicio servicio = new AsistenciaServicio();

        List<RegistroAsistencia> asistencia =
                servicio.obtenerAsistencia(fecha);

        campoFecha.setEditable(false);
        modeloTabla.setRowCount(0);
        

        for (RegistroAsistencia registro : asistencia) {
            modeloTabla.addRow(new Object[]{
                registro.getCedula(),
                registro.getFecha(),
                registro.isPresente()
            });
        }

        JOptionPane.showMessageDialog(
                this,
                "Estudiantes cargados correctamente."
        );

    } catch (IllegalArgumentException ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Fecha incorrecta",
                JOptionPane.WARNING_MESSAGE
        );

    } catch (IOException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Error al cargar estudiantes: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


    public VentanaAsistencia() {
        setTitle("TutorAlerta - Registro de Asistencia");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelSuperior = new JPanel(new FlowLayout());

        panelSuperior.add(new JLabel("Fecha (AAAA-MM-DD):"));

        campoFecha = new JTextField(12);
        panelSuperior.add(campoFecha);

        botonCargar = new JButton("Cargar estudiantes");
        botonCargar.addActionListener(e -> cargarEstudiantes());
        panelSuperior.add(botonCargar);

        String[] columnas = {
            "Cédula", "Fecha", "Presente"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 2 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna == 2;
            }
        };

        tablaAsistencia = new JTable(modeloTabla);

        
        botonGuardar = new JButton("Guardar asistencia");
        botonGuardar.addActionListener(e -> guardarAsistencia());
        
        JPanel panelInferior = new JPanel();
        panelInferior.add(botonGuardar);
        
        botonNuevaSesion = new JButton("Nueva sesión");

        botonNuevaSesion.addActionListener(e -> nuevaSesion());

        panelInferior.add(botonNuevaSesion);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaAsistencia), BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    
private void nuevaSesion() {

    modeloTabla.setRowCount(0);

    campoFecha.setText("");
    campoFecha.setEditable(true);

    campoFecha.requestFocusInWindow();
}

    
    
    
    
    
    
public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
        VentanaAsistencia ventana = new VentanaAsistencia();
        ventana.setVisible(true);
    });
}
   
    
    
    
    
   
    
    
}
