package com.group33.cp2.motorph;

/**
 * Functional interface invoked by {@link ResetPasswordProcessor} after a
 * successful password reset.
 *
 * <p>Decouples the business-logic package from the GUI package — the GUI passes
 * a lambda (e.g., {@code this::loadPasswordResetRequests}) that the processor
 * calls back without importing any GUI types.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The processor depends on this
 * interface, not any concrete GUI class.</p>
 *
 * @author Group13
 * @version 1.0
 */
@FunctionalInterface
public interface PasswordResetCallback {

    /**
     * Called when a password reset has been successfully completed.
     */
    void onPasswordResetComplete();
}
