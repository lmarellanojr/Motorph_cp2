package com.group33.cp2.motorph.util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Centralized utility for common confirmation dialogs shared across all frames.
 *
 * <p>Eliminates duplicated {@link JOptionPane#showConfirmDialog} calls for exit and
 * logout actions throughout the forms layer.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> Callers do not need to know the dialog
 * message strings, title text, or option type — they receive a simple boolean result.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class DialogUtil {

    private DialogUtil() {
        // Utility class — no instances
    }

    /**
     * Shows a "Confirm Exit" dialog and returns whether the user confirmed.
     *
     * @param parent the parent component for dialog positioning; may be null
     * @return {@code true} if the user clicked Yes, {@code false} otherwise
     */
    public static boolean confirmExit(Component parent) {
        return JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to exit?", "Confirm Exit",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Shows a "Confirm Logout" dialog and returns whether the user confirmed.
     *
     * @param parent the parent component for dialog positioning; may be null
     * @return {@code true} if the user clicked Yes, {@code false} otherwise
     */
    public static boolean confirmLogout(Component parent) {
        return JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to log out?", "Confirm Logout",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
