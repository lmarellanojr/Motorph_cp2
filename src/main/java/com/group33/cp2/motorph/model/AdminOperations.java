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
 * @author Group13
 * @version 2.0
 */
public interface AdminOperations {

    boolean manageUsers(int userId, String action);
    Report generateSystemReport(String reportType);
}
