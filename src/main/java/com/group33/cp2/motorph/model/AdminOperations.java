package com.group33.cp2.motorph.model;

/**
 * Contract for Admin department-specific operations in the MotorPH Payroll System.
 *
 * <p>Implemented by the {@link Admin} department employee subclass. Defines the
 * user-management and system-report operations exclusive to administrators.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — the interface separates
 * the "what" (admin capabilities) from the "how" (the concrete {@link Admin} implementation).</p>
 *
 * @author Group 33
 * @version 2.0
 */
public interface AdminOperations {

    boolean manageUsers(int userId, String action);
    Report generateSystemReport(String reportType);

    /**
     * Deletes the employee with the given ID from all CSV stores.
     *
     * @param empId the ID of the employee to delete
     * @return {@code true} if the employee was found and deleted
     */
    boolean deleteEmployee(String empId);
}
