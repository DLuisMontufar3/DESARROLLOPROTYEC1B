
package tutoralerta.servicio;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tutoralerta.modelo.Estudiante;
import tutoralerta.modelo.Evaluacion;
import tutoralerta.modelo.Nota;
import tutoralerta.persistencia.EvaluacionRepositorio;
import tutoralerta.persistencia.NotaRepositorio;

/**
 * HU-B3: reglas para registrar y consultar las notas de una evaluación.
 */
public class NotaServicio {

    /**
     * Rango de calificaciones. Regla de negocio general: 0 a 20.
     * (El criterio de HU-B3 menciona 0 a 10: pendiente de confirmar con la
     * docente. Si prevalece 0-10, solo hay que cambiar NOTA_MAXIMA.)
     */
    public static final BigDecimal NOTA_MINIMA = BigDecimal.ZERO;
    public static final BigDecimal NOTA_MAXIMA = new BigDecimal("20");

    private final EvaluacionRepositorio evaluacionRepositorio;
    private final NotaRepositorio notaRepositorio;
    private final EstudianteServicio estudianteServicio;

    private final List<Evaluacion> evaluaciones;
    private final List<Nota> notas;

    public NotaServicio() throws IOException {
        this(new EvaluacionRepositorio(), new NotaRepositorio(),
                new EstudianteServicio());
    }

    public NotaServicio(EvaluacionRepositorio evaluacionRepositorio,
            NotaRepositorio notaRepositorio,
            EstudianteServicio estudianteServicio) throws IOException {
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.notaRepositorio = notaRepositorio;
        this.estudianteServicio = estudianteServicio;
        this.evaluaciones = evaluacionRepositorio.cargar();
        this.notas = notaRepositorio.cargar();
    }

    /** Evaluaciones guardadas, las más recientes primero. */
    public List<Evaluacion> listarEvaluaciones() {
        List<Evaluacion> copia = new ArrayList<>(evaluaciones);
        copia.sort(Comparator.comparing(Evaluacion::getFecha).reversed()
                .thenComparing(Evaluacion::getNombre));
        return copia;
    }

