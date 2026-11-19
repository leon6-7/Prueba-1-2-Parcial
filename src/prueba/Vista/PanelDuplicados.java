package prueba.Vista;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;
/** Grupos de archivos con el mismo nombre y tamaño. */
public class PanelDuplicados extends JPanel {
    public final DefaultTableModel modelo=new DefaultTableModel(new String[]{"Grupo","Archivo","Ruta","Bytes"},0) {
        public boolean isCellEditable(int r,int c) {
            return false;
        }
    }
    ;
    public final JTable tabla=new JTable(modelo);
    public final JButton detectar=Estilo.boton("Detectar duplicados");
    public PanelDuplicados() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this,"Archivos duplicados");
        tabla.setAutoCreateRowSorter(true);
        tabla.setFont(Estilo.TEXTO);
        tabla.setRowHeight(25);
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(tabla),g);
        add(detectar,Estilo.gbc(0,1));
    }
    public void mostrar(Map<String,java.util.List<File>> grupos) {
        modelo.setRowCount(0);
        int i=1;
        for(java.util.List<File> archivos:grupos.values()) {
            for(File f:archivos)modelo.addRow(new Object[]{i,f.getName(),f.getAbsolutePath(),f.length()});
            i++;
        }
    }
}
