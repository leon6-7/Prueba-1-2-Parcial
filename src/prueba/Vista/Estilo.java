package prueba.Vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Colores y fuentes que comparten las ventanas. */
public class Estilo {

    public static final Color FONDO = new Color(218, 232, 238);
    public static final Color AZUL = new Color(20, 42, 60);
    public static final Color BLANCO = Color.WHITE;
    public static final Color ACENTO = new Color(46, 105, 137);

    public static final Font TITULO = new Font("Impact", Font.PLAIN, 25);
    public static final Font SUBTITULO = new Font("Impact", Font.PLAIN, 17);
    public static final Font TEXTO = new Font("Arial", Font.PLAIN, 13);

    public static JPanel panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(FONDO);
        return panel;
    }

    public static GridBagConstraints gbc(int columna, int fila) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = columna;
        g.gridy = fila;
        g.insets = new Insets(5, 7, 5, 7);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        return g;
    }

    public static JLabel label(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TEXTO);
        etiqueta.setForeground(AZUL);
        return etiqueta;
    }

    public static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(TEXTO);
        boton.setBackground(AZUL);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false);
        return boton;
    }

    public static void campo(JTextField campo) {
        campo.setFont(TEXTO);
        campo.setForeground(AZUL);
    }

    public static void tituloBorde(JComponent componente, String titulo) {
        componente.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACENTO), titulo,
                0, 0, SUBTITULO, AZUL));
    }
}
