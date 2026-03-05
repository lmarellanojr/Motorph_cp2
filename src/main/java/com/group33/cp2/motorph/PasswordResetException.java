package com.group33.cp2.motorph;

/**
 * Thrown by {@link PasswordResetService} when a password reset operation cannot
 * be completed due to a business-rule violation (e.g., employee not found,
 * request already processed).
 *
 * @author Group13
 * @version 1.0
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
