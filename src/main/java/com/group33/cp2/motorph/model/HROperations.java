package com.group33.cp2.motorph.model;

/**
 * Contract for HR department-specific operations in the MotorPH Payroll System.
 *
 * <p>Implemented by the {@link HR} department employee subclass. Defines the
 * leave management operations that are exclusive to HR staff.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers interact through
 * this interface, decoupling the HR workflow from the concrete {@link HR} implementation.</p>
 *
 * @author Group 33
 * @version 2.1
 */
public interface HROperations {

    boolean approveLeave(String leaveId, String remark);
    boolean rejectLeave(String leaveId, String remark);
    Employee viewEmployeeRecords(String employeeId);

    /**
     * Deletes the employee with the given ID from all CSV stores.
     *
     * @param empId the ID of the employee to delete
     * @return {@code true} if the employee was found and deleted
     */
    boolean deleteEmployee(String empId);
}
