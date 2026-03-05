package com.group33.cp2.motorph;

/**
 * Represents a password reset request submitted by an employee.
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> Sensitive fields are private.
 * {@code employeeNumber} and {@code employeeName} are immutable after construction
 * (final). Mutable fields (status, admin info, date) are changed through setters.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class PasswordResetRequest {

    private final String employeeNumber;
    private final String employeeName;
    private final String dateOfRequest;
    private String status;
    private String adminName;
    private String adminEmployeeNumber;
    private String dateOfReset;

    /**
     * Constructs a full {@code PasswordResetRequest} with all fields.
     * Used when reading existing records from the CSV.
     *
     * @param employeeNumber       the employee who requested the reset
     * @param employeeName         the employee's full name
     * @param dateOfRequest        date the request was submitted
     * @param status               "Pending" or "Approved"
     * @param adminName            name of the IT admin who processed it (empty if pending)
     * @param adminEmployeeNumber  employee number of the IT admin (empty if pending)
     * @param dateOfReset          date the reset was processed (empty if pending)
     */
    public PasswordResetRequest(String employeeNumber, String employeeName,
                                String dateOfRequest, String status,
                                String adminName, String adminEmployeeNumber,
                                String dateOfReset) {
        this.employeeNumber       = employeeNumber;
        this.employeeName         = employeeName;
        this.dateOfRequest        = dateOfRequest;
        this.status               = status;
        this.adminName            = adminName;
        this.adminEmployeeNumber  = adminEmployeeNumber;
        this.dateOfReset          = dateOfReset;
    }

    /**
     * Constructs a new pending {@code PasswordResetRequest}.
     * Sets status to "Pending" and leaves admin fields empty.
     *
     * @param employeeNumber the employee who needs the reset
     * @param employeeName   the employee's full name
     * @param dateOfRequest  date the request was submitted
     */
    public PasswordResetRequest(String employeeNumber, String employeeName, String dateOfRequest) {
        this.employeeNumber       = employeeNumber;
        this.employeeName         = employeeName;
        this.dateOfRequest        = dateOfRequest;
        this.status               = "Pending";
        this.adminName            = "";
        this.adminEmployeeNumber  = "";
        this.dateOfReset          = "";
    }

    // =========================================================================
    //  Getters
    // =========================================================================

    /** @return the employee number who requested the reset */
    public String getEmployeeNumber() { return employeeNumber; }

    /** @return the employee's full name */
    public String getEmployeeName() { return employeeName; }

    /** @return the date the request was submitted */
    public String getDateOfRequest() { return dateOfRequest; }

    /** @return the current status ("Pending" or "Approved") */
    public String getStatus() { return status; }

    /** @return name of the IT admin who processed the reset */
    public String getAdminName() { return adminName; }

    /** @return employee number of the IT admin */
    public String getAdminEmployeeNumber() { return adminEmployeeNumber; }

    /** @return date the reset was processed */
    public String getDateOfReset() { return dateOfReset; }

    // =========================================================================
    //  Setters for mutable fields
    // =========================================================================

    /** @param status new status */
    public void setStatus(String status) { this.status = status; }

    /** @param adminName name of the IT admin */
    public void setAdminName(String adminName) { this.adminName = adminName; }

    /** @param adminEmployeeNumber employee number of the IT admin */
    public void setAdminEmployeeNumber(String adminEmployeeNumber) {
        this.adminEmployeeNumber = adminEmployeeNumber;
    }

    /** @param dateOfReset date the reset was processed */
    public void setDateOfReset(String dateOfReset) { this.dateOfReset = dateOfReset; }

    /**
     * Returns this request as a 7-element String array for CSV serialization.
     * Column order: employeeNumber, employeeName, dateOfRequest, status,
     *               adminName, adminEmployeeNumber, dateOfReset.
     *
     * @return CSV row representation
     */
    public String[] toArray() {
        return new String[]{
            employeeNumber,
            employeeName,
            dateOfRequest,
            status,
            adminName,
            adminEmployeeNumber,
            dateOfReset
        };
    }
}
