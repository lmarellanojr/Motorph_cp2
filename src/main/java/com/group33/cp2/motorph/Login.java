package com.group33.cp2.motorph;

/**
 * Represents the login credentials and role of an employee in the MotorPH system.
 * Provides a constructor to initialise login data, as well as getters and setters.
 *
 * @author Group13
 * @version 1.0
 */
public class Login {

    private String employeeID;
    private String username;
    private String password;
    private Role role;

    /**
     * Constructs a Login with the specified credentials and role.
     *
     * @param employeeID unique employee identifier
     * @param username   login username
     * @param password   login password
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
