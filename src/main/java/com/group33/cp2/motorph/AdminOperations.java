package com.group33.cp2.motorph;

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

    /**
     * Creates, updates, or deactivates a user account.
     *
     * @param userId the unique identifier of the user account to manage
     * @param action the action to perform — must be {@code "create"},
     *               {@code "update"}, or {@code "deactivate"}
     * @return {@code true} if the action was successful; {@code false} otherwise
     */
    boolean manageUsers(int userId, String action);

    /**
     * Generates a system-wide report of the given type.
     *
     * @param reportType the type of report to generate (e.g. "payroll", "attendance")
     * @return a {@link Report} containing the generated report content
     */
    Report generateSystemReport(String reportType);
}
