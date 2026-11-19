/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba;
import java.io.File;
import java.io.IOException;
import prueba.Vista.VentanaPrincipal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TestMiFile {
    static MiFile mf = new MiFile();
    static Scanner lea = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = 0;

        do {
            System.out.println("\n===== MENU =====\n");
            System.out.println("1- Set el archivo/folder");
            System.out.println("2- Ver Información");
            System.out.println("3- Crear un archivo");
            System.out.println("4- Crear un folder");
            System.out.println("5- Salir");
            System.out.println("Escoja una opción: ");

            try {
                opcion = lea.nextInt();
                switch (opcion) {
                    case 1:
                        VentanaPrincipal n = new VentanaPrincipal();
                        n.setVisible(true);
                        System.out.println("Ingresar la ruta: ");
                        String ruta = lea.next();
                        mf.setFile(ruta);
                        break;
                    case 2:
                        mf.viewInfo();
                        break;
                    case 3:
                        try {
                            mf.crearFile();
                        } catch (IOException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 4:
                        mf.crearDir();
                        break;
                    case 5:
                        System.out.println("Saliendo....");
                        break;
                    default:
                        System.out.println("Escoja una opción correcta.");
                        break;
                }
            } catch (InputMismatchException e) {
                lea.nextLine();
                System.out.println("Por favor ingresar una opción correcta");
            } catch (NullPointerException e) {
                System.out.println("Debe seleccionar la opción 1 por lo menos una vez");
            }
        } while (opcion != 5);
    }
}