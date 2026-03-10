package com.group33.cp2.motorph;

import com.group33.cp2.motorph.forms.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

// App entry point. Sets up the Nimbus theme and opens the login screen.
public class Main extends javax.swing.JFrame {

    public static void main(String[] args) {
        setLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            LoginFrame form = new LoginFrame();
            form.setVisible(true);
        });
    }

    // falls back to the platform default if Nimbus isn't available
    public static void setLookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
