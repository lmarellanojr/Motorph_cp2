package com.group33.cp2.motorph;

import java.io.IOException;
import java.util.List;

/**
 * Represents an Admin department employee in the MotorPH Payroll System.
 *
 * <p>Admin employees receive full regular-employee payroll treatment
 * (all four deductions, overtime at 1.25x) and additionally implement
 * the user-management and report-generation operations defined by
 * {@link AdminOperations}.</p>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Inheritance</em> — extends {@link Employee}; inherits all personal/salary state.</li>
 *   <li><em>Polymorphism</em> — overrides abstract payroll methods with Admin-specific rules.</li>
 *   <li><em>Abstraction</em> — implements {@link PayrollCalculable} and {@link AdminOperations};
 *       both interfaces are fully implemented.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.1
 */
public class Admin extends Employee implements PayrollCalculable, AdminOperations {

    /** Shared employee service used by manageUsers and generateSystemReport. */
    private final EmployeeService employeeService;

    /**
     * Constructs an {@code Admin} department employee with all required fields.
     *
     * @param employeeID           unique employee identifier
     * @param lastName             last name
     * @param firstName            first name
     * @param birthday             birthday string (MM/dd/yyyy)
     * @param address              home address
     * @param phoneNumber          contact phone number
     * @param basicSalary          monthly basic salary
     * @param hourlyRate           hourly pay rate
     * @param grossSemiMonthlyRate gross semi-monthly salary
     * @param status               employment status
     * @param position             job title / position
     * @param immediateSupervisor  immediate supervisor's name
     * @param allowance            allowance object (rice, phone, clothing)
     * @param governmentDetails    government IDs (SSS, PhilHealth, TIN, Pag-IBIG)
     */
    public Admin(String employeeID, String lastName, String firstName, String birthday,
                 String address, String phoneNumber, double basicSalary, double hourlyRate,
                 double grossSemiMonthlyRate, String status, String position,
                 String immediateSupervisor, Allowance allowance,
                 GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
        this.employeeService = new EmployeeService();
    }

    /**
     * Returns the department name for this employee type.
     *
     * @return "Administration"
     */
    public String getDepartment() { return "Administration"; }

    // =========================================================================
    //  PayrollCalculable implementation
    // =========================================================================

    /**
     * Gross salary = basic salary + total allowance for Admin employees.
     *
     * @return gross salary
     */
    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    /**
     * Returns the supplied salary amount unchanged.
     *
     * @param salary custom base salary; must be &gt; 0
     * @return {@code salary}
     * @throws IllegalArgumentException if {@code salary} is not positive
     */
    @Override
    public double calculateGrossSalary(double salary) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

