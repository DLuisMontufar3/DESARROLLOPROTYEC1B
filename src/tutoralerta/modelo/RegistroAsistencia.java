
package tutoralerta.modelo;

/**
 * Representa la asistencia de un estudiante en una fecha determinada.
 */
public class RegistroAsistencia {

    private final String cedula;
    private final String fecha;
    private boolean presente;

    public RegistroAsistencia(String cedula, String fecha, boolean presente) {
        this.cedula = cedula;
        this.fecha = fecha;
        this.presente = presente;
    }

    public String getCedula() {
        return cedula;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}
