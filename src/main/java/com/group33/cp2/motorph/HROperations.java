package com.group33.cp2.motorph;

/**
 * Contract for HR department-specific operations.
 * Implemented by HR employee subclass.
 */
public interface HROperations {

    // approves a pending leave request; returns true if approved
    boolean approveLeave(int leaveId);

    // rejects a pending leave request with a reason; returns true if rejected
    boolean rejectLeave(int leaveId, String reason);

    // retrieves an employee record by ID for review
    Employee viewEmployeeRecords(String employeeId);
}
