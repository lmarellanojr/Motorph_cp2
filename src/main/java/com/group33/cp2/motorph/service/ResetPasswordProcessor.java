package com.group33.cp2.motorph.service;

import java.io.IOException;
import javax.swing.JOptionPane;

// Orchestrates the password reset workflow: calls PasswordResetService, shows dialogs,
// and invokes the PasswordResetCallback on success. Uses the callback interface to
// avoid a circular dependency between the service and GUI packages.
public class ResetPasswordProcessor {

    private final PasswordResetService passwordResetService;

    public ResetPasswordProcessor(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Resets the password, shows a result dialog, and invokes onSuccess on success.
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
