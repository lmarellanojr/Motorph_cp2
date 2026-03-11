package com.group33.cp2.motorph.service;

import java.io.IOException;

// Orchestrates the password reset workflow: calls PasswordResetService and invokes the
// PasswordResetCallback on success. Uses the callback interface to avoid a circular
// dependency between the service and GUI packages.
// Dialog display is the caller's responsibility — this class has no javax.swing imports.
// After calling resetPassword(), the caller should check isLastResetSuccessful() and
// getLastResultMessage() to display an appropriate dialog.
public class ResetPasswordProcessor {

    private final PasswordResetService passwordResetService;

    // Stores the human-readable outcome of the most recent resetPassword() call.
    // The calling form reads this to display an appropriate dialog.
    private String lastResultMessage = "";

    // True if the most recent resetPassword() call succeeded; false otherwise.
    private boolean lastResetSuccessful = false;

    public ResetPasswordProcessor(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Returns the human-readable result message from the most recent resetPassword() call.
    // Empty string if resetPassword() has not been called yet.
    public String getLastResultMessage() {
        return lastResultMessage;
    }

    // Returns true if the most recent resetPassword() call completed without error.
    public boolean isLastResetSuccessful() {
        return lastResetSuccessful;
    }

    // Resets the password and invokes onSuccess if the operation succeeds.
    // Sets lastResultMessage and lastResetSuccessful so the calling form can display
    // an appropriate dialog without this service class importing javax.swing.
    // Returns true on success, false on any error.
    public boolean resetPassword(String employeeNumber, String adminName,
                                  String adminEmpNum, PasswordResetCallback onSuccess) {
        try {
            passwordResetService.resetPassword(employeeNumber, adminName, adminEmpNum);
            lastResultMessage = "Password reset successful for employee " + employeeNumber
                    + ".\nA temporary password has been set. The employee must change it on next login.";
            lastResetSuccessful = true;
            onSuccess.onPasswordResetComplete();
            return true;
        } catch (PasswordResetException e) {
            lastResultMessage = "Reset failed: " + e.getMessage();
            lastResetSuccessful = false;
            System.err.println("ResetPasswordProcessor: business rule violation — " + e.getMessage());
            return false;
        } catch (IOException e) {
            lastResultMessage = "I/O error during reset: " + e.getMessage();
            lastResetSuccessful = false;
            System.err.println("ResetPasswordProcessor: I/O error — " + e.getMessage());
            return false;
        }
    }
}
