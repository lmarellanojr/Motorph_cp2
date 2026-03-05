package com.group33.cp2.motorph;

import com.group33.cp2.motorph.data.AllowanceDetailsReader;
import com.group33.cp2.motorph.data.Salary;
import com.group33.cp2.motorph.data.SalaryDetailsReader;
import com.group33.cp2.motorph.data.EmployeeDetailsReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages employee data: load from the three split CSVs, add, update, delete,
 * and link attendance records.
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> Each reader class owns exactly one CSV
 * file. {@code EmployeeService} joins data from all three to produce {@link Employee}
 * objects, and fans out writes to all three on mutations. No caller ever touches a CSV
 * file directly.</p>
 */
public final class EmployeeService {

    private List<Employee> employeeList;

    private final EmployeeDetailsReader  employeeReader;
    private final SalaryDetailsReader    salaryReader;
    private final AllowanceDetailsReader allowanceReader;
    private final AttendanceService      attendanceService;

    public EmployeeService() {
        this.employeeReader    = new EmployeeDetailsReader();
        this.salaryReader      = new SalaryDetailsReader();
        this.allowanceReader   = new AllowanceDetailsReader();
        this.attendanceService = new AttendanceService();
        reloadEmployees();
    }

    // -------------------------------------------------------------------------
    //  CRUD — fan out to all three CSVs
    // -------------------------------------------------------------------------

    /**
     * Persists a new employee to Employee.csv, Salary.csv, and Allowance.csv.
     *
     * @param employee the employee to add
     */
    public void addEmployee(Employee employee) {
        try {
            employeeReader.addEmployee(toEmployeeRow(employee));
            salaryReader.addSalary(employee.getEmployeeID(), toSalary(employee));
            allowanceReader.addAllowance(employee.getEmployeeID(), employee.getAllowanceDetails());
        } catch (IOException e) {
            System.err.println("EmployeeService.addEmployee failed: " + e.getMessage());
        }
        reloadEmployees();
    }

    /**
     * Updates an employee's record across all three CSVs.
     *
     * @param employee the employee with updated fields
     */
    public void updateEmployee(Employee employee) {
        try {
            employeeReader.updateEmployee(toEmployeeRow(employee));
            salaryReader.updateSalary(employee.getEmployeeID(), toSalary(employee));
            allowanceReader.updateAllowance(employee.getEmployeeID(), employee.getAllowanceDetails());
        } catch (IOException e) {
            System.err.println("EmployeeService.updateEmployee failed: " + e.getMessage());
        }
        reloadEmployees();
    }

    /**
     * Removes an employee from all three CSVs.
     *
     * @param employeeId the ID of the employee to remove
     * @return {@code true} if the employee was found and removed
     */
    public boolean deleteEmployee(String employeeId) {
        boolean deleted = false;
        try {
            deleted = employeeReader.deleteEmployee(employeeId);
            salaryReader.deleteSalary(employeeId);
            allowanceReader.deleteAllowance(employeeId);
        } catch (IOException e) {
            System.err.println("EmployeeService.deleteEmployee failed: " + e.getMessage());
        }
        if (deleted) reloadEmployees();
        return deleted;
    }

    // -------------------------------------------------------------------------
    //  Reload — join data from all three readers
    // -------------------------------------------------------------------------

    /**
     * Re-reads all three CSVs and rebuilds the in-memory employee list.
     * Attendance records are linked after loading.
     */
    public void reloadEmployees() {
        List<Employee> loaded = new ArrayList<>();

        for (String[] row : employeeReader.getAllEmployees()) {
            try {
                String empId = row[0].trim();

                Salary    salary    = salaryReader.getSalary(empId);
                Allowance allowance = allowanceReader.getAllowance(empId);

                if (salary == null) {
                    System.err.println("EmployeeService: no salary row for " + empId + " — skipping");
                    continue;
                }
                if (allowance == null) {
                    allowance = new Allowance(empId, 0, 0, 0);
                }

                GovernmentDetails govDetails = new GovernmentDetails(
                    empId,
                    row[6].trim(),   // sssNumber
                    row[7].trim(),   // philhealthNumber
                    row[8].trim(),   // tinNumber
                    row[9].trim()    // pagibigNumber
                );

                String status = row[10].trim();
                Employee employee;

                if ("Probationary".equalsIgnoreCase(status)) {
                    employee = new ProbationaryEmployee(
                        empId, row[1].trim(), row[2].trim(), row[3].trim(),
                        row[4].trim(), row[5].trim(),
                        salary.getBasicSalary(), salary.getHourlyRate(), salary.getGrossSMRate(),
                        status, row[11].trim(), row[12].trim(),
                        allowance, govDetails
                    );
                } else {
                    employee = new RegularEmployee(
                        empId, row[1].trim(), row[2].trim(), row[3].trim(),
                        row[4].trim(), row[5].trim(),
                        salary.getBasicSalary(), salary.getHourlyRate(), salary.getGrossSMRate(),
                        status, row[11].trim(), row[12].trim(),
                        allowance, govDetails
                    );
                }

                loaded.add(employee);

            } catch (Exception e) {
                System.err.println("EmployeeService.reloadEmployees: skipping row — " + e.getMessage());
            }
        }

        employeeList = loaded;
        loadAndAssociateAttendances(attendanceService.getAllAttendance());
    }

    // -------------------------------------------------------------------------
    //  Query helpers
    // -------------------------------------------------------------------------

    /** Returns a read-only view of all employees. */
    public List<Employee> getAllEmployees() {
        return Collections.unmodifiableList(employeeList);
    }

    /** Returns the employee with the given ID, or {@code null} if not found. */
    public Employee getEmployeeById(String employeeId) {
        for (Employee e : employeeList) {
            if (e.getEmployeeID().equals(employeeId)) return e;
        }
        return null;
    }

    /** Returns the next available employee ID (last ID + 1). */
    public String generateEmployeeID() {
        if (employeeList.isEmpty()) return "10001";
        String lastId = employeeList.get(employeeList.size() - 1).getEmployeeID();
        return String.valueOf(Integer.parseInt(lastId) + 1);
    }

    /** Returns the integer value of the last employee ID, or 0 if the list is empty. */
    public int getLastEmployeeID() {
        if (employeeList.isEmpty()) return 0;
        return Integer.parseInt(employeeList.get(employeeList.size() - 1).getEmployeeID());
    }

    /**
     * Links attendance records to their corresponding employees.
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

    // -------------------------------------------------------------------------
    //  Conversion helpers (Employee → CSV row / Salary object)
    // -------------------------------------------------------------------------

    /**
     * Converts an {@link Employee} to the 13-column String array expected by Employee.csv.
     */
    private String[] toEmployeeRow(Employee e) {
        GovernmentDetails g = e.getGovernmentDetails();
        return new String[]{
            e.getEmployeeID(),
            e.getLastName(),
            e.getFirstName(),
            e.getBirthday(),
            e.getAddress(),
            e.getPhoneNumber(),
            g != null ? g.getSssNumber()        : "",
            g != null ? g.getPhilHealthNumber() : "",
            g != null ? g.getTinNumber()        : "",
            g != null ? g.getPagibigNumber()    : "",
            e.getStatus(),
            e.getPosition(),
            e.getImmediateSupervisor()
        };
    }

    /** Converts an {@link Employee}'s salary fields to a {@link Salary} data object. */
    private Salary toSalary(Employee e) {
        return new Salary(e.getBasicSalary(), e.getHourlyRate(), e.getGrossSemiMonthlyRate());
    }
}