    /**
     * Calculates gross salary including overtime pay for the given overtime hours.
     * Admin employees are eligible for overtime at 1.25x their hourly rate.
     *
     * @param salary        custom base salary; must be &gt; 0
     * @param overtimeHours hours worked beyond 8 in a day; must be &ge; 0
     * @return {@code salary + (overtimeHours * hourlyRate * 1.25)}
     * @throws IllegalArgumentException if {@code salary} &le; 0 or {@code overtimeHours} &lt; 0
     */
    @Override
    public double calculateGrossSalary(double salary, double overtimeHours) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        if (overtimeHours < 0) {
            throw new IllegalArgumentException("Overtime hours must be >= 0. Received: " + overtimeHours);
        }
        return salary + (overtimeHours * getHourlyRate() * 1.25);
    }

    /**
     * Overtime pay at 1.25x the hourly rate for Admin employees.
     *
     * @param overtimeHours hours worked beyond 8 in a day
     * @return overtime pay amount
     */
    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return overtimeHours * getHourlyRate() * 1.25;
    }

    /**
     * Full deductions: SSS + PhilHealth + Pag-IBIG + withholding tax.
     *
     * @return total deductions
     */
    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary())
             + PayrollCalculator.computeWithholdingTax(getBasicSalary());
    }

    /** @return gross salary minus all applicable deductions */
    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    // =========================================================================
    //  AdminOperations implementation
    // =========================================================================

    /**
     * Creates, updates, or deactivates a user account.
     *
     * <p>Actions:</p>
     * <ul>
     *   <li>{@code "create"} — opens {@code NewEmployeeFrame} (GUI action; returns {@code true}).</li>
     *   <li>{@code "update"} — opens {@code UpdateEmployeeFrame} for the given user ID.</li>
     *   <li>{@code "deactivate"} — sets the employee's status to {@code "Inactive"} via
     *       {@link EmployeeService#updateEmployee(Employee)}.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> The {@code create} and {@code update} actions open GUI frames.
     * Those frames manage their own lifecycle. The return value is {@code true} whenever
     * the requested action was dispatched without error.</p>
     *
     * @param userId the employee number to act on (used as String ID internally)
     * @param action "create", "update", or "deactivate" (case-insensitive)
     * @return {@code true} if the action was recognised and dispatched; {@code false} otherwise
     */
    @Override
    public boolean manageUsers(int userId, String action) {
        if (action == null) return false;
        String empId = String.valueOf(userId);

        return switch (action.toLowerCase()) {
            case "create" -> {
                // Swing action: open NewEmployeeFrame on the Event Dispatch Thread
                javax.swing.SwingUtilities.invokeLater(() -> {
                    com.group33.cp2.motorph.forms.NewEmployeeFrame frame =
                            new com.group33.cp2.motorph.forms.NewEmployeeFrame();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
                yield true;
            }
            case "update" -> {
                Employee target = employeeService.getEmployeeById(empId);
                if (target == null) yield false;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    com.group33.cp2.motorph.forms.UpdateEmployeeFrame frame =
                            new com.group33.cp2.motorph.forms.UpdateEmployeeFrame(empId);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
                yield true;
            }
            case "deactivate" -> {
                Employee target = employeeService.getEmployeeById(empId);
                if (target == null) yield false;
                target.setStatus("Inactive");
                employeeService.updateEmployee(target);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * Generates a system-wide report of the given type and returns a populated {@link Report}.
     *
     * <p>Supported report types:</p>
     * <ul>
     *   <li>{@code "payroll"} — iterates all employees, computing gross salary, total
     *       deductions, and net salary. Returns a formatted text report.</li>
     *   <li>{@code "attendance"} — iterates all employees, reading each employee's time-log
     *       entries from {@code TimeTracker.csv} and summarising hours worked.</li>
     * </ul>
     *
     * @param reportType "payroll" or "attendance" (case-insensitive)
     * @return a {@link Report} containing the formatted report content;
     *         if {@code reportType} is unrecognised, an empty {@link Report} is returned
     */
    @Override
    public Report generateSystemReport(String reportType) {
        if (reportType == null) return new Report("");

        return switch (reportType.toLowerCase()) {
            case "payroll"    -> generatePayrollReport();
            case "attendance" -> generateAttendanceReport();
            default           -> new Report("Unknown report type: " + reportType);
        };
    }

    // =========================================================================
    //  Private report builders
    // =========================================================================

    /**
     * Builds a payroll summary report: one row per employee with gross, deductions, and net.
     */
    private Report generatePayrollReport() {
        List<Employee> employees = employeeService.getAllEmployees();
        StringBuilder sb = new StringBuilder();
        sb.append("=== MotorPH Payroll Report ===\n");
        sb.append(String.format("%-10s %-25s %12s %12s %12s%n",
                "Emp ID", "Full Name", "Gross", "Deductions", "Net Pay"));
        sb.append("-".repeat(75)).append("\n");

        double totalGross  = 0;
        double totalDeduct = 0;
        double totalNet    = 0;

        for (Employee emp : employees) {
            double gross  = emp.calculateGrossSalary();
            double deduct = emp.calculateDeductions();
            double net    = emp.calculateNetSalary();
            totalGross  += gross;
            totalDeduct += deduct;
            totalNet    += net;

            sb.append(String.format("%-10s %-25s %12.2f %12.2f %12.2f%n",
                    emp.getEmployeeID(),
                    truncate(emp.getFullName(), 25),
                    gross, deduct, net));
        }

        sb.append("-".repeat(75)).append("\n");
        sb.append(String.format("%-10s %-25s %12.2f %12.2f %12.2f%n",
                "", "TOTALS", totalGross, totalDeduct, totalNet));
        sb.append(String.format("%nTotal Employees: %d%n", employees.size()));

        return new Report(sb.toString());
    }

    /**
     * Builds an attendance summary report: total hours per employee from TimeTracker.csv.
     */
    private Report generateAttendanceReport() {
        List<Employee> employees = employeeService.getAllEmployees();
        StringBuilder sb = new StringBuilder();
        sb.append("=== MotorPH Attendance Report ===\n");
        sb.append(String.format("%-10s %-25s %10s %10s%n",
                "Emp ID", "Full Name", "Sessions", "Total Hrs"));
        sb.append("-".repeat(60)).append("\n");

        for (Employee emp : employees) {
            try {
                List<String[]> logs = TimeTrackerReader.getTimeLogs(emp.getEmployeeID());
                double totalHours = 0;
                int sessions = 0;
                for (String[] row : logs) {
                    if (row.length > 4 && !row[4].isBlank() && !row[4].equalsIgnoreCase("Error")) {
                        totalHours += parseHoursWorked(row[4]);
                        sessions++;
                    }
                }
                sb.append(String.format("%-10s %-25s %10d %10.2f%n",
                        emp.getEmployeeID(), truncate(emp.getFullName(), 25),
                        sessions, totalHours));
            } catch (IOException e) {
                sb.append(String.format("%-10s %-25s  (error reading logs)%n",
                        emp.getEmployeeID(), truncate(emp.getFullName(), 25)));
            }
        }

        return new Report(sb.toString());
    }

    /**
     * Parses hours in "H:mm" format to a decimal double.
     * Returns 0.0 on any parse error.
     */
    private double parseHoursWorked(String hoursStr) {
        try {
            String[] parts = hoursStr.split(":");
            if (parts.length < 2) return 0.0;
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]) / 60.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Truncates a string to {@code maxLen} characters, appending "..." if needed. */
    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String toString() {
        return "Admin {" + super.toString() + "}";
    }
}
