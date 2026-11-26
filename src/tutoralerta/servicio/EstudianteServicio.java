package tutoralerta.servicio;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import tutoralerta.modelo.Estudiante;
import tutoralerta.persistencia.EstudianteRepositorio;

/**
 * (registrar y editar estudiantes).
 */
public class EstudianteServicio {

    private static final String MSG_VACIOS = "Todos los campos son obligatorios.";

    // Correo válido: texto antes de la arroba + "@gmail.com" (sin espacios).
    private static final Pattern CORREO_GMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@gmail\\.com$", Pattern.CASE_INSENSITIVE);

    private final EstudianteRepositorio repositorio;
    private final List<Estudiante> estudiantes; 

    // Al crear el servicio se cargan los estudiantes que ya estaban guardados.
    public EstudianteServicio() throws IOException {
        this.repositorio = new EstudianteRepositorio();
        this.estudiantes = repositorio.cargar();
    }

    /** Registra un estudiante nuevo si cumple las reglas. */
    public void registrar(String cedula, String nombres, String apellidos, String correo)
            throws IOException {
        cedula = limpiar(cedula);
        nombres = limpiar(nombres);
        apellidos = limpiar(apellidos);
        correo = limpiar(correo);

        if (cedula.isEmpty()) {
            throw new IllegalArgumentException(MSG_VACIOS);
        }
        validarTextos(nombres, apellidos, correo);

        if (!cedula.matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                    "La cédula debe tener exactamente 10 dígitos (solo números).");
        }
        if (buscarPorCedula(cedula) != null) {
            throw new IllegalArgumentException("Ya existe un estudiante con esa cédula.");
        }

        Estudiante nuevo = new Estudiante(cedula, nombres, apellidos, correo);
        estudiantes.add(nuevo);
        try {
            repositorio.guardar(estudiantes);
        } catch (IOException ex) {
            estudiantes.remove(nuevo); 
            throw ex;
        }
    }

    /** Edicion de nombres, apellidos y correo. **/
    public void editar(String cedula, String nombres, String apellidos, String correo)
            throws IOException {
        Estudiante e = buscarPorCedula(cedula);
        if (e == null) {
            throw new IllegalArgumentException("No existe un estudiante con esa cédula.");
        }

        nombres = limpiar(nombres);
        apellidos = limpiar(apellidos);
        correo = limpiar(correo);
        validarTextos(nombres, apellidos, correo);

        String nombresAntes = e.getNombres();
        String apellidosAntes = e.getApellidos();
        String correoAntes = e.getCorreo();

        e.setNombres(nombres);
        e.setApellidos(apellidos);
        e.setCorreo(correo);
        try {
            repositorio.guardar(estudiantes);
        } catch (IOException ex) {
            e.setNombres(nombresAntes); 
            e.setApellidos(apellidosAntes);
            e.setCorreo(correoAntes);
            throw ex;
        }
    }

    /**  Estudiantes ordenados por apellido  */
    public List<Estudiante> listarOrdenado() {
        List<Estudiante> copia = new ArrayList<>(estudiantes); // copia: nadie toca la lista interna
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-EC"));
        collator.setStrength(Collator.PRIMARY); // ignora tildes y mayúsculas al ordenar
        copia.sort((a, b) -> {
            int r = collator.compare(a.getApellidos(), b.getApellidos());
            return r != 0 ? r : collator.compare(a.getNombres(), b.getNombres());
        });
        return copia;
    }

    /** Busca por cédula. **/
    public Estudiante buscarPorCedula(String cedula) {
        String buscada = limpiar(cedula);
        for (Estudiante e : estudiantes) {
            if (e.getCedula().equals(buscada)) {
                return e;
            }
        }
        return null;
    }


    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    // Nombres, apellidos y correo
    private void validarTextos(String nombres, String apellidos, String correo) {
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            throw new IllegalArgumentException(MSG_VACIOS);
        }
        if (nombres.contains(",") || apellidos.contains(",") || correo.contains(",")) {
            throw new IllegalArgumentException("No se permiten comas en los datos.");
        }
        if (!CORREO_GMAIL.matcher(correo).matches()) {
            throw new IllegalArgumentException(
                    "El correo no es válido. Debe tener el formato nombre@gmail.com "
                    + "(sin espacios y terminando en @gmail.com).");
        }
    }
}

