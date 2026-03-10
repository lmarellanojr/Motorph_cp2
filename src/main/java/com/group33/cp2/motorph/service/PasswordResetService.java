package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.EmployeeDetailsReader;
import com.group33.cp2.motorph.dao.PasswordResetReader;
import com.group33.cp2.motorph.model.PasswordResetRequest;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
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

        String tempPassword = generateTemporaryPassword(employeeNumber);
        // BCrypt-hash the temp password; changePassword=YES forces a change on next login
        String hashed = BCrypt.hashpw(tempPassword, BCrypt.gensalt());

        if (!loginReader.changeUserPassword(employeeNumber, hashed, "YES")) {
            throw new PasswordResetException("Employee not found in login data: " + employeeNumber);
        }

        if (!PasswordResetReader.approveRequest(employeeNumber, adminName, adminEmpNum)) {
            throw new PasswordResetException(
                    "Password reset request not found or already approved for: " + employeeNumber);
        }

        return true;
    }

    /**
     * Returns all password reset requests from the CSV.
     *
     * <p>Delegates to {@link PasswordResetReader#getAllRequests()} so that
     * {@code forms/} classes never need to import from {@code dao/}.</p>
     *
     * @return a list of all {@link PasswordResetRequest} records; never {@code null}
     */
    public List<PasswordResetRequest> getAllRequests() {
        return PasswordResetReader.getAllRequests();
    }

    /** Generates a temporary password: {@code "Default" + empNum + specialChar + twoDigits} */
    private String generateTemporaryPassword(String employeeNumber) {
        Random random = new Random();
        char   specialChar   = SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
        String twoDigitNum   = String.format("%02d", random.nextInt(100));
        return "Default" + employeeNumber + specialChar + twoDigitNum;
    }
}
