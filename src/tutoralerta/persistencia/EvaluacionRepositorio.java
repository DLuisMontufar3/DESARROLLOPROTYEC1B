
package tutoralerta.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tutoralerta.modelo.Evaluacion;

/**
 * Guarda y recupera las evaluaciones desde un archivo CSV.
 * Formato (primera línea = encabezado):
 *   id,nombre,fecha
 */
public class EvaluacionRepositorio {

    private static final String ENCABEZADO = "id,nombre,fecha";

    private final Path archivo;

    public EvaluacionRepositorio() {
        this("datos/evaluaciones.csv");
    }

    public EvaluacionRepositorio(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    public List<Evaluacion> cargar() throws IOException {
        List<Evaluacion> evaluaciones = new ArrayList<>();

        if (!Files.exists(archivo)) {
            return evaluaciones;
        }

        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i).replace("\uFEFF", "").trim();

            if (linea.isEmpty() || (i == 0 && linea.equals(ENCABEZADO))) {
                continue;
            }

            String[] campos = linea.split(",", -1);
            if (campos.length != 3) {
                continue;
            }

            try {
                evaluaciones.add(new Evaluacion(
                        Integer.parseInt(campos[0].trim()),
                        campos[1].trim(),
                        campos[2].trim()));
            } catch (NumberFormatException ex) {
                // Línea dañada: se ignora.
            }
        }

        return evaluaciones;
    }

    /** Escribe TODA la lista en el archivo. */
    public void guardar(List<Evaluacion> evaluaciones) throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);

        for (Evaluacion e : evaluaciones) {
            lineas.add(e.getId() + "," + e.getNombre() + "," + e.getFecha());
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }
}
