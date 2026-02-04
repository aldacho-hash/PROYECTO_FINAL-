
package Clases;

import Views.LoginUsuarios;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginUsuarios loginFrame = new LoginUsuarios();
                loginFrame.setVisible(true); 
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setLocationRelativeTo(null);
            }
        });
    }
}
