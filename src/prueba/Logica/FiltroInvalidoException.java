package explorador.modelo;

/**
 * Se lanza cuando los criterios de búsqueda no son coherentes: números negativos o mal
 * escritos, tamaño mínimo mayor que el máximo, fechas invertidas o con formato incorrecto.
 * El mensaje explica el motivo y está pensado para mostrarse tal cual al usuario.
 */
public class FiltroInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public FiltroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
