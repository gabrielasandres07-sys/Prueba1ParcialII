# Explorador, analizador y gestor de archivos

Aplicación de escritorio en Java (Swing) que explora un directorio y todas sus subcarpetas,
saca estadísticas, busca archivos con filtros, detecta duplicados, permite crear, renombrar,
copiar, mover y eliminar archivos, y guarda una bitácora de todo lo que se hace.

**Estudiante:** Gabriela Sandres
**Clase:** Programación II


## Requisitos

- JDK 21 o superior
- NetBeans (el proyecto es de NetBeans con Ant)
- No usa librerías externas



## Estructura del proyecto

```
src/
├── Main/
│   └── Main.java                  -> inicia la aplicación
├── modelo/                        -> lógica (no usa nada de javax.swing)
│   ├── Analizador.java            -> recorridos recursivos: árbol, estadísticas, búsqueda, duplicados
│   ├── ArchivoException.java      -> excepción personalizada para operaciones con archivos
│   ├── Bitacora.java              -> bitacora.log (modo append)
│   ├── Configuracion.java         -> config.txt (última ruta y filtros)
│   ├── Estadisticas.java          -> resultados del análisis y reportes .txt / .csv
│   ├── FiltroBusqueda.java        -> filtros de texto, extensión, tamaño y fecha
│   ├── GestorArchivos.java        -> crear, renombrar, copiar, mover, eliminar, leer y guardar texto
│   ├── NodoArchivo.java           -> nodo del árbol
│   ├── Recorrido.java             -> profundidad, ocultos, enlaces simbólicos, permisos y cancelar
│   ├── RutaInvalidaException.java -> excepción personalizada para rutas no válidas
│   └── Utilidades.java            -> validar ruta, formato de tamaño y fecha, extensión
├── vista/
│   └── VentanaPrincipal.java      -> ventana, árbol, tablas, pestañas y diálogos
└── controlador/
    └── Controlador.java           -> une la vista con el modelo y usa SwingWorker
```

## Cómo usar

1. Escribir la ruta del directorio raíz o elegirla con **Examinar...**
2. (Opcional) Poner la profundidad máxima (0 = ilimitada) y marcar **Incluir ocultos**.
3. Botones principales:
   - **Cargar arbol**: muestra la estructura de carpetas y archivos.
   - **Estadisticas**: cantidad, tamaño y porcentaje de .txt, .java, .pdf y otros, además del archivo más
     grande, el más reciente, la profundidad máxima, el número de carpetas y las carpetas vacías.
   - **Buscar**: busca por nombre (sin importar mayúsculas) combinado con extensión, tamaño mínimo y máximo
     en KB y rango de fechas `dd/MM/yyyy`. La tabla se ordena haciendo clic en las columnas.
   - **Duplicados**: archivos con el mismo nombre y el mismo tamaño en carpetas distintas.
   - **Cancelar**: detiene el recorrido que está en curso.
4. Gestión (seleccionar primero un elemento en el árbol o en la tabla de búsqueda):
   **Nueva carpeta** (acepta anidadas como `a/b/c`), **Nuevo archivo**, **Renombrar**, **Copiar**,
   **Mover**, **Eliminar** y **Abrir en editor**.
5. Pestaña **Editor**: editar el archivo y presionar **Guardar**.
6. Pestaña **Estadisticas** > **Exportar reporte**: guarda un `.txt` o un `.csv` con fecha y hora.
7. Pestaña **Bitacora** > **Actualizar bitacora**: muestra el historial de operaciones.



