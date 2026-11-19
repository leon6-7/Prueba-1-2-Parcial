package explorador.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Nodo del árbol de directorios que se guarda en memoria después de un recorrido.
 *
 * Guarda una "foto" del archivo o carpeta (tamaño y fecha se leen una sola vez al crear el
 * nodo), de modo que estadísticas, búsqueda y duplicados trabajan sobre el árbol sin volver
 * a leer el disco. Los hijos solo los agrega {@link Recorredor}.
 */
public class NodoArchivo {

    private final File archivo;
    private final boolean carpeta;
    private final int profundidad;
    private final long tamano;
    private final long fechaModificacion;
    private final List<NodoArchivo> hijos = new ArrayList<>();
    private boolean explorada = true;

    /**
     * @param archivo     archivo o carpeta que representa el nodo
     * @param profundidad nivel dentro del árbol (la raíz es 0)
     */
    NodoArchivo(File archivo, int profundidad) {
        this.archivo = archivo;
        this.profundidad = profundidad;
        this.carpeta = archivo.isDirectory();
        this.tamano = carpeta ? 0 : archivo.length();
        this.fechaModificacion = archivo.lastModified();
    }

    /** Agrega un hijo (solo lo usa el Recorredor al construir el árbol). */
    void agregarHijo(NodoArchivo hijo) {
        hijos.add(hijo);
    }

    /**
     * Marca la carpeta como "no explorada": su contenido no se cargó (límite de profundidad,
     * sin permiso, enlace repetido o recorrido cancelado). Una carpeta no explorada no se
     * considera vacía.
     */
    void marcarNoExplorada() {
        explorada = false;
    }

    public File getArchivo() {
        return archivo;
    }

    /** Nombre para mostrar; si es la raíz de una unidad (sin nombre) devuelve la ruta. */
    public String getNombre() {
        String nombre = archivo.getName();
        return nombre.isEmpty() ? archivo.getPath() : nombre;
    }

    public boolean esCarpeta() {
        return carpeta;
    }

    public int getProfundidad() {
        return profundidad;
    }

    /** Tamaño en bytes. Las carpetas devuelven 0. */
    public long getTamano() {
        return tamano;
    }

    /** Fecha de última modificación en milisegundos desde 1970. */
    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public boolean esOculto() {
        return archivo.isHidden();
    }

    /** Hijos del nodo (lista de solo lectura). Vacía para los archivos. */
    public List<NodoArchivo> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    /** true si es una carpeta cuyo contenido se leyó y no tenía ningún elemento. */
    public boolean estaVacia() {
        return carpeta && explorada && hijos.isEmpty();
    }

    /** Extensión en minúsculas y sin punto; cadena vacía para carpetas y archivos sin extensión. */
    public String getExtension() {
        return carpeta ? "" : extensionDe(getNombre());
    }

    /**
     * Extrae la extensión de un nombre. Un nombre que empieza con punto y no tiene otro
     * (".gitignore") o que termina en punto se considera sin extensión.
     */
    public static String extensionDe(String nombre) {
        int punto = nombre.lastIndexOf('.');
        if (punto <= 0 || punto == nombre.length() - 1) {
            return "";
        }
        return nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
