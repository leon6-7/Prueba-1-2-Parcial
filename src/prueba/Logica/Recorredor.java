package explorador.modelo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;

/**
 * Recorre un directorio y todos sus subdirectorios de forma recursiva y construye el árbol
 * de {@link NodoArchivo}. No usa Files.walk ni Files.walkFileTree.
 *
 * Casos límite que resuelve:
 * - Profundidad máxima: 0 significa ilimitada; con n se leen n niveles por debajo de la raíz.
 * - Carpetas sin permiso (listFiles() devuelve null): se omiten y se avisan.
 * - Enlaces simbólicos: se guardan las rutas canónicas ya visitadas, así nunca se entra dos
 *   veces a la misma carpeta y no hay bucles infinitos.
 * - Archivos ocultos y sin extensión: se recorren como cualquier otro archivo.
 *
 * El modelo no conoce Swing: el progreso y la cancelación llegan por interfaces estándar de Java
 * (Consumer y BooleanSupplier) que la capa de control conecta con su SwingWorker.
 */
public class Recorredor {

    private final int profundidadMaxima;
    private final Consumer<File> alEntrarCarpeta;
    private final Consumer<String> alOmitir;
    private final BooleanSupplier cancelado;
    private final Set<String> visitadas = new HashSet<>();

    private volatile int elementosVisitados;
    private volatile int carpetasOmitidas;

    /**
     * @param profundidadMaxima 0 para recorrer todo, o la cantidad de niveles a leer
     * @param alEntrarCarpeta   se llama cada vez que se empieza a leer una carpeta
     * @param alOmitir          se llama con la ruta y el motivo de cada carpeta omitida
     * @param cancelado         se consulta durante el recorrido; si devuelve true se detiene
     */
    public Recorredor(int profundidadMaxima, Consumer<File> alEntrarCarpeta,
                      Consumer<String> alOmitir, BooleanSupplier cancelado) {
        this.profundidadMaxima = profundidadMaxima;
        this.alEntrarCarpeta = alEntrarCarpeta;
        this.alOmitir = alOmitir;
        this.cancelado = cancelado;
    }

    /**
     * Valida el directorio raíz: no vacío, existente y que sea un directorio.
     *
     * @return el directorio como File
     * @throws RutaInvalidaException si no cumple alguna condición
     */
    public static File validarRuta(String ruta) throws RutaInvalidaException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new RutaInvalidaException("La ruta no puede estar vacía.");
        }
        File directorio = new File(ruta.trim());
        if (!directorio.exists()) {
            throw new RutaInvalidaException("La ruta no existe: " + directorio.getPath());
        }
        if (!directorio.isDirectory()) {
            throw new RutaInvalidaException("La ruta no es un directorio: " + directorio.getPath());
        }
        return directorio;
    }

    /** Recorre la raíz y devuelve el árbol. Si se cancela, devuelve el árbol parcial. */
    public NodoArchivo recorrer(File raiz) {
        visitadas.clear();
        elementosVisitados = 0;
        carpetasOmitidas = 0;
        return construir(raiz, 0);
    }

    /** Cantidad de archivos y carpetas encontrados hasta el momento. */
    public int getElementosVisitados() {
        return elementosVisitados;
    }

    /** Cantidad de carpetas que no se pudieron leer (sin permiso o enlaces repetidos). */
    public int getCarpetasOmitidas() {
        return carpetasOmitidas;
    }

    /** Paso recursivo: crea el nodo y, si es una carpeta explorable, recorre sus hijos. */
    private NodoArchivo construir(File actual, int profundidad) {
        NodoArchivo nodo = new NodoArchivo(actual, profundidad);
        elementosVisitados++;

        if (!nodo.esCarpeta()) {
            return nodo;                                    // caso base: un archivo
        }
        if (alcanzoLimite(profundidad) || cancelado.getAsBoolean()) {
            nodo.marcarNoExplorada();
            return nodo;                                    // caso base: no se baja más
        }
        if (yaVisitada(actual)) {
            omitir(nodo, actual, "enlace simbólico o carpeta ya recorrida");
            return nodo;
        }

        alEntrarCarpeta.accept(actual);
        File[] contenido = actual.listFiles();
        if (contenido == null) {
            omitir(nodo, actual, "sin permiso de lectura");
            return nodo;
        }

        Arrays.sort(contenido);
        for (File hijo : contenido) {
            if (cancelado.getAsBoolean()) {
                nodo.marcarNoExplorada();
                break;
            }
            nodo.agregarHijo(construir(hijo, profundidad + 1));
        }
        return nodo;
    }

    private boolean alcanzoLimite(int profundidad) {
        return profundidadMaxima > 0 && profundidad >= profundidadMaxima;
    }

    /** Registra la ruta canónica; devuelve true si ya estaba (o si no se pudo resolver). */
    private boolean yaVisitada(File carpeta) {
        try {
            return !visitadas.add(carpeta.getCanonicalPath());
        } catch (IOException ex) {
            return true;    // si no se puede resolver la ruta real, es más seguro no entrar
        }
    }

    private void omitir(NodoArchivo nodo, File carpeta, String motivo) {
        nodo.marcarNoExplorada();
        carpetasOmitidas++;
        alOmitir.accept(carpeta.getAbsolutePath() + " (" + motivo + ")");
    }
}
