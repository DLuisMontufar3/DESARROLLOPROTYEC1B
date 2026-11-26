
package tutoralerta.app;

import javax.swing.SwingUtilities;
import tutoralerta.vista.Estilo;
import tutoralerta.vista.MenuPrincipal;

public class Principal {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Fuentes y colores comunes antes de crear cualquier ventana
            Estilo.instalarTema();

            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);

        });
    }
}