    /** Busca una evaluación por nombre y fecha; devuelve null si no existe. */
    public Evaluacion buscarEvaluacion(String nombre, String fecha) {
        String n = limpiar(nombre);
        String f = limpiar(fecha);
        for (Evaluacion e : evaluaciones) {
            if (e.getNombre().equalsIgnoreCase(n) && e.getFecha().equals(f)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Devuelve una nota por cada estudiante del curso (ordenados por apellido).
     * Si la evaluación ya existe, trae las notas guardadas; si no, el valor
     * de cada nota queda en null para que el tutor la ingrese.
     */
    public List<Nota> obtenerNotas(String nombre, String fecha) {
        validarEvaluacion(nombre, fecha);

        Evaluacion evaluacion = buscarEvaluacion(nombre, fecha);
        int id = evaluacion == null ? 0 : evaluacion.getId();

        List<Nota> resultado = new ArrayList<>();
        for (Estudiante estudiante : estudianteServicio.listarOrdenado()) {
            BigDecimal valor = null;
            if (evaluacion != null) {
                for (Nota n : notas) {
                    if (n.getIdEvaluacion() == id
                            && n.getCedula().equals(estudiante.getCedula())) {
                        valor = n.getValor();
                        break;
                    }
                }
            }
            resultado.add(new Nota(id, estudiante.getCedula(), valor));
        }
        return resultado;
    }

    /**
     * Guarda las notas de toda la evaluación. Recibe el texto escrito por el
     * tutor (cédula -> nota). Si algún dato es inválido NO se guarda nada y
     * el mensaje indica qué estudiante debe corregirse.
     * Si la evaluación ya existía, sus notas se actualizan.
     */
    public void guardarNotas(String nombre, String fecha,
            Map<String, String> notasPorCedula) throws IOException {

        validarEvaluacion(nombre, fecha);

        List<Estudiante> estudiantes = estudianteServicio.listarOrdenado();

        if (notasPorCedula == null || notasPorCedula.size() != estudiantes.size()) {
            throw new IllegalArgumentException(
                    "Debe registrar la nota de todos los estudiantes del curso.");
        }

        // Valida todas las notas antes de guardar nada.
        List<String> errores = new ArrayList<>();
        List<Nota> nuevas = new ArrayList<>();
        Set<String> cedulasVistas = new HashSet<>();

        for (Estudiante e : estudiantes) {
            if (!notasPorCedula.containsKey(e.getCedula())
                    || !cedulasVistas.add(e.getCedula())) {
                throw new IllegalArgumentException(
                        "Las notas no corresponden a los estudiantes registrados.");
            }
            try {
                BigDecimal valor = validarNota(notasPorCedula.get(e.getCedula()));
                nuevas.add(new Nota(0, e.getCedula(), valor));
            } catch (IllegalArgumentException ex) {
                errores.add(e.getApellidos() + " " + e.getNombres()
                        + ": " + ex.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Corrige las siguientes notas:\n- " + String.join("\n- ", errores));
        }

        // Crea la evaluación si no existe.
        Evaluacion evaluacion = buscarEvaluacion(nombre, fecha);
        boolean esNueva = evaluacion == null;
        List<Evaluacion> nuevasEvaluaciones = new ArrayList<>(evaluaciones);
        if (esNueva) {
            evaluacion = new Evaluacion(siguienteId(), limpiar(nombre), limpiar(fecha));
            nuevasEvaluaciones.add(evaluacion);
        }

        // Reemplaza las notas anteriores de esa evaluación.
        List<Nota> nuevasNotas = new ArrayList<>();
        for (Nota n : notas) {
            if (n.getIdEvaluacion() != evaluacion.getId()) {
                nuevasNotas.add(n);
            }
        }
        for (Nota n : nuevas) {
            nuevasNotas.add(new Nota(evaluacion.getId(), n.getCedula(), n.getValor()));
        }

        // Guarda primero; solo si todo salió bien se actualiza la memoria.
        notaRepositorio.guardar(nuevasNotas);
        if (esNueva) {
            try {
                evaluacionRepositorio.guardar(nuevasEvaluaciones);
            } catch (IOException ex) {
                notaRepositorio.guardar(notas); // deshace el guardado de notas
                throw ex;
            }
        }

        evaluaciones.clear();
        evaluaciones.addAll(nuevasEvaluaciones);
        notas.clear();
        notas.addAll(nuevasNotas);
    }

    /**
     * Valida el texto de una nota: obligatoria, numérica, sin negativos,
     * máximo dos decimales y dentro del rango permitido.
     * Acepta coma o punto como separador decimal.
     */
    public BigDecimal validarNota(String texto) {
        String t = limpiar(texto).replace(',', '.');

        if (t.isEmpty()) {
            throw new IllegalArgumentException("la nota está vacía.");
        }
        if (t.startsWith("-")) {
            throw new IllegalArgumentException("la nota no puede ser negativa.");
        }
        if (!t.matches("[0-9]+(\\.[0-9]+)?")) {
            throw new IllegalArgumentException("la nota debe ser un número (sin letras).");
        }
        if (t.matches("[0-9]+\\.[0-9]{3,}")) {
            throw new IllegalArgumentException("la nota admite máximo dos decimales.");
        }

        BigDecimal valor = new BigDecimal(t);
        if (valor.compareTo(NOTA_MINIMA) < 0 || valor.compareTo(NOTA_MAXIMA) > 0) {
            throw new IllegalArgumentException("la nota debe estar entre "
                    + NOTA_MINIMA.toPlainString() + " y "
                    + NOTA_MAXIMA.toPlainString() + ".");
        }
        return valor.setScale(2);
    }

    private void validarEvaluacion(String nombre, String fecha) {
        String n = limpiar(nombre);
        if (n.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la evaluación es obligatorio.");
        }
        if (n.contains(",")) {
            throw new IllegalArgumentException("No se permiten comas en el nombre.");
        }
        try {
            LocalDate.parse(limpiar(fecha));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha debe tener el formato AAAA-MM-DD.");
        }
    }

    private int siguienteId() {
        int max = 0;
        for (Evaluacion e : evaluaciones) {
            max = Math.max(max, e.getId());
        }
        return max + 1;
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
