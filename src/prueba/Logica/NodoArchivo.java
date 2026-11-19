/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba.Logica;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
/**
 *
 * @author Abril
 */
public class NodoArchivo {

    private final File archivo;
    private final boolean carpeta;
    private final int profundidad;
    private final long tamano;
    private final long fechaModificacion;
    private final List<NodoArchivo> hijos = new ArrayList<>();
    private boolean explorada = true;

    NodoArchivo(File archivo, int profundidad) {
        this.archivo = archivo;
        this.profundidad = profundidad;
        this.carpeta = archivo.isDirectory();
        this.tamano = carpeta ? 0 : archivo.length();
        this.fechaModificacion = archivo.lastModified();
    }

    void agregarHijo(NodoArchivo hijo) {
        hijos.add(hijo);
    }

    void marcarNoExplorada() {
        explorada = false;
    }

    public File getArchivo() {
        return archivo;
    }

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

    public long getTamano() {
        return tamano;
    }
    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public boolean esOculto() {
        return archivo.isHidden();
    }

    public List<NodoArchivo> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    public boolean estaVacia() {
        return carpeta && explorada && hijos.isEmpty();
    }

    public String getExtension() {
        return carpeta ? "" : extensionDe(getNombre());
    }


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
