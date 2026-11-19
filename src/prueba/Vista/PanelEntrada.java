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
public class PanelEntrada extends JPanel{
    
    
    public PanelEntrada(){
    this.setOpaque(false);
    GridBagLayout n = new GridBagLayout();
    this.setLayout(n);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx=0;
    gbc.gridy=0;
    String[] opciones = {"Ingresar Ruta", ""}
    JComboBox<String> rtxt = new JComboBox<>();
    add();
    
    
    n.setConstraints(this, gbc);
    }
}
