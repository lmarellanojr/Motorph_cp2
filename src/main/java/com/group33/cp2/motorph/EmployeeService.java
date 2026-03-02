package com.group33.cp2.motorph;

import java.util.List;

/**
 * Service class for managing employee data. Provides methods to add, delete,
 * update, and retrieve employees, as well as associate attendance records and
 * manage CSV persistence.
 *
 * @author Group13
 * @version 1.0
 */
public final class EmployeeService {

    private List<Employee> employeeList;
    private final EmployeeCSVHandler employeeCSVHandler;
    private final AttendanceService attendanceService;

    /**
     * Constructs an {@code EmployeeService}, initialising the CSV handler,
     * attendance service, and loading the initial employee list.
     */
    public EmployeeService() {
        employeeCSVHandler = new EmployeeCSVHandler();
        attendanceService = new AttendanceService();
        reloadEmployees();
    }

    /**
     * Adds a new employee and updates both the in-memory list and the CSV file.
     *
     * @param employee the Employee object to add
     */
    public void addEmployee(Employee employee) {
        employeeCSVHandler.addEmployee(employee);
        reloadEmployees();
    }

    /**
     * Deletes an employee from memory and the CSV file.
     *
     * @param employeeId the ID of the employee to delete
     * @return {@code true} if deleted; {@code false} if not found
     */
    public boolean deleteEmployee(String employeeId) {
        boolean deleted = employeeCSVHandler.deleteEmployee(employeeId);
        if (deleted) {
            reloadEmployees();
        }
        return deleted;
    }

    /**
     * Updates an existing employee's information in memory and in the CSV file.
     *
     * @param updatedEmployee the modified employee object
     */
    public void updateEmployee(Employee updatedEmployee) {
        employeeCSVHandler.updateEmployee(updatedEmployee);
        reloadEmployees();
    }

    /**
     * Returns a unique new employee ID by incrementing the highest existing ID.
     *
     * @return generated employee ID as a string
     */
    public String generateEmployeeID() {
        if (employeeList.isEmpty()) {
            return "1";
        }
        String lastEmployeeID = employeeList.get(employeeList.size() - 1).getEmployeeID();
        int nextId = Integer.parseInt(lastEmployeeID) + 1;
        return String.valueOf(nextId);
    }

    /**
     * Gets the highest existing employee ID as an integer.
     *
     * @return last employee ID as an int, or {@code 0} if the list is empty
     */
    public int getLastEmployeeID() {
        if (employeeList.isEmpty()) {
            return 0;
        }
        String lastEmployeeID = employeeList.get(employeeList.size() - 1).getEmployeeID();
        return Integer.parseInt(lastEmployeeID);
    }

    /**
     * Finds an employee by their unique ID.
     *
     * @param employeeId the ID to search for
     * @return the matching {@link Employee}, or {@code null} if not found
     */
    public Employee getEmployeeById(String employeeId) {
        for (Employee employee : employeeList) {
            if (employee.getEmployeeID().equals(employeeId)) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Reloads all employees from the CSV file and re-associates their attendance records.
     */
    public void reloadEmployees() {
        employeeList = employeeCSVHandler.readEmployees();
        loadAndAssociateAttendances(attendanceService.getAllAttendance());
    }

    /**
     * Returns all employees currently held in memory.
     *
     * @return list of employees
     */
    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    /**
     * Links attendance records to their corresponding employees.
     *
     * @param attendanceList list of all attendance entries
     */
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
