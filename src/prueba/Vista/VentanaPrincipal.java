package prueba.Vista;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import prueba.Logica.*;
import prueba.controlador.Controlador;
/** Ventana principal con pestañas y distribución GridBagLayout. */
public class VentanaPrincipal extends JFrame implements Controlador.Listener {
    public static Font titulo=Estilo.TITULO,subTitulo=Estilo.SUBTITULO,texto=Estilo.TEXTO;
    public static Color titColor=Estilo.AZUL;
    private final PanelEntrada entrada=new PanelEntrada();
    private final PanelArbol arbol=new PanelArbol();
    private final PanelEstadisticas estadisticas=new PanelEstadisticas();
    private final PanelBusqueda busqueda=new PanelBusqueda();
    private final PanelDuplicados duplicados=new PanelDuplicados();
    private final VisorTexto visor=new VisorTexto();
    private final JTabbedPane pestanas=new JTabbedPane();
    private Controlador controlador;
    public VentanaPrincipal() {
        super("Buscador de Archivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050,720);
        setMinimumSize(new Dimension(850,620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Estilo.FONDO);
        setLayout(new GridBagLayout());
        JLabel encabezado=new JLabel("BUSCADOR DE ARCHIVOS",SwingConstants.CENTER);
        encabezado.setFont(titulo);
        encabezado.setForeground(titColor);
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        add(encabezado,g);
        g=Estilo.gbc(0,1);
        g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL;
        add(entrada,g);
        pestanas.setFont(subTitulo);
        pestanas.addTab("Explorador",arbol);
        pestanas.addTab("Estadísticas",estadisticas);
        pestanas.addTab("Búsqueda",busqueda);
        pestanas.addTab("Duplicados",duplicados);
        pestanas.addTab("Texto / Bitácora",visor);
        g=Estilo.gbc(0,2);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(pestanas,g);
        try {
            controlador=new Controlador(this);
            visor.conectar(controlador);
            conectarAcciones();
            controlador.restaurarConfiguracion();
        }
        catch(Exception ex) {
            error(ex);
        }
        setVisible(true);
    }
    private void conectarAcciones() {
        entrada.seleccionar.addActionListener(e->{JFileChooser c=new JFileChooser();c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);if(c.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)entrada.ruta.setText(c.getSelectedFile().getAbsolutePath());});
        entrada.analizar.addActionListener(e->ejecutar(()->controlador.analizar(entrada.ruta.getText(),(Integer)entrada.profundidad.getValue())));
        entrada.cancelar.addActionListener(e->controlador.cancelarRecorrido());
        entrada.buscar.addActionListener(e->ejecutar(()->{aplicarFiltro();controlador.buscar();pestanas.setSelectedComponent(busqueda);}));
        duplicados.detectar.addActionListener(e->ejecutar(()->{controlador.detectarDuplicados();pestanas.setSelectedComponent(duplicados);}));
        estadisticas.exportar.addActionListener(e->ejecutar(()->{JFileChooser c=new JFileChooser();if(c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)controlador.exportarReporte(c.getSelectedFile());}));
        arbol.crearCarpeta.addActionListener(e->ejecutar(()->{String s=JOptionPane.showInputDialog(this,"Ruta completa de la nueva carpeta:");if(s!=null){controlador.crearCarpeta(s);refrescar();}}));
        arbol.crearArchivo.addActionListener(e->ejecutar(()->{String s=JOptionPane.showInputDialog(this,"Ruta completa del nuevo archivo .txt:");if(s!=null){String contenido=JOptionPane.showInputDialog(this,"Contenido inicial:");if(contenido!=null){controlador.crearArchivoTexto(s,contenido);refrescar();}}}));
        arbol.renombrar.addActionListener(e->ejecutar(()->{File f=requerirSeleccion();String s=JOptionPane.showInputDialog(this,"Nuevo nombre:",f.getName());if(s!=null){controlador.renombrar(f,s);refrescar();}}));
        arbol.copiar.addActionListener(e->ejecutar(()->{File f=requerirSeleccion();JFileChooser c=new JFileChooser();c.setSelectedFile(new File(f.getParentFile(),f.getName()+"_copia"));if(c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){File d=c.getSelectedFile();if(d.exists())throw new IllegalArgumentException("El destino ya existe. Seleccione otro nombre.");controlador.copiar(f,d);refrescar();}}));
        arbol.mover.addActionListener(e->ejecutar(()->{File f=requerirSeleccion();JFileChooser c=new JFileChooser();c.setSelectedFile(f);if(c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){File d=c.getSelectedFile();if(d.exists())throw new IllegalArgumentException("El destino ya existe. Seleccione otro nombre.");controlador.mover(f,d);refrescar();}}));
        arbol.eliminar.addActionListener(e->ejecutar(()->{File f=requerirSeleccion();if(confirmar("¿Eliminar definitivamente "+f.getName()+"?")){controlador.eliminar(f);refrescar();}}));
        arbol.abrir.addActionListener(e->ejecutar(()->{visor.abrir(requerirSeleccion());pestanas.setSelectedComponent(visor);}));
    }
    private File requerirSeleccion() {
        File f=arbol.seleccionado();
        if(f==null)throw new IllegalArgumentException("Seleccione un archivo o carpeta del árbol.");
        return f;
    }
    private boolean confirmar(String s) {
        return JOptionPane.showConfirmDialog(this,s,"Confirmar operación",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION;
    }
    private void aplicarFiltro()throws Exception {
        SimpleDateFormat formato=new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        Date inicio=entrada.desde.getText().trim().isEmpty()?null:formato.parse(entrada.desde.getText().trim());
        Date fin=entrada.hasta.getText().trim().isEmpty()?null:formato.parse(entrada.hasta.getText().trim());
        controlador.configurarFiltro(entrada.texto.getText(),entrada.extension.getText(),Double.parseDouble(entrada.minimo.getText()),(entrada.maximo.getText().trim().equals("0") ? Double.POSITIVE_INFINITY : Double.parseDouble(entrada.maximo.getText())),inicio,fin);
    }
    private void refrescar()throws Exception {
        if(controlador.getDirectorioActual()!=null)controlador.analizar(controlador.getDirectorioActual().getAbsolutePath(),(Integer)entrada.profundidad.getValue());
    }
    private interface Accion {
        void ejecutar()throws Exception;
    }
    private void ejecutar(Accion a) {
        try {
            a.ejecutar();
        }
        catch(Exception ex) {
            error(ex);
        }
    }
    private void error(Exception e) {
        JOptionPane.showMessageDialog(this,e.getMessage()==null?e.toString():e.getMessage(),"Aviso",JOptionPane.ERROR_MESSAGE);
    }
    @Override public void alIniciarRecorrido() {
        entrada.analizar.setEnabled(false);
        entrada.cancelar.setEnabled(true);
        entrada.progreso.setIndeterminate(true);
        entrada.estado.setText("Analizando archivos...");
    }
    @Override public void alActualizarProgreso(int n,File f) {
        entrada.estado.setText("Elementos: "+n+(f==null?"":" | "+f.getAbsolutePath()));
    }
    @Override public void alOmitirCarpeta(String m) {
        entrada.estado.setText("Carpeta omitida: "+m);
    }
    @Override public void alTerminarRecorrido(NodoArchivo n,Estadisticas e,int cantidad,int omitidas) {
        entrada.progreso.setIndeterminate(false);
        entrada.progreso.setValue(100);
        entrada.analizar.setEnabled(true);
        entrada.cancelar.setEnabled(false);
        entrada.estado.setText("Análisis completo: "+cantidad+" elementos, "+omitidas+" carpetas omitidas");
        arbol.mostrar(n);
        estadisticas.mostrar(e);
    }
    @Override public void alCancelarRecorrido() {
        entrada.progreso.setIndeterminate(false);
        entrada.analizar.setEnabled(true);
        entrada.cancelar.setEnabled(false);
        entrada.estado.setText("Recorrido cancelado");
    }
    @Override public void alOcurrirError(String m) {
        entrada.progreso.setIndeterminate(false);
        entrada.analizar.setEnabled(true);
        entrada.cancelar.setEnabled(false);
        error(new Exception(m));
    }
    @Override public void alMostrarBusqueda(java.util.List<NodoArchivo> r) {
        busqueda.mostrar(r);
    }
    @Override public void alMostrarDuplicados(Map<String,java.util.List<File>> m) {
        duplicados.mostrar(m);
    }
    @Override public void alActualizarBitacora(String s) {
        visor.mostrarBitacora(s);
    }
    @Override public void alRestaurarConfiguracion(Map<String,String> m) {
        if(m.containsKey("ruta"))entrada.ruta.setText(m.get("ruta"));
        if(m.containsKey("texto"))entrada.texto.setText(m.get("texto"));
        if(m.containsKey("extension"))entrada.extension.setText(m.get("extension"));
        if(m.containsKey("tamanoMinimoKB"))entrada.minimo.setText(m.get("tamanoMinimoKB"));
        if(m.containsKey("tamanoMaximoKB")) {
            String max=m.get("tamanoMaximoKB");
            entrada.maximo.setText("Infinity".equals(max)?"0":max);
        }
        if(m.containsKey("fechaDesde"))entrada.desde.setText(m.get("fechaDesde"));
        if(m.containsKey("fechaHasta"))entrada.hasta.setText(m.get("fechaHasta"));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
