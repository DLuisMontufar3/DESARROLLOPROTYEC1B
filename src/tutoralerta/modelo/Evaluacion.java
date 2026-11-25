
package tutoralerta.modelo;

/**
 * Representa una evaluación del curso (por ejemplo, "Prueba 1") con su fecha.
 */
public class Evaluacion {

    private final int id;
    private final String nombre;
    private final String fecha;

    public Evaluacion(int id, String nombre, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return nombre + " (" + fecha + ")";
    }
}
