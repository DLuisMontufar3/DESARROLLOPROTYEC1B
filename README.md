# TutorAlerta — Sprint 1

## 1. Información del proyecto

- **Equipo:** Equipo 4.
- **Escenario B:** Sistema de alerta temprana de riesgo académico.
- **Institución:** Escuela Politécnica Nacional.
- **Sprint:** 1.

## 2. Descripción

TutorAlerta es una aplicación de escritorio desarrollada en Java que permite al tutor gestionar los estudiantes, registrar la asistencia y almacenar las calificaciones de un curso.

El Sprint 1 establece la base del sistema de seguimiento académico mediante la implementación de tres historias de usuario y la persistencia de información en archivos CSV.

## 3. Integrantes y roles

| Integrante | Nombre | Rol | Responsabilidad |
|---|---|---|---|
| 1 | Luis Montufar | Product Owner y desarrollador | HU-B1: Registro de estudiantes |
| 2 | Damian Ushiña | Scrum Master y desarrollador | HU-B2: Registro de asistencia |
| 3 | Vianca Guzman | Desarrolladora | HU-B3: Registro de notas |
| 4 | Anthony Cuenca | Desarrollador | Integración, pruebas, ejecutable JAR y documentación |

## 4. Historias de usuario implementadas

### HU-B1 — Registro de estudiantes

- Registro de estudiantes mediante cédula, nombres, apellidos y correo electrónico.
- Validación de cédula única de 10 dígitos.
- Visualización de estudiantes ordenados por apellido.
- Edición de datos personales sin modificar la cédula.
- Persistencia de registros.

### HU-B2 — Registro de asistencia

- Selección de la fecha de la sesión.
- Carga de estudiantes registrados.
- Marcación de estudiantes presentes y ausentes.
- Registro de asistencia de todo el curso.
- Prevención de sesiones duplicadas por fecha.
- Recuperación de sesiones almacenadas.

### HU-B3 — Registro de notas

- Registro del nombre y fecha de una evaluación.
- Carga de estudiantes del curso.
- Ingreso de calificaciones individuales.
- Validación de valores numéricos y hasta dos decimales.
- Almacenamiento y recuperación de evaluaciones y calificaciones.

La escala implementada es de 0 a 20, conforme a la regla general del escenario B. Su confirmación definitiva está pendiente debido a una discrepancia en el criterio de aceptación de HU-B3.

## 5. Tecnologías utilizadas

- Java 21.
- Java Swing.
- Apache NetBeans.
- Apache Ant.
- Archivos CSV.
- Git y GitHub.
- Trello para la gestión del Sprint mediante Scrum.

## 6. Estructura del proyecto

```text
DESARROLLOPROTYEC1B/
├── datos/
│   ├── estudiantes.csv
│   ├── asistencia.csv
│   ├── evaluaciones.csv
│   └── notas.csv
├── dist/
│   └── TutorAlerta.jar
├── nbproject/
├── src/
│   └── tutoralerta/
│       ├── app/
│       ├── modelo/
│       ├── persistencia/
│       ├── servicio/
│       └── vista/
├── build.xml
├── manifest.mf
├── .gitignore
└── README.md
```

### Organización de paquetes

- **app:** punto de entrada de la aplicación.
- **modelo:** entidades del sistema.
- **persistencia:** almacenamiento y recuperación de información mediante CSV.
- **servicio:** reglas de negocio y validaciones.
- **vista:** interfaces gráficas desarrolladas con Swing.

## 7. Requisitos de ejecución

- Java 21 o una versión posterior compatible.
- Entorno gráfico de escritorio.

No es necesario instalar NetBeans para ejecutar el archivo JAR.

## 8. Instrucciones de ejecución

### Clonar el repositorio

```bash
git clone https://github.com/DLuisMontufar3/DESARROLLOPROTYEC1B.git
cd DESARROLLOPROTYEC1B
```

### Verificar la versión de Java

```bash
java -version
```

### Ejecutar la aplicación

Desde la carpeta raíz del proyecto:

```bash
java -jar dist/TutorAlerta.jar
```

El menú principal permite acceder a los módulos de estudiantes, asistencia y notas.

La aplicación debe ejecutarse desde la carpeta raíz para acceder correctamente al directorio relativo `datos/`.

## 9. Archivos de persistencia

| Archivo | Información almacenada |
|---|---|
| estudiantes.csv | Cédula, nombres, apellidos y correo electrónico |
| asistencia.csv | Cédula, fecha y estado de asistencia |
| evaluaciones.csv | Identificador, nombre y fecha de evaluación |
| notas.csv | Identificador de evaluación, cédula y calificación |

La aplicación conserva los registros después de cerrar y volver a iniciar el programa.

## 10. Datos de prueba

El proyecto incluye un conjunto de datos para comprobar el funcionamiento de los tres módulos:

- 8 estudiantes registrados.
- 1 sesión de asistencia.
- 6 estudiantes presentes y 2 ausentes.
- 1 evaluación registrada.
- 8 calificaciones almacenadas.

## 11. Pruebas realizadas

Durante la integración se realizaron verificaciones de:

- Navegación entre los tres módulos.
- Registro y edición de estudiantes.
- Validación de cédulas.
- Registro de asistencia.
- Prevención de sesiones duplicadas.
- Validación de calificaciones.
- Persistencia e integridad de los archivos CSV.
- Ejecución del archivo JAR fuera del entorno de desarrollo.

## 12. Gestión del proyecto

El equipo utiliza Scrum para organizar el desarrollo del Sprint 1.

El tablero de Trello contiene las tareas, responsables, etiquetas por historia de usuario, criterios de finalización y seguimiento de las revisiones.

**Tablero de Trello:** https://trello.com/b/XTIZYc2v/tutoralerta-sprint-1

**Repositorio GitHub:** https://github.com/DLuisMontufar3/DESARROLLOPROTYEC1B

---

Equipo 4 — Escuela Politécnica Nacional.
