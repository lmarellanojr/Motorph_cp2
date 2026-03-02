package com.group33.cp2.motorph;

/**
 * Contract for HR department-specific operations in the MotorPH Payroll System.
 *
 * <p>Implemented by the {@link HR} department employee subclass. Defines the
 * leave management operations that are exclusive to HR staff.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers interact through
 * this interface, decoupling the HR workflow from the concrete {@link HR} implementation.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface HROperations {

    /**
     * Approves a pending leave request.
     *
     * @param leaveId the unique identifier of the leave request
     * @return {@code true} if the leave was successfully approved; {@code false} otherwise
     */
    boolean approveLeave(int leaveId);

    /**
     * Rejects a pending leave request with a stated reason.
     *
     * @param leaveId the unique identifier of the leave request
     * @param reason  the reason for rejection; must not be null or blank
     * @return {@code true} if the leave was successfully rejected; {@code false} otherwise
     */
    boolean rejectLeave(int leaveId, String reason);

    /**
     * Retrieves an employee's personal and employment record by ID.
     *
     * @param employeeId the unique identifier of the employee to look up
     * @return the matching {@link Employee}, or {@code null} if not found
     */
    Employee viewEmployeeRecords(String employeeId);
}
