package prueba.Vista;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import prueba.Logica.*;
/** Estadísticas por extensión y resumen global. */
public class PanelEstadisticas extends JPanel {
    public final DefaultTableModel modelo=new DefaultTableModel(new String[]{"Categoría","Archivos","Tamaño","Porcentaje"},0) {
        public boolean isCellEditable(int r,int c) {
            return false;
        }
    }
    ;
    public final JTable tabla=new JTable(modelo);
    public final JTextArea resumen=new JTextArea(6,55);
    public final JButton exportar=Estilo.boton("Exportar reporte");
    public PanelEstadisticas() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this,"Estadísticas del directorio");
        tabla.setFont(Estilo.TEXTO);
        tabla.setRowHeight(25);
        resumen.setEditable(false);
        resumen.setFont(Estilo.TEXTO);
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(tabla),g);
        g=Estilo.gbc(0,1);
        g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL;
        add(new JScrollPane(resumen),g);
        add(exportar,Estilo.gbc(0,2));
    }
    public void mostrar(Estadisticas e) {
        modelo.setRowCount(0);
        for(Estadisticas.Categoria c:Estadisticas.Categoria.values()) {
            Estadisticas.DatoCategoria d=e.getDato(c);
            modelo.addRow(new Object[]{c,d.getCantidad(),Estadisticas.formatoTamano(d.getTamanoTotal()),String.format("%.1f %%",d.getPorcentaje(e.getTotalArchivos()))});
        }
        resumen.setText("Archivos: "+e.getTotalArchivos()+"     Carpetas: "+e.getTotalCarpetas()+"     Vacías: "+e.getCarpetasVacias()+"\nProfundidad máxima: "+e.getProfundidadMaxima()+"\nArchivo más grande: "+(e.getArchivoMasGrande()==null?"Ninguno":e.getArchivoMasGrande().getAbsolutePath())+"\nArchivo más reciente: "+(e.getArchivoMasReciente()==null?"Ninguno":e.getArchivoMasReciente().getAbsolutePath()));
    }
}
