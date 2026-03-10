package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.EmployeeDetailsReader;

import java.io.IOException;

/**
 * Service layer facade for authentication-related data access.
 *
 * <p>Wraps {@link EmployeeDetailsReader} login operations so that {@code forms/}
 * classes — specifically {@link com.group33.cp2.motorph.forms.LoginFrame} — never
 * import from the {@code dao/} package.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The details of how login data is
 * stored (Login.csv format, column indices) are hidden behind this interface.
 * BCrypt hashing remains the caller's responsibility; this service only reads and
 * writes raw hashed strings.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class AuthService {

    private final EmployeeDetailsReader employeeDetailsReader;

    /**
     * Constructs an AuthService with a default EmployeeDetailsReader instance.
     */
    public AuthService() {
        this.employeeDetailsReader = new EmployeeDetailsReader();
    }

    /**
     * Looks up a Login.csv row by username.
     *
     * <p>The returned array has columns:
     * [0]=empNum, [1]=username, [2]=roleName, [3]=bcryptHash, [4]=changePassword.</p>
     *
     * @param username the username to look up (case-insensitive)
     * @return the matching row, or {@code null} if not found
     */
    public String[] getLoginDataByUsername(String username) {
        return employeeDetailsReader.getLoginDataByUsername(username);
    }

    /**
     * Updates the BCrypt password hash for the given employee and clears the
     * {@code changePassword} flag (sets it to {@code "NO"}).
     *
     * <p>Used by the forced-change flow when an employee sets their own new password.</p>
     *
     * @param empId             the employee number to update
     * @param newHashedPassword the new BCrypt-hashed password
     * @return {@code true} if the row was found and updated
     * @throws IOException if the Login CSV cannot be read or written
     */
    public boolean changeUserPassword(String empId, String newHashedPassword) throws IOException {
        return employeeDetailsReader.changeUserPassword(empId, newHashedPassword);
    }
}
