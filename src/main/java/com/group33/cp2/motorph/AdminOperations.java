package com.group33.cp2.motorph;

/**
 * Contract for Admin department-specific operations.
 * Implemented by Admin employee subclass.
 */
public interface AdminOperations {

    // creates, updates, or deactivates a user account; action must be "create", "update", or "deactivate"
    boolean manageUsers(int userId, String action);

    // generates a system-wide report for the given report type
    Report generateSystemReport(String reportType);
}
