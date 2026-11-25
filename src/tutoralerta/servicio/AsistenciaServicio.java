
package tutoralerta.servicio;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tutoralerta.modelo.Estudiante;
import tutoralerta.modelo.RegistroAsistencia;
import tutoralerta.persistencia.AsistenciaRepositorio;

/**
 * Gestiona las reglas del registro de asistencia.
 */
public class AsistenciaServicio {

    private final AsistenciaRepositorio repositorio;
    private final EstudianteServicio estudianteServicio;
    private final List<RegistroAsistencia> registros;

    public AsistenciaServicio() throws IOException {
        this.repositorio = new AsistenciaRepositorio();
        this.estudianteServicio = new EstudianteServicio();
        this.registros = repositorio.cargar();
    }

    /**
     * Prepara la asistencia de una fecha.
     * Si todavía no existe una sesión, todos empiezan como presentes.
     */
    public List<RegistroAsistencia> obtenerAsistencia(String fecha) {
        validarFecha(fecha);

        List<Estudiante> estudiantes =
                estudianteServicio.listarOrdenado();

        List<RegistroAsistencia> asistencia = new ArrayList<>();

        for (Estudiante estudiante : estudiantes) {

            boolean presente = true;

            for (RegistroAsistencia registro : registros) {
                if (registro.getFecha().equals(fecha)
                        && registro.getCedula().equals(estudiante.getCedula())) {
                    presente = registro.isPresente();
                    break;
                }
            }

            asistencia.add(new RegistroAsistencia(
                    estudiante.getCedula(),
                    fecha,
                    presente
            ));
        }

        return asistencia;
    }

    /**
     * Guarda una sesión completa de asistencia.
     * No permite registrar dos sesiones para la misma fecha.
     */
    public void guardarSesion(
            String fecha,
            List<RegistroAsistencia> asistencia) throws IOException {

        validarFecha(fecha);

        if (asistencia == null || asistencia.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay registros de asistencia para guardar.");
        }

        for (RegistroAsistencia registro : registros) {
            if (registro.getFecha().equals(fecha)) {
                throw new IllegalArgumentException(
                        "Ya existe una sesión de asistencia para esta fecha.");
            }
        }

        List<Estudiante> estudiantes =
                estudianteServicio.listarOrdenado();

        if (asistencia.size() != estudiantes.size()) {
            throw new IllegalArgumentException(
                    "Debe registrar la asistencia de todos los estudiantes.");
        }

        Set<String> cedulasRegistradas = new HashSet<>();

        for (RegistroAsistencia registro : asistencia) {

            if (!registro.getFecha().equals(fecha)) {
                throw new IllegalArgumentException(
                        "La fecha de los registros no coincide con la sesión.");
            }

            if (estudianteServicio.buscarPorCedula(
                    registro.getCedula()) == null) {
                throw new IllegalArgumentException(
                        "Existe una cédula que no pertenece a un estudiante registrado.");
            }

            if (!cedulasRegistradas.add(registro.getCedula())) {
                throw new IllegalArgumentException(
                        "Hay estudiantes repetidos en la asistencia.");
            }
        }

        List<RegistroAsistencia> nuevosRegistros =
                new ArrayList<>(registros);

        nuevosRegistros.addAll(asistencia);

        repositorio.guardar(nuevosRegistros);

        registros.clear();
        registros.addAll(nuevosRegistros);
    }

    /**
     * Comprueba que la fecha tenga el formato correcto.
     */
    private void validarFecha(String fecha) {
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "La fecha debe tener el formato AAAA-MM-DD.");
        }
    }
}
