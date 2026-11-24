
package tutoralerta.modelo;

import java.math.BigDecimal;

/**
 * Representa la calificación de un estudiante
 * en una evaluación del curso.
 */
public class Nota {

    private final int idEvaluacion;
    private final String cedula;
    private final BigDecimal valor;

    public Nota(int idEvaluacion, String cedula, BigDecimal valor) {
        this.idEvaluacion = idEvaluacion;
        this.cedula = cedula;
        this.valor = valor;
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public String getCedula() {
        return cedula;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
