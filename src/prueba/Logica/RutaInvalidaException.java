package explorador.modelo;

/**
 * Se lanza cuando una ruta, un nombre o una operación sobre archivos no es válida:
 * ruta vacía, inexistente o que no es un directorio, nombre con caracteres prohibidos,
 * elemento que ya existe, archivo que no se puede editar, etc.
 * El mensaje explica el motivo y está pensado para mostrarse tal cual al usuario.
 */
public class RutaInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    public RutaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
