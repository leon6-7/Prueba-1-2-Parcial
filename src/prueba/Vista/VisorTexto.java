package prueba.Vista;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import prueba.controlador.Controlador;
/** Editor de texto y visor de bitácora. */
public class VisorTexto extends JPanel {
    public final JTextArea editor=new JTextArea();
    public final JButton guardar=Estilo.boton("Guardar cambios"),actualizar=Estilo.boton("Ver bitácora");
    private File archivo;
    private Controlador controlador;
    public VisorTexto() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this,"Editor y bitácora");
        editor.setFont(new Font("Monospaced",Font.PLAIN,14));
        GridBagConstraints g=Estilo.gbc(0,0);
        g.weightx=1;
        g.weighty=1;
        g.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(editor),g);
        JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setOpaque(false);
        botones.add(guardar);
        botones.add(actualizar);
        add(botones,Estilo.gbc(0,1));
        guardar.addActionListener(e->{if(archivo==null)return;try{int r=JOptionPane.showConfirmDialog(this,"¿Guardar cambios en "+archivo.getName()+"?","Confirmar",JOptionPane.YES_NO_OPTION);if(r==JOptionPane.YES_OPTION)controlador.guardarTexto(archivo,editor.getText());}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}});
        actualizar.addActionListener(e->{try{mostrarBitacora(controlador.leerBitacora());}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}});
    }
    public void conectar(Controlador c) {
        controlador=c;
    }
    public void abrir(File f)throws Exception {
        try {
            String contenido = controlador.leerTexto(f);
            archivo=f;
            editor.setEditable(true);
            editor.setText(contenido);
            guardar.setEnabled(true);
        }
        catch (prueba.Logica.RutaInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Advertencia del archivo", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void mostrarBitacora(String s) {
        archivo=null;
        editor.setText(s);
        editor.setEditable(false);
        guardar.setEnabled(false);
    }
}
