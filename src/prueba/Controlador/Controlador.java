package prueba.controlador;

import prueba.Logica.Buscador;
import prueba.Logica.Estadisticas;
import prueba.Logica.Filtro;
import prueba.Logica.FiltroInvalidoException;
import prueba.Logica.GestorArchivos;
import prueba.Logica.NodoArchivo;
import prueba.Logica.Persistencia;
import prueba.Logica.Recorredor;
import prueba.Logica.RutaInvalidaException;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Abril
 */
public class Controlador {

    public interface Listener {
        default void alIniciarRecorrido() {}
        default void alActualizarProgreso(int elementos, File carpetaActual) {}
        default void alOmitirCarpeta(String mensaje) {}
        default void alTerminarRecorrido(NodoArchivo raiz, Estadisticas estadisticas,
                                         int elementos, int carpetasOmitidas) {}
        default void alCancelarRecorrido() {}
        default void alOcurrirError(String mensaje) {}
        default void alMostrarBusqueda(List<NodoArchivo> resultados) {}
        default void alMostrarDuplicados(Map<String, List<File>> duplicados) {}
        default void alActualizarBitacora(String contenido) {}
        default void alRestaurarConfiguracion(Map<String, String> configuracion) {}
    }

    private final Listener vista;
    private final RecorredorFactory recorredorFactory;
    private final Buscador buscador;
    private final GestorArchivos gestor;
    private final Persistencia persistencia;

    private NodoArchivo raizActual;
    private Estadisticas estadisticasActuales;
    private Filtro filtroActual;
    private File directorioActual;
    private RecorridoWorker trabajador;

    public Controlador(Listener vista) throws RutaInvalidaException {
        this(vista, new Persistencia());
    }

    public Controlador(Listener vista, Persistencia persistencia) {
        if (vista == null) throw new IllegalArgumentException("La vista no puede ser nula.");
        if (persistencia == null) throw new IllegalArgumentException("La persistencia no puede ser nula.");
        this.vista = vista;
        this.persistencia = persistencia;
        this.buscador = new Buscador();
        this.gestor = new GestorArchivos();
        this.recorredorFactory = new RecorredorFactory();
        this.filtroActual = new Filtro();
        this.estadisticasActuales = new Estadisticas();
    }


    public void analizar(String ruta, int profundidad) throws RutaInvalidaException {
        File directorio = Recorredor.validarRuta(ruta);
        if (profundidad < 0) throw new RutaInvalidaException("La profundidad no puede ser negativa.");
        if (trabajador != null && !trabajador.isDone()) {
            throw new RutaInvalidaException("Ya hay un recorrido en ejecución.");
        }

        directorioActual = directorio;
        registrar("ANALIZAR", directorio.getAbsolutePath());
        trabajador = new RecorridoWorker(directorio, profundidad);
        vista.alIniciarRecorrido();
        trabajador.execute();
    }

    public void cancelarRecorrido() {
        if (trabajador != null && !trabajador.isDone()) trabajador.cancel(true);
    }

    public void configurarFiltro(String texto, String extension, double minimoKB,
                                 double maximoKB, Date desde, Date hasta)
            throws FiltroInvalidoException, RutaInvalidaException {
        filtroActual = new Filtro(texto, extension, minimoKB, maximoKB, desde, hasta);
        guardarConfiguracionActual();
    }

    public List<NodoArchivo> buscar() throws RutaInvalidaException {
        if (raizActual == null) throw new RutaInvalidaException("Primero debe analizar un directorio.");
        List<NodoArchivo> resultados = buscador.buscar(raizActual, filtroActual);
        registrar("BUSCAR", directorioActual == null ? "" : directorioActual.getAbsolutePath());
        vista.alMostrarBusqueda(resultados);
        return resultados;
    }

    public Map<String, List<File>> detectarDuplicados() throws RutaInvalidaException {
        if (raizActual == null) throw new RutaInvalidaException("Primero debe analizar un directorio.");
        Map<String, List<File>> duplicados = buscador.detectarDuplicados(raizActual);
        registrar("DETECTAR DUPLICADOS", directorioActual == null ? "" : directorioActual.getAbsolutePath());
        vista.alMostrarDuplicados(duplicados);
        return duplicados;
    }

    public File crearCarpeta(String ruta) throws RutaInvalidaException {
        File creada = gestor.crearCarpeta(ruta);
        registrar("CREAR CARPETA", creada.getAbsolutePath());
        return creada;
    }

    public File crearArchivoTexto(String ruta, String contenido) throws RutaInvalidaException {
        File creado = gestor.crearArchivoTexto(ruta, contenido);
        registrar("CREAR ARCHIVO", creado.getAbsolutePath());
        return creado;
    }

    public File renombrar(File origen, String nombreNuevo) throws RutaInvalidaException {
        File resultado = gestor.renombrar(origen, nombreNuevo);
        registrar("RENOMBRAR", origen.getAbsolutePath() + " -> " + resultado.getAbsolutePath());
        return resultado;
    }

    public File copiar(File origen, File destino) throws RutaInvalidaException {
        File resultado = gestor.copiar(origen, destino);
        registrar("COPIAR", origen.getAbsolutePath() + " -> " + resultado.getAbsolutePath());
        return resultado;
    }

