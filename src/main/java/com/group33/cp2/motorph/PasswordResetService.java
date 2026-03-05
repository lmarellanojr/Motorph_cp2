package com.group33.cp2.motorph;

import com.group33.cp2.motorph.data.EmployeeDetailsReader;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Business-logic service for processing password reset requests.
 *
 * <p>Generates a temporary password, BCrypt-hashes it, writes it to Login.csv
 * (setting {@code changePassword=YES}), and marks the request as Approved in
 * {@code Password_Reset_Requests.csv}.</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> All file-read/write details
 * are delegated to {@link EmployeeDetailsReader} and {@link PasswordResetReader}.
 * This class only contains business logic.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class PasswordResetService {

    private static final String SPECIAL_CHARS = "!@#$%^&*";

    private final EmployeeDetailsReader loginReader = new EmployeeDetailsReader();

    /**
     * Resets the password for the given employee, then marks the pending reset
     * request as Approved.
     *
     * @param employeeNumber the employee whose password will be reset
     * @param adminName      the IT admin's full name
     * @param adminEmpNum    the IT admin's employee number
     * @return {@code true} on success
     * @throws IOException            if a CSV read/write fails
     * @throws PasswordResetException if the employee is not found or no pending request exists
     */
    public boolean resetPassword(String employeeNumber, String adminName, String adminEmpNum)
            throws IOException, PasswordResetException {

        // Generate a temporary password
        String tempPassword = generateTemporaryPassword(employeeNumber);
        String hashed       = BCrypt.hashpw(tempPassword, BCrypt.gensalt());

        // Update Login.csv with hashed temp password and changePassword=YES
        if (!loginReader.changeUserPassword(employeeNumber, hashed)) {
            throw new PasswordResetException("Employee not found in login data: " + employeeNumber);
        }

        // Mark the pending request as approved in Password_Reset_Requests.csv
        if (!PasswordResetReader.approveRequest(employeeNumber, adminName, adminEmpNum)) {
            throw new PasswordResetException(
                    "Password reset request not found or already approved for: " + employeeNumber);
        }

        return true;
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    /**
     * Generates a predictable-format temporary password:
     * {@code "Default" + employeeNumber + specialChar + twoDigitNumber}
     */
    private String generateTemporaryPassword(String employeeNumber) {
        Random random = new Random();
        char   specialChar   = SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
        String twoDigitNum   = String.format("%02d", random.nextInt(100));
        return "Default" + employeeNumber + specialChar + twoDigitNum;
    }
}
