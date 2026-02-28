package com.group33.cp2.motorph;

import com.group33.cp2.motorph.forms.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main entry point for the MotorPH Payroll System (CP2).
 * Sets the Nimbus look-and-feel and opens the login screen.
 *
 * @author Group13
 * @version 1.0
 */
public class Main extends javax.swing.JFrame {

    /**
     * Application entry point. Applies the UI theme and launches the login window.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        setLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            LoginFrame form = new LoginFrame();
            form.setVisible(true);
        });
    }

    /**
     * Attempts to set the Nimbus look-and-feel. Falls back to the platform default if unavailable.
     */
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