    public File mover(File origen, File destino) throws RutaInvalidaException {
        File resultado = gestor.mover(origen, destino);
        registrar("MOVER", origen.getAbsolutePath() + " -> " + resultado.getAbsolutePath());
        return resultado;
    }

    public void eliminar(File elemento) throws RutaInvalidaException {
        gestor.eliminar(elemento);
        registrar("ELIMINAR", elemento.getAbsolutePath());
    }

    public String leerTexto(File archivo) throws RutaInvalidaException {
        String contenido = gestor.leerTexto(archivo);
        registrar("ABRIR TEXTO", archivo.getAbsolutePath());
        return contenido;
    }

    public void guardarTexto(File archivo, String contenido) throws RutaInvalidaException {
        gestor.guardarTexto(archivo, contenido);
        registrar("GUARDAR TEXTO", archivo.getAbsolutePath());
    }

    public void exportarReporte(File destino) throws RutaInvalidaException {
        if (estadisticasActuales == null || raizActual == null) {
            throw new RutaInvalidaException("Primero debe analizar un directorio.");
        }
        persistencia.exportarReporte(destino, estadisticasActuales);
        registrar("EXPORTAR REPORTE", destino.getAbsolutePath());
    }

    public String leerBitacora() throws RutaInvalidaException {
        String contenido = persistencia.leerBitacora();
        vista.alActualizarBitacora(contenido);
        return contenido;
    }

    public Map<String, String> restaurarConfiguracion() throws RutaInvalidaException {
        Map<String, String> configuracion = persistencia.leerConfiguracion();
        vista.alRestaurarConfiguracion(configuracion);
        return configuracion;
    }

    public void guardarConfiguracionActual() throws RutaInvalidaException {
        String ruta = directorioActual == null ? "" : directorioActual.getAbsolutePath();
        persistencia.guardarConfiguracion(ruta, filtroActual);
    }

    private void registrar(String accion, String ruta) throws RutaInvalidaException {
        persistencia.registrarOperacion(accion, ruta);
    }

    public NodoArchivo getRaizActual() { return raizActual; }
    public Estadisticas getEstadisticasActuales() { return estadisticasActuales; }
    public Filtro getFiltroActual() { return filtroActual; }
    public File getDirectorioActual() { return directorioActual; }
    public boolean hayRecorridoActivo() {
        return trabajador != null && !trabajador.isDone();
    }

 
    private static class RecorredorFactory {
        Recorredor crear(int profundidad, java.util.function.Consumer<File> progreso,
                         java.util.function.Consumer<String> omitido,
                         java.util.function.BooleanSupplier cancelado) {
            return new Recorredor(profundidad, progreso, omitido, cancelado);
        }
    }

    private class RecorridoWorker extends SwingWorker<NodoArchivo, Avance> {
        private final File directorio;
        private final int profundidad;
        private int omitidas;

        RecorridoWorker(File directorio, int profundidad) {
            this.directorio = directorio;
            this.profundidad = profundidad;
        }

        @Override
        protected NodoArchivo doInBackground() {
            Recorredor recorredor = recorredorFactory.crear(
                profundidad,
                carpeta -> publish(new Avance(0, carpeta, null)),
                mensaje -> {
                    omitidas++;
                    publish(new Avance(0, null, mensaje));
                },
                () -> isCancelled() || Thread.currentThread().isInterrupted()
            );
            NodoArchivo arbol = recorredor.recorrer(directorio);
            publish(new Avance(recorredor.getElementosVisitados(), null, null));
            return arbol;
        }

        @Override
        protected void process(List<Avance> avances) {
            for (Avance avance : avances) {
                if (avance.mensajeOmitido != null) vista.alOmitirCarpeta(avance.mensajeOmitido);
                if (avance.carpetaActual != null) {
                    vista.alActualizarProgreso(avance.elementos, avance.carpetaActual);
                } else if (avance.elementos > 0) {
                    vista.alActualizarProgreso(avance.elementos, null);
                }
            }
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                vista.alCancelarRecorrido();
                return;
            }
            try {
                NodoArchivo arbol = get();
                Estadisticas estadisticas = new Estadisticas();
                estadisticas.calcular(arbol);
                raizActual = arbol;
                estadisticasActuales = estadisticas;
                guardarConfiguracionActual();
                vista.alTerminarRecorrido(arbol, estadisticas, contarElementos(arbol), omitidas);
            } catch (Exception ex) {
                Throwable causa = ex.getCause() == null ? ex : ex.getCause();
                vista.alOcurrirError(causa.getMessage() == null ? "Error durante el recorrido." : causa.getMessage());
            }
        }
    }

    private static class Avance {
        private final int elementos;
        private final File carpetaActual;
        private final String mensajeOmitido;

        Avance(int elementos, File carpetaActual, String mensajeOmitido) {
            this.elementos = elementos;
            this.carpetaActual = carpetaActual;
            this.mensajeOmitido = mensajeOmitido;
        }
    }

    private int contarElementos(NodoArchivo nodo) {
        if (nodo == null) return 0;
        int total = 1;
        for (NodoArchivo hijo : nodo.getHijos()) total += contarElementos(hijo);
        return total;
    }
}
