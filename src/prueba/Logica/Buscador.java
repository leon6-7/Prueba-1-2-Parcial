package prueba.Logica;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 *
 * @author Abril
 */
public class Buscador {

    public List<NodoArchivo> buscar(NodoArchivo raiz, Filtro filtro) {
        List<NodoArchivo> resultados = new ArrayList<>();
        if (raiz != null && filtro != null) buscarRecursivo(raiz, filtro, resultados);
        return resultados;
    }

    private void buscarRecursivo(NodoArchivo nodo, Filtro filtro, List<NodoArchivo> resultados) {
        if (nodo.esCarpeta()) {
            for (NodoArchivo hijo : nodo.getHijos()) buscarRecursivo(hijo, filtro, resultados);
        } else if (filtro.cumple(nodo.getArchivo())) {
            resultados.add(nodo);
        }
    }


    public Map<String, List<File>> detectarDuplicados(NodoArchivo raiz) {
        Map<String, List<File>> candidatos = new HashMap<>();
        Map<String, List<File>> duplicados = new HashMap<>();
        if (raiz != null) recopilarArchivos(raiz, candidatos);
        for (Map.Entry<String, List<File>> entrada : candidatos.entrySet()) {
            List<File> archivos = entrada.getValue();
            if (archivos.size() > 1 && estanEnRutasDiferentes(archivos)) {
                duplicados.put(entrada.getKey(), archivos);
            }
        }
        return duplicados;
    }

    private void recopilarArchivos(NodoArchivo nodo, Map<String, List<File>> grupos) {
        if (nodo.esCarpeta()) {
            for (NodoArchivo hijo : nodo.getHijos()) recopilarArchivos(hijo, grupos);
        } else {
            File archivo = nodo.getArchivo();
            String clave = archivo.getName().toLowerCase(Locale.ROOT) + "|" + nodo.getTamano();
            grupos.computeIfAbsent(clave, k -> new ArrayList<>()).add(archivo);
        }
    }

    private boolean estanEnRutasDiferentes(List<File> archivos) {
        String primeraRuta = archivos.get(0).getAbsolutePath();
        for (int i = 1; i < archivos.size(); i++) {
            if (!primeraRuta.equals(archivos.get(i).getAbsolutePath())) return true;
        }
        return false;
    }
}
