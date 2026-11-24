
package tutoralerta.persistencia;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tutoralerta.modelo.Nota;

/**
 * Guarda y recupera las notas desde un archivo CSV.
 * Formato (primera línea = encabezado):
 *   idEvaluacion,cedula,nota
 */
public class NotaRepositorio {

    private static final String ENCABEZADO = "idEvaluacion,cedula,nota";

    private final Path archivo;

    public NotaRepositorio() {
        this("datos/notas.csv");
    }

    public NotaRepositorio(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    public List<Nota> cargar() throws IOException {
        List<Nota> notas = new ArrayList<>();

        if (!Files.exists(archivo)) {
            return notas;
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
                notas.add(new Nota(
                        Integer.parseInt(campos[0].trim()),
                        campos[1].trim(),
                        new BigDecimal(campos[2].trim())));
            } catch (NumberFormatException ex) {
                // Línea dañada: se ignora.
            }
        }

        return notas;
    }

    /** Escribe TODAS las notas en el archivo. */
    public void guardar(List<Nota> notas) throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);

        for (Nota n : notas) {
            lineas.add(n.getIdEvaluacion() + "," + n.getCedula() + ","
                    + n.getValor().toPlainString());
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }
}
