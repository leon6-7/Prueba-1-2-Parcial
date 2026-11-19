package prueba.Vista;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** Campos para elegir la carpeta y aplicar filtros. */
public class PanelEntrada extends JPanel {

    public final JTextField ruta = new JTextField(34);
    public final JTextField texto = new JTextField(15);
    public final JTextField extension = new JTextField(8);
    public final JTextField minimo = new JTextField("0", 7);
    public final JTextField maximo = new JTextField("0", 7);
    public final JTextField desde = new JTextField(10);
    public final JTextField hasta = new JTextField(10);

    public final JSpinner profundidad = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    public final JButton seleccionar = Estilo.boton("Examinar");
    public final JButton analizar = Estilo.boton("Analizar");
    public final JButton buscar = Estilo.boton("Buscar");
    public final JButton cancelar = Estilo.boton("Cancelar");
    public final JProgressBar progreso = new JProgressBar();
    public final JLabel estado = Estilo.label("Listo para explorar");

    public PanelEntrada() {
        super(new GridBagLayout());
        setBackground(Estilo.FONDO);
        Estilo.tituloBorde(this, "Directorio y filtros");

        JTextField[] campos = {ruta, texto, extension, minimo, maximo, desde, hasta};
        for (JTextField campo : campos) {
            Estilo.campo(campo);
        }

        agregar(Estilo.label("Directorio raíz"), 0, 0);
        GridBagConstraints g = Estilo.gbc(1, 0);
        g.gridwidth = 2;
        g.weightx = 1;
        add(ruta, g);
        agregar(seleccionar, 3, 0);

        agregar(Estilo.label("Nombre"), 0, 1);
        agregar(texto, 1, 1);
        agregar(Estilo.label("Extensión"), 2, 1);
        agregar(extension, 3, 1);

        agregar(Estilo.label("Mínimo KB"), 0, 2);
        agregar(minimo, 1, 2);
        agregar(Estilo.label("Máximo KB (0 = sin límite)"), 2, 2);
        agregar(maximo, 3, 2);

        agregar(Estilo.label("Desde (aaaa-mm-dd)"), 0, 3);
        agregar(desde, 1, 3);
        agregar(Estilo.label("Hasta (aaaa-mm-dd)"), 2, 3);
        agregar(hasta, 3, 3);

        agregar(Estilo.label("Profundidad (0 = ilimitada)"), 0, 4);
        agregar(profundidad, 1, 4);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.setOpaque(false);
        botones.add(analizar);
        botones.add(buscar);
        botones.add(cancelar);

        g = Estilo.gbc(0, 5);
        g.gridwidth = 4;
        add(botones, g);

        g = Estilo.gbc(0, 6);
        g.gridwidth = 4;
        g.weightx = 1;
        add(progreso, g);

        g = Estilo.gbc(0, 7);
        g.gridwidth = 4;
        add(estado, g);
        cancelar.setEnabled(false);
    }

    private void agregar(java.awt.Component componente, int columna, int fila) {
        add(componente, Estilo.gbc(columna, fila));
    }
}
