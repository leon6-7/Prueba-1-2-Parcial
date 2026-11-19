package prueba.Logica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 *
 * @author Abril
 */
public class Persistencia {

    private final File archivoBitacora;
    private final File archivoConfiguracion;

    public Persistencia(File carpetaDatos) throws RutaInvalidaException {
        if (carpetaDatos == null) throw new RutaInvalidaException("Debe indicar la carpeta de datos.");
        if (!carpetaDatos.exists() && !carpetaDatos.mkdirs()) {
            throw new RutaInvalidaException("No se pudo crear la carpeta de datos.");
        }
        if (!carpetaDatos.isDirectory()) throw new RutaInvalidaException("La ubicación de datos no es una carpeta.");
        archivoBitacora = new File(carpetaDatos, "bitacora.log");
        archivoConfiguracion = new File(carpetaDatos, "config.txt");
    }

    public Persistencia() throws RutaInvalidaException {
        this(new File("."));
    }

    public void registrarOperacion(String accion, String ruta) throws RutaInvalidaException {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoBitacora, true))) {
            escritor.write(fecha + " | " + limpiar(accion) + " | " + limpiar(ruta));
            escritor.newLine();
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo escribir la bitácora: " + ex.getMessage());
        }
    }

    public String leerBitacora() throws RutaInvalidaException {
        if (!archivoBitacora.exists()) return "";
        StringBuilder texto = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoBitacora))) {
            String linea;
            while ((linea = lector.readLine()) != null) texto.append(linea).append(System.lineSeparator());
            return texto.toString();
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo leer la bitácora: " + ex.getMessage());
        }
    }

    public void guardarConfiguracion(String ruta, Filtro filtro) throws RutaInvalidaException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoConfiguracion))) {
            escritor.write("ruta=" + limpiar(ruta)); escritor.newLine();
            if (filtro != null) {
                escritor.write("texto=" + limpiar(filtro.getTexto())); escritor.newLine();
                escritor.write("extension=" + limpiar(filtro.getExtension())); escritor.newLine();
                escritor.write("tamanoMinimoKB=" + filtro.getTamanoMinimoKB()); escritor.newLine();
                escritor.write("tamanoMaximoKB=" + filtro.getTamanoMaximoKB()); escritor.newLine();
                escritor.write("fechaDesde=" + fechaComoTexto(filtro.getFechaDesde())); escritor.newLine();
                escritor.write("fechaHasta=" + fechaComoTexto(filtro.getFechaHasta())); escritor.newLine();
            }
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo guardar la configuración: " + ex.getMessage());
        }
    }

    public Map<String, String> leerConfiguracion() throws RutaInvalidaException {
        Map<String, String> configuracion = new java.util.HashMap<>();
        if (!archivoConfiguracion.exists()) return configuracion;
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoConfiguracion))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                int separador = linea.indexOf('=');
                if (separador > 0) configuracion.put(linea.substring(0, separador), linea.substring(separador + 1));
            }
            return configuracion;
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo leer la configuración: " + ex.getMessage());
        }
    }

    public void exportarReporte(File destino, Estadisticas estadisticas) throws RutaInvalidaException {
        if (destino == null || estadisticas == null) throw new RutaInvalidaException("Faltan datos para exportar el reporte.");
        String extension = NodoArchivo.extensionDe(destino.getName());
        if (!extension.equals("txt") && !extension.equals("csv")) {
            throw new RutaInvalidaException("El reporte debe tener extensión .txt o .csv.");
        }
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(destino))) {
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            boolean csv = extension.equals("csv");
            if (csv) {
                escritor.write("Fecha,Categoria,Cantidad,Tamano total,Porcentaje"); escritor.newLine();
                for (Estadisticas.Categoria categoria : Estadisticas.Categoria.values()) {
                    Estadisticas.DatoCategoria dato = estadisticas.getDato(categoria);
                    escritor.write(fecha + "," + categoria + "," + dato.getCantidad() + ","
                            + dato.getTamanoTotal() + "," + String.format(java.util.Locale.ROOT, "%.2f", dato.getPorcentaje(estadisticas.getTotalArchivos())));
                    escritor.newLine();
                }
                escritor.write("Global,Archivos," + estadisticas.getTotalArchivos()); escritor.newLine();
                escritor.write("Global,Carpetas," + estadisticas.getTotalCarpetas()); escritor.newLine();
                escritor.write("Global,Carpetas vacias," + estadisticas.getCarpetasVacias()); escritor.newLine();
                escritor.write("Global,Profundidad maxima," + estadisticas.getProfundidadMaxima()); escritor.newLine();
                escritor.write("Global,Archivo mas grande," + ruta(estadisticas.getArchivoMasGrande())); escritor.newLine();
                escritor.write("Global,Archivo mas reciente," + ruta(estadisticas.getArchivoMasReciente())); escritor.newLine();
            } else {
                escritor.write("Reporte de estadísticas - " + fecha); escritor.newLine();
                escritor.write("Archivos: " + estadisticas.getTotalArchivos()); escritor.newLine();
                for (Estadisticas.Categoria categoria : Estadisticas.Categoria.values()) {
                    Estadisticas.DatoCategoria dato = estadisticas.getDato(categoria);
                    escritor.write(categoria + ": " + dato.getCantidad() + " archivos, "
                            + Estadisticas.formatoTamano(dato.getTamanoTotal()) + ", "
                            + String.format(java.util.Locale.ROOT, "%.2f%%", dato.getPorcentaje(estadisticas.getTotalArchivos())));
                    escritor.newLine();
                }
                escritor.write("Carpetas: " + estadisticas.getTotalCarpetas()); escritor.newLine();
                escritor.write("Carpetas vacías: " + estadisticas.getCarpetasVacias()); escritor.newLine();
                escritor.write("Profundidad máxima: " + estadisticas.getProfundidadMaxima()); escritor.newLine();
                escritor.write("Archivo más grande: " + ruta(estadisticas.getArchivoMasGrande())); escritor.newLine();
                escritor.write("Archivo más reciente: " + ruta(estadisticas.getArchivoMasReciente())); escritor.newLine();
            }
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo exportar el reporte: " + ex.getMessage());
        }
    }

    public File getArchivoBitacora() { return archivoBitacora; }
    public File getArchivoConfiguracion() { return archivoConfiguracion; }

    private String fechaComoTexto(Date fecha) {
        return fecha == null ? "" : Long.toString(fecha.getTime());
    }

    private String ruta(File archivo) {
        return archivo == null ? "Ninguno" : archivo.getAbsolutePath();
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.replace('\n', ' ').replace('\r', ' ');
    }
}
