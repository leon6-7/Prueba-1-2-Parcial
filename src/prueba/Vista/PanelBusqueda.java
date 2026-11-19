package prueba.Vista;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import prueba.Logica.*;
/** Tabla ordenable de resultados. */
public class PanelBusqueda extends JPanel {
    public final DefaultTableModel modelo=new DefaultTableModel(new String[]{"Nombre","Ruta absoluta","Tamaño","Modificado"},0) {
        public boolean isCellEditable(int r,int c) {
            return false;
        }
    }
    ;
    public final JTable tabla=new JTable(modelo);
    public final JLabel estado=Estilo.label("Sin búsquedas");
    public PanelBusqueda() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this,"Resultados de búsqueda");
        tabla.setAutoCreateRowSorter(true);
        tabla.setFont(Estilo.TEXTO);
        tabla.setRowHeight(25);
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(tabla),g);
        add(estado,Estilo.gbc(0,1));
    }
    public void mostrar(java.util.List<NodoArchivo> resultados) {
        modelo.setRowCount(0);
        for(NodoArchivo n:resultados)modelo.addRow(new Object[]{n.getNombre(),n.getArchivo().getAbsolutePath(),Estadisticas.formatoTamano(n.getTamano()),new Date(n.getFechaModificacion())});
        estado.setText(resultados.isEmpty()?"No se encontraron archivos que coincidan con los criterios.":resultados.size()+" coincidencia(s)");
    }
}
