
package tutoralerta.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tutoralerta.modelo.RegistroAsistencia;

/**
 * Guarda y recupera los registros de asistencia desde un archivo CSV.
 */
public class AsistenciaRepositorio {

    private static final String ENCABEZADO = "cedula,fecha,presente";

    private final Path archivo;

    public AsistenciaRepositorio() {
        this("datos/asistencia.csv");
    }

    public AsistenciaRepositorio(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    /**
     * Carga los registros guardados.
     */
    public List<RegistroAsistencia> cargar() throws IOException {
        List<RegistroAsistencia> registros = new ArrayList<>();

        if (!Files.exists(archivo)) {
            return registros;
        }

        List<String> lineas = Files.readAllLines(
                archivo, StandardCharsets.UTF_8);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();

            if (linea.isEmpty()) {
                continue;
            }

            if (i == 0 && linea.replace("\uFEFF", "").equals(ENCABEZADO)) {
                continue;
            }

            String[] campos = linea.split(",", -1);

            if (campos.length != 3) {
                continue;
            }

            String cedula = campos[0].trim();
            String fecha = campos[1].trim();
            String valorPresente = campos[2].trim();

            if (!valorPresente.equalsIgnoreCase("true")
                    && !valorPresente.equalsIgnoreCase("false")) {
                continue;
            }

            registros.add(new RegistroAsistencia(
                    cedula,
                    fecha,
                    Boolean.parseBoolean(valorPresente)
            ));
        }

        return registros;
    }

    /**
     * Guarda todos los registros en el archivo CSV.
     */
    public void guardar(List<RegistroAsistencia> registros)
            throws IOException {

        Path carpeta = archivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);

        for (RegistroAsistencia registro : registros) {
            lineas.add(
                    registro.getCedula() + ","
                    + registro.getFecha() + ","
                    + registro.isPresente()
            );
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }
}
