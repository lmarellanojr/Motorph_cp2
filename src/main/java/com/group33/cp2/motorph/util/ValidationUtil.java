package com.group33.cp2.motorph.util;

/**
 * Centralized utility for validating Philippine government ID number formats.
 *
 * <p>All methods perform a {@code null} check and a regex match, returning
 * {@code true} only when the input matches the expected format exactly.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> Callers depend on the method contract
 * (valid vs. invalid) without knowing the underlying regex pattern.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Utility class — no instances
    }

    /**
     * Validates an SSS number in the format {@code XX-XXXXXXX-X} (e.g., 44-4506057-3).
     *
     * @param sss the SSS number string to validate; may be null
     * @return {@code true} if the format matches, {@code false} otherwise
     */
    public static boolean isValidSSS(String sss) {
        return sss != null && sss.matches("\\d{2}-\\d{7}-\\d{1}");
    }

    /**
     * Validates a TIN number in the format {@code XXX-XXX-XXX-XXX} (e.g., 442-605-657-000).
     *
     * @param tin the TIN string to validate; may be null
     * @return {@code true} if the format matches, {@code false} otherwise
     */
    public static boolean isValidTIN(String tin) {
        return tin != null && tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    }

    /**
     * Validates a PhilHealth number as exactly 12 digits (e.g., 820126853951).
     *
     * @param ph the PhilHealth number string to validate; may be null
     * @return {@code true} if the format matches, {@code false} otherwise
     */
    public static boolean isValidPhilHealth(String ph) {
        return ph != null && ph.matches("\\d{12}");
    }

    /**
     * Validates a Pag-IBIG number as exactly 12 digits (e.g., 691295330870).
     *
     * @param pag the Pag-IBIG number string to validate; may be null
     * @return {@code true} if the format matches, {@code false} otherwise
     */
    public static boolean isValidPagIBIG(String pag) {
        return pag != null && pag.matches("\\d{12}");
    }

    /**
     * Validates a phone number in the format {@code XXX-XXX-XXXX} (e.g., 966-860-2700).
     *
     * @param phone the phone number string to validate; may be null
     * @return {@code true} if the format matches, {@code false} otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{3}-\\d{3}-\\d{4}");
    }
}
