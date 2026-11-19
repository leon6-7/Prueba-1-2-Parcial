/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba.Vista;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Abril
 */
public class VentanaPrincipal extends JFrame{
    public static Font titulo = new Font("Impact",Font.BOLD, 22);
    public static Color titColor = new Color(24, 42, 58);
    public static Font subTitulo = new Font("Impact",Font.PLAIN, 10);
    public static Font texto = new Font("Impact",Font.PLAIN,14);
    
    //Aqui tengo lamo a los otros
    
    public VentanaPrincipal(){
        GridBagLayout n = new GridBagLayout();
        this.getContentPane().setBackground(new Color(218, 232, 238));
        this.setSize(700,700);
        this.setLayout(n);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        
        JLabel tit = new JLabel("Buscador De Archivos");
        tit.setHorizontalAlignment(SwingConstants.CENTER);
        tit.setForeground(titColor);
        tit.setFont(titulo);
        this.add(tit);
        gbc.gridy++;
        this.add(tit);
        n.setConstraints(this, gbc);
        this.setVisible(true);
    }
}
