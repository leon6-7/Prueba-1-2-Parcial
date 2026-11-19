package prueba.Logica;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
/**
 *
 * @author Abril
 */
public class Estadisticas {

    public enum Categoria {
        TXT, JAVA, PDF, OTROS
    }

    public static class DatoCategoria {
        private int cantidad;
        private long tamanoTotal;

        public int getCantidad() { return cantidad; }
        public long getTamanoTotal() { return tamanoTotal; }
        public double getPorcentaje(int totalArchivos) {
            return totalArchivos == 0 ? 0 : cantidad * 100.0 / totalArchivos;
        }

        private void agregar(long tamano) {
            cantidad++;
            tamanoTotal += tamano;
        }
    }

    private final Map<Categoria, DatoCategoria> datos;
    private int totalArchivos;
    private int totalCarpetas;
    private int carpetasVacias;
    private int profundidadMaxima;
    private File archivoMasGrande;
    private long tamanoMayor = -1;
    private File archivoMasReciente;
    private long fechaMasReciente = Long.MIN_VALUE;

    public Estadisticas() {
        datos = new EnumMap<>(Categoria.class);
        for (Categoria categoria : Categoria.values()) {
            datos.put(categoria, new DatoCategoria());
        }
    }

    public void calcular(NodoArchivo raiz) {
        reiniciar();
        if (raiz != null) {
            acumular(raiz);
        }
    }

    private void reiniciar() {
        datos.clear();
        for (Categoria categoria : Categoria.values()) {
            datos.put(categoria, new DatoCategoria());
        }
        totalArchivos = 0;
        totalCarpetas = 0;
        carpetasVacias = 0;
        profundidadMaxima = 0;
        archivoMasGrande = null;
        tamanoMayor = -1;
        archivoMasReciente = null;
        fechaMasReciente = Long.MIN_VALUE;
    }

    private void acumular(NodoArchivo nodo) {
        profundidadMaxima = Math.max(profundidadMaxima, nodo.getProfundidad());
        if (nodo.esCarpeta()) {
            totalCarpetas++;
            if (nodo.estaVacia()) {
                carpetasVacias++;
            }
            for (NodoArchivo hijo : nodo.getHijos()) {
                acumular(hijo);
            }
        } else {
            totalArchivos++;
            Categoria categoria = obtenerCategoria(nodo.getExtension());
            datos.get(categoria).agregar(nodo.getTamano());
            if (nodo.getTamano() > tamanoMayor) {
                tamanoMayor = nodo.getTamano();
                archivoMasGrande = nodo.getArchivo();
            }
            if (nodo.getFechaModificacion() > fechaMasReciente) {
                fechaMasReciente = nodo.getFechaModificacion();
                archivoMasReciente = nodo.getArchivo();
            }
        }
    }

    private Categoria obtenerCategoria(String extension) {
        if ("txt".equalsIgnoreCase(extension)) return Categoria.TXT;
        if ("java".equalsIgnoreCase(extension)) return Categoria.JAVA;
        if ("pdf".equalsIgnoreCase(extension)) return Categoria.PDF;
        return Categoria.OTROS;
    }

    public DatoCategoria getDato(Categoria categoria) { return datos.get(categoria); }
    public Map<Categoria, DatoCategoria> getDatos() { return new EnumMap<>(datos); }
    
    public int getTotalArchivos(){
        return totalArchivos; 
    }
    public int getTotalCarpetas(){
        return totalCarpetas; 
    }
    public int getCarpetasVacias(){
        return carpetasVacias;
    }
    public int getProfundidadMaxima(){
        return profundidadMaxima;
    }
    public File getArchivoMasGrande(){ 
        return archivoMasGrande;
    }
    public long getTamanoMayor() {
        return Math.max(0, tamanoMayor);
    }
    public File getArchivoMasReciente(){
        return archivoMasReciente;
    }
    public long getFechaMasReciente(){
        return archivoMasReciente == null ? 0 : fechaMasReciente;
    }

    public static String formatoTamano(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format(java.util.Locale.ROOT, "%.2f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format(java.util.Locale.ROOT, "%.2f MB", mb);
        return String.format(java.util.Locale.ROOT, "%.2f GB", mb / 1024.0);
    }
}
