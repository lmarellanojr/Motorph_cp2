package com.group33.cp2.motorph;

import java.util.Collections;
import java.util.List;

/**
 * Manages employee data: load from CSV, add, update, delete, and link attendance records.
 *
 * <p><strong>OOP Pillar — Interface Polymorphism:</strong> The field {@code employeeDAO}
 * is declared as the {@link EmployeeDAO} interface type. At runtime the assigned object is
 * an {@link EmployeeCSVHandler}, but all mutation calls ({@code create}, {@code update},
 * {@code delete}) are dispatched through the interface reference. This means the persistence
 * strategy can be swapped (e.g., to a database-backed implementation) without changing any
 * call site in this class.</p>
 */
public final class EmployeeService {

    private List<Employee> employeeList;

    /**
     * Interface-typed reference for mutation operations (create / update / delete).
     * Assigned to {@link EmployeeCSVHandler} at construction time — demonstrates
     * interface polymorphism: callers see only the {@link EmployeeDAO} contract.
     */
    private final EmployeeDAO employeeDAO;

    /**
     * Concrete-typed reference retained only for {@code readEmployees()}, which returns
     * a full {@code List<Employee>} — a method that has no equivalent on {@link EmployeeDAO}
     * (the DAO {@code read} method returns a single record by ID).
     */
    private final EmployeeCSVHandler employeeCSVHandler;

    private final AttendanceService attendanceService;

    public EmployeeService() {
        // Both fields point to the same instance — employeeDAO holds it as the interface type.
        EmployeeCSVHandler handler = new EmployeeCSVHandler();
        this.employeeDAO = handler;          // interface polymorphism: EmployeeDAO reference
        this.employeeCSVHandler = handler;   // concrete reference — only for readEmployees()
        attendanceService = new AttendanceService();
        reloadEmployees();
    }

    /**
     * Persists a new employee record by dispatching through the {@link EmployeeDAO} interface.
     * At runtime the call resolves to {@link EmployeeCSVHandler#create(Employee)}.
     *
     * @param employee the employee to add
     */
    public void addEmployee(Employee employee) {
        employeeDAO.create(employee);   // interface polymorphism — dispatches via EmployeeDAO
        reloadEmployees();
    }

    /**
     * Removes an employee record by dispatching through the {@link EmployeeDAO} interface.
     * At runtime the call resolves to {@link EmployeeCSVHandler#delete(String)}.
     *
     * @param employeeId the ID of the employee to remove
     * @return {@code true} if deleted; {@code false} if not found
     */
    public boolean deleteEmployee(String employeeId) {
        boolean deleted = employeeDAO.delete(employeeId);   // interface polymorphism
        if (deleted) {
            reloadEmployees();
        }
        return deleted;
    }

    /**
     * Updates an employee record by dispatching through the {@link EmployeeDAO} interface.
     * At runtime the call resolves to {@link EmployeeCSVHandler#update(Employee)}.
     *
     * @param updatedEmployee the employee with updated fields
     */
    public void updateEmployee(Employee updatedEmployee) {
        employeeDAO.update(updatedEmployee);   // interface polymorphism — dispatches via EmployeeDAO
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
