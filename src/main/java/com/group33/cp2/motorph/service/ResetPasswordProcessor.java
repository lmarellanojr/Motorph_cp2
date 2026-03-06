package com.group33.cp2.motorph.service;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Orchestrates the password reset workflow: invokes {@link PasswordResetService},
 * shows result dialogs, and calls the provided callback on success.
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> This class depends on the
 * {@link PasswordResetCallback} functional interface rather than any concrete GUI
 * class, preventing a circular dependency between the domain package and the GUI
 * package.</p>
 */
public class ResetPasswordProcessor {

    private final PasswordResetService passwordResetService;

    /**
     * Constructs a {@code ResetPasswordProcessor} with the given service.
     *
     * @param passwordResetService the service that performs the actual reset; must not be null
     */
    public ResetPasswordProcessor(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Resets the password for the given employee, shows a result dialog,
     * and invokes {@code onSuccess} if the reset was successful.
     *
     * @param employeeNumber the employee number to reset
     * @param adminName      the IT admin's full name
     * @param adminEmpNum    the IT admin's employee number
     * @param onSuccess      callback invoked after a successful reset; must not be null
     * @return {@code true} if the reset succeeded; {@code false} otherwise
     */
    public boolean resetPassword(String employeeNumber, String adminName,
                                  String adminEmpNum, PasswordResetCallback onSuccess) {
        try {
            passwordResetService.resetPassword(employeeNumber, adminName, adminEmpNum);
            JOptionPane.showMessageDialog(null,
                    "Password reset successful for employee " + employeeNumber
                    + ".\nA temporary password has been set. The employee must change it on next login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            onSuccess.onPasswordResetComplete();
            return true;
        } catch (PasswordResetException e) {
            JOptionPane.showMessageDialog(null, "Reset failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "I/O error during reset: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
