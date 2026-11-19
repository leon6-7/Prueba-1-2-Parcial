/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba.Logica;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;
/**
 *
 * @author Abril
 */

public class Recorredor {

    private final int profundidadMaxima;
    private final Consumer<File> alEntrarCarpeta;
    private final Consumer<String> alOmitir;
    private final BooleanSupplier cancelado;
    private final Set<String> visitadas = new HashSet<>();

    private volatile int elementosVisitados;
    private volatile int carpetasOmitidas;

    public Recorredor(int profundidadMaxima, Consumer<File> alEntrarCarpeta,
                      Consumer<String> alOmitir, BooleanSupplier cancelado) {
        this.profundidadMaxima = profundidadMaxima;
        this.alEntrarCarpeta = alEntrarCarpeta;
        this.alOmitir = alOmitir;
        this.cancelado = cancelado;
    }


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

    public NodoArchivo recorrer(File raiz) {
        visitadas.clear();
        elementosVisitados = 0;
        carpetasOmitidas = 0;
        return construir(raiz, 0);
    }

    public int getElementosVisitados() {
        return elementosVisitados;
    }

    public int getCarpetasOmitidas() {
        return carpetasOmitidas;
    }

    //METODO RECURSIVO ESTE
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

    private boolean yaVisitada(File carpeta) {
        try {
            return !visitadas.add(carpeta.getCanonicalPath());
        } catch (IOException ex) {
            return true;   
        }
    }

    private void omitir(NodoArchivo nodo, File carpeta, String motivo) {
        nodo.marcarNoExplorada();
        carpetasOmitidas++;
        alOmitir.accept(carpeta.getAbsolutePath() + " (" + motivo + ")");
    }
}
