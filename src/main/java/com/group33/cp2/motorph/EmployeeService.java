package com.group33.cp2.motorph;

import java.util.Collections;
import java.util.List;

/**
 * Manages employee data: load from CSV, add, update, delete, and link attendance records.
 */
public final class EmployeeService {

    private List<Employee> employeeList;
    private final EmployeeCSVHandler employeeCSVHandler;
    private final AttendanceService attendanceService;

    public EmployeeService() {
        employeeCSVHandler = new EmployeeCSVHandler();
        attendanceService = new AttendanceService();
        reloadEmployees();
    }

    public void addEmployee(Employee employee) {
        employeeCSVHandler.addEmployee(employee);
        reloadEmployees();
    }

    public boolean deleteEmployee(String employeeId) {
        boolean deleted = employeeCSVHandler.deleteEmployee(employeeId);
        if (deleted) {
            reloadEmployees();
        }
        return deleted;
    }

    public void updateEmployee(Employee updatedEmployee) {
        employeeCSVHandler.updateEmployee(updatedEmployee);
        reloadEmployees();
    }

    // increments the last ID to get the next one
    public String generateEmployeeID() {
        if (employeeList.isEmpty()) {
            return "1";
        }
        String lastEmployeeID = employeeList.get(employeeList.size() - 1).getEmployeeID();
        int nextId = Integer.parseInt(lastEmployeeID) + 1;
        return String.valueOf(nextId);
    }

    public int getLastEmployeeID() {
        if (employeeList.isEmpty()) {
            return 0;
        }
        String lastEmployeeID = employeeList.get(employeeList.size() - 1).getEmployeeID();
        return Integer.parseInt(lastEmployeeID);
    }

    public Employee getEmployeeById(String employeeId) {
        for (Employee employee : employeeList) {
            if (employee.getEmployeeID().equals(employeeId)) {
                return employee;
            }
        }
        return null;
    }

    public void reloadEmployees() {
        employeeList = employeeCSVHandler.readEmployees();
        loadAndAssociateAttendances(attendanceService.getAllAttendance());
    }

    // returns read-only view; use addEmployee/deleteEmployee/updateEmployee to modify
    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employeeList);
    }

    /**
     * Links attendance records to their corresponding employees.
     * Uses {@link Employee#addAttendance(Attendance)} to respect encapsulation
     * (the attendance list is exposed as an unmodifiable view).
     *
     * @param attendanceList list of all attendance entries
     */
    public void loadAndAssociateAttendances(List<Attendance> attendanceList) {
        for (Attendance attendance : attendanceList) {
            for (Employee employee : employeeList) {
                if (employee.getEmployeeID().equals(attendance.getEmployeeID())) {
                    employee.addAttendance(attendance);
                    break;
                }
            }
        }
    }
}
