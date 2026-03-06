package com.group33.cp2.motorph.service;

/**
 * Thrown by {@link PasswordResetService} when a password reset operation cannot
 * be completed due to a business-rule violation (e.g., employee not found,
 * request already processed).
 */
public class PasswordResetException extends Exception {

    /**
     * Constructs a {@code PasswordResetException} with the given message.
     *
     * @param message a human-readable description of why the reset failed
     */
    public PasswordResetException(String message) {
        super(message);
    }
}
