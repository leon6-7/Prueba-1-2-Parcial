package prueba.Logica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Abril
 */
public class GestorArchivos {

    private static final long TAMANO_MAXIMO_TEXTO = 5L * 1024L * 1024L;

    public File crearCarpeta(String ruta) throws RutaInvalidaException {
        File carpeta = validarNombreDestino(ruta);
        if (carpeta.exists()) throw new RutaInvalidaException("Ya existe un elemento con esa ruta.");
        if (!carpeta.mkdirs()) throw new RutaInvalidaException("No se pudo crear la carpeta.");
        return carpeta;
    }

    public File crearArchivoTexto(String ruta, String contenido) throws RutaInvalidaException {
        File archivo = validarNombreDestino(ruta);
        if (archivo.exists()) throw new RutaInvalidaException("Ya existe un elemento con esa ruta.");
        File padre = archivo.getParentFile();
        if (padre != null && !padre.exists() && !padre.mkdirs()) {
            throw new RutaInvalidaException("No se pudo crear la carpeta de destino.");
        }
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write(contenido == null ? "" : contenido);
            return archivo;
        } catch (IOException ex) {
            archivo.delete();
            throw new RutaInvalidaException("No se pudo crear el archivo: " + ex.getMessage());
        }
    }

    public File renombrar(File origen, String nuevoNombre) throws RutaInvalidaException {
        validarOrigen(origen);
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty() || contieneCaracterInvalido(nuevoNombre.trim())) {
            throw new RutaInvalidaException("El nombre nuevo no es válido.");
        }
        File destino = new File(origen.getParentFile(), nuevoNombre.trim());
        if (destino.exists()) throw new RutaInvalidaException("Ya existe un elemento con ese nombre.");
        if (!origen.renameTo(destino)) throw new RutaInvalidaException("No se pudo renombrar el elemento.");
        return destino;
    }

    public File copiar(File origen, File destino) throws RutaInvalidaException {
        validarOrigen(origen);
        if (destino == null) throw new RutaInvalidaException("La ruta de destino no es válida.");
        if (destino.exists()) throw new RutaInvalidaException("El destino ya existe; confirme y elimínelo desde la vista antes de copiar.");
        if (esDescendiente(origen, destino)) throw new RutaInvalidaException("No se puede copiar una carpeta dentro de sí misma.");
        try {
            copiarRecursivo(origen, destino);
            return destino;
        } catch (IOException ex) {
            eliminarRecursivo(destino);
            throw new RutaInvalidaException("No se pudo copiar el elemento: " + ex.getMessage());
        }
    }

    private void copiarRecursivo(File origen, File destino) throws IOException {
        if (origen.isDirectory()) {
            if (!destino.mkdirs() && !destino.isDirectory()) throw new IOException("No se pudo crear " + destino);
            File[] hijos = origen.listFiles();
            if (hijos == null) throw new IOException("No se puede leer " + origen);
            for (File hijo : hijos) copiarRecursivo(hijo, new File(destino, hijo.getName()));
        } else {
            try (FileInputStream entrada = new FileInputStream(origen);
                 FileOutputStream salida = new FileOutputStream(destino)) {
                byte[] buffer = new byte[8192];
                int cantidad;
                while ((cantidad = entrada.read(buffer)) != -1) salida.write(buffer, 0, cantidad);
            }
        }
    }

    public File mover(File origen, File destino) throws RutaInvalidaException {
        validarOrigen(origen);
        if (destino == null || destino.exists()) throw new RutaInvalidaException("El destino no es válido o ya existe.");
        if (esDescendiente(origen, destino)) throw new RutaInvalidaException("No se puede mover una carpeta dentro de sí misma.");
        File padre = destino.getParentFile();
        if (padre != null && !padre.exists() && !padre.mkdirs()) throw new RutaInvalidaException("No se pudo crear la carpeta de destino.");
        if (origen.renameTo(destino)) return destino;
        File copia = copiar(origen, destino);
        try {
            eliminarRecursivo(origen);
            return copia;
        } catch (RutaInvalidaException ex) {
            eliminarRecursivo(copia);
            throw new RutaInvalidaException("Se copió el elemento, pero no se pudo eliminar el original.");
        }
    }

    public void eliminar(File elemento) throws RutaInvalidaException {
        validarOrigen(elemento);
        eliminarRecursivo(elemento);
    }

    private void eliminarRecursivo(File elemento) throws RutaInvalidaException {
        if (elemento.isDirectory()) {
            File[] hijos = elemento.listFiles();
            if (hijos == null) throw new RutaInvalidaException("No se puede leer la carpeta: " + elemento);
            for (File hijo : hijos) eliminarRecursivo(hijo);
        }
        if (!elemento.delete()) throw new RutaInvalidaException("No se pudo eliminar: " + elemento);
    }

    public String leerTexto(File archivo) throws RutaInvalidaException {
        validarOrigen(archivo);
        if (!archivo.isFile()) throw new RutaInvalidaException("La ruta seleccionada no es un archivo.");
        if (archivo.length() > TAMANO_MAXIMO_TEXTO) throw new RutaInvalidaException("El archivo supera el límite de 5 MB.");
        if (!pareceTexto(archivo)) throw new RutaInvalidaException("El archivo no parece ser de texto.");
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) contenido.append(linea).append(System.lineSeparator());
            return contenido.toString();
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    public void guardarTexto(File archivo, String contenido) throws RutaInvalidaException {
        if (archivo == null) throw new RutaInvalidaException("Debe indicar un archivo.");
        File padre = archivo.getParentFile();
        if (padre != null && !padre.isDirectory()) throw new RutaInvalidaException("La carpeta de destino no existe.");
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            escritor.write(contenido == null ? "" : contenido);
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo guardar el archivo: " + ex.getMessage());
        }
    }

    private boolean pareceTexto(File archivo) throws RutaInvalidaException {
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            int dato;
            int revisados = 0;
            while ((dato = entrada.read()) != -1 && revisados < 4096) {
                if (dato == 0) return false;
                revisados++;
            }
            return true;
        } catch (IOException ex) {
            throw new RutaInvalidaException("No se pudo comprobar el archivo: " + ex.getMessage());
        }
    }

    private File validarNombreDestino(String ruta) throws RutaInvalidaException {
        if (ruta == null || ruta.trim().isEmpty()) throw new RutaInvalidaException("La ruta no puede estar vacía.");
        File archivo = new File(ruta.trim());
        if (archivo.getName().isEmpty() || archivo.getName().equals(".") || archivo.getName().equals("..")
                || contieneCaracterInvalido(archivo.getName())) {
            throw new RutaInvalidaException("El nombre del elemento no es válido.");
        }
        return archivo;
    }

    private void validarOrigen(File origen) throws RutaInvalidaException {
        if (origen == null || !origen.exists()) throw new RutaInvalidaException("El elemento no existe.");
    }

    private boolean contieneCaracterInvalido(String nombre) {
        return nombre.matches(".*[<>:\"|?*/\\\\].*") || nombre.endsWith(".") || nombre.endsWith(" ");
    }

    private boolean esDescendiente(File origen, File destino) {
        try {
            String rutaOrigen = origen.getCanonicalPath() + File.separator;
            String rutaDestino = destino.getCanonicalPath() + File.separator;
            return rutaDestino.startsWith(rutaOrigen);
        } catch (IOException ex) {
            return true;
        }
    }
}
