package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.AllowanceDetailsReader;
import com.group33.cp2.motorph.dao.EmployeeDetailsReader;
import com.group33.cp2.motorph.dao.SalaryDetailsReader;
import com.group33.cp2.motorph.model.Admin;
import com.group33.cp2.motorph.model.Allowance;
import com.group33.cp2.motorph.model.Attendance;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.Finance;
import com.group33.cp2.motorph.model.GovernmentDetails;
import com.group33.cp2.motorph.model.HR;
import com.group33.cp2.motorph.model.IT;
import com.group33.cp2.motorph.model.ProbationaryEmployee;
import com.group33.cp2.motorph.model.RegularEmployee;
import com.group33.cp2.motorph.model.Salary;

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
            // Guard against null allowance: use a zero-value Allowance if none is set
            Allowance allowance = employee.getAllowanceDetails();
            if (allowance == null) {
                allowance = new Allowance(employee.getEmployeeID(), 0, 0, 0);
            }
            allowanceReader.addAllowance(employee.getEmployeeID(), allowance);
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
            // Guard against null allowance: use a zero-value Allowance if none is set
            Allowance allowance = employee.getAllowanceDetails();
            if (allowance == null) {
                allowance = new Allowance(employee.getEmployeeID(), 0, 0, 0);
            }
            allowanceReader.updateAllowance(employee.getEmployeeID(), allowance);
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

                // Look up the role from Login.csv so we can instantiate the correct
                // concrete subtype (HR, Finance, IT, Admin, RegularEmployee, ProbationaryEmployee).
                // EmployeeDetailsReader.getLoginDataByEmpNum returns null if not in Login.csv;
                // in that case the employee falls through to RegularEmployee / ProbationaryEmployee.
                String[] loginRow = employeeReader.getLoginDataByEmpNum(empId);
                String roleName = (loginRow != null && loginRow.length > 2)
                        ? loginRow[2].trim().toUpperCase()
                        : "";

                Employee employee;

                // Common constructor args shared by all paths
                String ln = row[1].trim(), fn = row[2].trim(), bday = row[3].trim();
                String addr = row[4].trim(), phone = row[5].trim();
                String pos = row[11].trim(), sup = row[12].trim();
                double basic = salary.getBasicSalary(), hr = salary.getHourlyRate();
                double gross = salary.getGrossSMRate();

                employee = switch (roleName) {
                    case "HR" -> new HR(
                            empId, ln, fn, bday, addr, phone,
                            basic, hr, gross, status, pos, sup, allowance, govDetails);
                    case "FINANCE" -> new Finance(
                            empId, ln, fn, bday, addr, phone,
                            basic, hr, gross, status, pos, sup, allowance, govDetails);
                    case "IT" -> new IT(
                            empId, ln, fn, bday, addr, phone,
                            basic, hr, gross, status, pos, sup, allowance, govDetails);
                    case "ADMIN" -> new Admin(
                            empId, ln, fn, bday, addr, phone,
                            basic, hr, gross, status, pos, sup, allowance, govDetails);
                    default -> {
                        if ("Probationary".equalsIgnoreCase(status)) {
                            yield new ProbationaryEmployee(
                                    empId, ln, fn, bday, addr, phone,
                                    basic, hr, gross, status, pos, sup, allowance, govDetails);
                        } else {
                            yield new RegularEmployee(
                                    empId, ln, fn, bday, addr, phone,
                                    basic, hr, gross, status, pos, sup, allowance, govDetails);
                        }
                    }
                };

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
