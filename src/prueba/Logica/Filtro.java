package prueba.Logica;

import java.util.Date;
import java.util.Locale;

public class Filtro {

    private String texto;
    private String extension;
    private double tamanoMinimoKB;
    private double tamanoMaximoKB;
    private Date fechaDesde;
    private Date fechaHasta;

    public Filtro() {
        texto = "";
        extension = "";
        tamanoMinimoKB = 0;
        tamanoMaximoKB = Double.POSITIVE_INFINITY;
    }

    public Filtro(String texto, String extension, double minimoKB, double maximoKB,
                  Date fechaDesde, Date fechaHasta) throws FiltroInvalidoException {
        this();
        setTexto(texto);
        setExtension(extension);
        setTamano(minimoKB, maximoKB);
        setFechas(fechaDesde, fechaHasta);
    }

    public void setTexto(String texto) throws FiltroInvalidoException {
        if (texto == null) throw new FiltroInvalidoException("El texto de búsqueda no puede ser nulo.");
        this.texto = texto.trim();
    }

    public void setExtension(String extension) throws FiltroInvalidoException {
        if (extension == null) throw new FiltroInvalidoException("La extensión no puede ser nula.");
        String valor = extension.trim().toLowerCase(Locale.ROOT);
        if (valor.startsWith(".")) valor = valor.substring(1);
        this.extension = valor;
    }

    public void setTamano(double minimoKB, double maximoKB) throws FiltroInvalidoException {
        if (minimoKB < 0 || maximoKB < 0 || Double.isNaN(minimoKB) || Double.isNaN(maximoKB)) {
            throw new FiltroInvalidoException("Los tamaños no pueden ser negativos.");
        }
        if (minimoKB > maximoKB) {
            throw new FiltroInvalidoException("El tamaño mínimo no puede ser mayor que el máximo.");
        }
        tamanoMinimoKB = minimoKB;
        tamanoMaximoKB = maximoKB;
    }

    public void setFechas(Date desde, Date hasta) throws FiltroInvalidoException {
        if (desde != null && hasta != null && desde.after(hasta)) {
            throw new FiltroInvalidoException("La fecha inicial no puede ser posterior a la fecha final.");
        }
        fechaDesde = desde == null ? null : new Date(desde.getTime());
        fechaHasta = hasta == null ? null : new Date(hasta.getTime());
    }

    public void setTamanoMinimoKB(double minimoKB) throws FiltroInvalidoException {
        setTamano(minimoKB, tamanoMaximoKB);
    }

    public void setTamanoMaximoKB(double maximoKB) throws FiltroInvalidoException {
        setTamano(tamanoMinimoKB, maximoKB);
    }

    public void setFechaDesde(Date fecha) throws FiltroInvalidoException {
        setFechas(fecha, fechaHasta);
    }

    public void setFechaHasta(Date fecha) throws FiltroInvalidoException {
        setFechas(fechaDesde, fecha);
    }

    public String getTexto(){
        return texto; 
    }
    public String getExtension(){
        return extension;
    }
    public double getTamanoMinimoKB(){
        return tamanoMinimoKB; 
    }
    public double getTamanoMaximoKB(){ 
        return tamanoMaximoKB;
    }
    public Date getFechaDesde(){
        return fechaDesde == null ? null : new Date(fechaDesde.getTime()); 
    }
    public Date getFechaHasta(){
        return fechaHasta == null ? null : new Date(fechaHasta.getTime());
    }

    public boolean cumple(java.io.File archivo) {
        if(archivo == null || !archivo.isFile())return false;
        String nombre = archivo.getName().toLowerCase(Locale.ROOT);
        if(!texto.isEmpty() && !nombre.contains(texto.toLowerCase(Locale.ROOT))){
            return false;
        }
        if (!extension.isEmpty() && !NodoArchivo.extensionDe(nombre).equals(extension)){
            return false;
        }
        double kb = archivo.length() / 1024.0;
        if(kb < tamanoMinimoKB){
            return false;
        }
        if (kb > tamanoMaximoKB){
            return false;
        }
        Date modificacion = new Date(archivo.lastModified());
        if (fechaDesde != null && modificacion.before(fechaDesde)){
            return false;
        }
        return fechaHasta == null || !modificacion.after(fechaHasta);
    }
}
