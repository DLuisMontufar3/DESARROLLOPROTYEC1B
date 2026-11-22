
package tutoralerta.app;

import javax.swing.SwingUtilities;
import tutoralerta.vista.MenuPrincipal;

public class Principal {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);

        });
    }
}
