package com.group33.cp2.motorph.model;

/**
 * Holds login credentials and role for one user. Password is never exposed directly.
 */
public class Login {

    private String employeeID;
    private String username;
    // no getter for password; use verifyPassword() instead
    private String password;
    private Role role;

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

    // getPassword() not provided

    // returns true if the candidate matches the stored password
    public boolean verifyPassword(String candidatePassword) {
        return this.password != null && this.password.equals(candidatePassword);
    }

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
