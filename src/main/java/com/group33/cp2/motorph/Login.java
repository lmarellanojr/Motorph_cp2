package com.group33.cp2.motorph;

/**
 * Represents the login credentials and role of an employee in the MotorPH system.
 *
 * <p><strong>Encapsulation (BP8):</strong> The {@code password} field is {@code private}
 * and is never exposed via a getter. Instead, {@link #verifyPassword(String)} allows
 * callers to check a candidate password without ever reading the stored value directly.
 * This prevents the password from being extracted from a {@code Login} object at runtime.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class Login {

    private String employeeID;
    private String username;
    // BP8: password has no public getter — access is controlled via verifyPassword()
    private String password;
    private Role role;

    /**
     * Constructs a Login with the specified credentials and role.
     *
     * @param employeeID unique employee identifier
     * @param username   login username
     * @param password   login password (stored privately; use verifyPassword() to check)
     * @param role       the user's role (ADMIN, EMPLOYEE, or PAYROLL_STAFF)
     */
    public Login(String employeeID, String username, String password, Role role) {
        this.employeeID = employeeID;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // BP8: getPassword() removed — the password is never exposed directly

    /**
     * Verifies whether the supplied candidate password matches the stored password.
     * The stored password is never returned; only a boolean result is provided.
     *
     * @param candidatePassword the plain-text password to verify
     * @return {@code true} if the candidate matches the stored password; {@code false} otherwise
     */
    public boolean verifyPassword(String candidatePassword) {
        return this.password != null && this.password.equals(candidatePassword);
    }

    /**
     * Updates the stored password.
     * The new value replaces the old one without exposing either.
     *
     * @param newPassword the new password to store
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
