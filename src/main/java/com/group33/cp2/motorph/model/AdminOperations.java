package com.group33.cp2.motorph.model;

// Interface for Admin-specific operations: user management and system reports.
public interface AdminOperations {

    boolean manageUsers(int userId, String action);
    Report generateSystemReport(String reportType);

    // Deletes the employee with the given ID from all CSV stores.
    boolean deleteEmployee(String empId);
}
