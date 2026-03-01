package com.group33.cp2.motorph;

import java.util.Collections;
import java.util.List;

/**
 * Service class for managing employee data. Provides methods to add, delete,
 * update, and retrieve employees, as well as associate attendance records and
 * manage CSV persistence.
 *
 * <p><strong>Encapsulation (BP9):</strong> {@link #getAllEmployees()} returns an
 * unmodifiable list view so callers cannot add, remove, or reorder the internal
 * employee list directly. All mutations must go through the service methods
 * ({@link #addEmployee}, {@link #deleteEmployee}, {@link #updateEmployee}).
 * Similarly, {@link #loadAndAssociateAttendances} uses {@link Employee#addAttendance}
 * instead of mutating the list returned by {@code getAttendanceList()}.</p>
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
     * Returns an unmodifiable view of all employees currently held in memory.
     * To modify the employee list, use {@link #addEmployee}, {@link #deleteEmployee},
     * or {@link #updateEmployee}.
     *
     * @return unmodifiable list of employees
     */
    public List<Employee> getAllEmployees() {
        // BP9: return unmodifiable view — callers cannot alter the internal list
        return Collections.unmodifiableList(employeeList);
    }

    /**
     * Links attendance records to their corresponding employees.
     * Uses {@link Employee#addAttendance(Attendance)} to append records through the
     * controlled mutator rather than bypassing encapsulation via the list getter.
     *
     * @param attendanceList list of all attendance entries
     */
    public void loadAndAssociateAttendances(List<Attendance> attendanceList) {
        for (Attendance attendance : attendanceList) {
            for (Employee employee : employeeList) {
                if (employee.getEmployeeID().equals(attendance.getEmployeeID())) {
                    // BP9: use addAttendance() instead of employee.getAttendanceList().add()
                    employee.addAttendance(attendance);
                    break;
                }
            }
        }
    }
}
