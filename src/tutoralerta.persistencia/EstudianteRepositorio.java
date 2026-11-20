package tutoralerta.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tutoralerta.modelo.Estudiante;

/**
 * Formato del archivo (primera línea = encabezado):
 *   cedula,nombres,apellidos,correo
 */
public class EstudianteRepositorio {

    private static final String ENCABEZADO = "cedula,nombres,apellidos,correo";

    private final Path archivo;

    public EstudianteRepositorio() {
        this("datos/estudiantes.csv");
    }

    public EstudianteRepositorio(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    public List<Estudiante> cargar() throws IOException {
        List<Estudiante> estudiantes = new ArrayList<>();

        if (!Files.exists(archivo)) {
            return estudiantes;
        }

        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);

            if (i == 0) {
                // Quita una marca invisible que a veces agregan los editores al inicio.
                linea = linea.replace("\uFEFF", "");
                if (linea.startsWith("cedula")) {
                    continue;
                }
            }

            if (linea.trim().isEmpty()) {
                continue; 
            }

            String[] campos = linea.split(",", -1);
            if (campos.length < 4) {
                continue; 
            }

            estudiantes.add(new Estudiante(
                    campos[0].trim(),
                    campos[1].trim(),
                    campos[2].trim(),
                    campos[3].trim()));
        }

        return estudiantes;
    }

    /**
     * Escribe TODA la lista en el archivo 
     */
    public void guardar(List<Estudiante> estudiantes) throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);

        for (Estudiante e : estudiantes) {
            lineas.add(e.getCedula() + "," + e.getNombres() + ","
                    + e.getApellidos() + "," + e.getCorreo());
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }
}
