package prueba.Vista;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import prueba.Logica.*;

public class PanelArbol extends JPanel {
    public final JTree arbol=new JTree(new DefaultMutableTreeNode("Sin análisis"));
    public final JButton crearCarpeta=Estilo.boton("Nueva carpeta"),crearArchivo=Estilo.boton("Nuevo texto"),renombrar=Estilo.boton("Renombrar"),copiar=Estilo.boton("Copiar"),mover=Estilo.boton("Mover"),eliminar=Estilo.boton("Eliminar"),abrir=Estilo.boton("Abrir texto");
    public PanelArbol() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this,"Estructura de archivos");
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(arbol),g);
        JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setOpaque(false);
        for(JButton b:new JButton[]{crearCarpeta,crearArchivo,renombrar,copiar,mover,eliminar,abrir})botones.add(b);
        g=Estilo.gbc(0,1);
        g.fill=GridBagConstraints.HORIZONTAL;
        add(botones,g);
    }
    public void mostrar(NodoArchivo raiz) {
        arbol.setModel(new DefaultTreeModel(convertir(raiz)));
    }
    private DefaultMutableTreeNode convertir(NodoArchivo n) {
        DefaultMutableTreeNode t=new DefaultMutableTreeNode(n);
        for(NodoArchivo h:n.getHijos())t.add(convertir(h));
        return t;
    }
    public java.io.File seleccionado() {
        Object x=arbol.getLastSelectedPathComponent();
        if(!(x instanceof DefaultMutableTreeNode))return null;
        Object u=((DefaultMutableTreeNode)x).getUserObject();
        return u instanceof NodoArchivo?((NodoArchivo)u).getArchivo():null;
    }
}
