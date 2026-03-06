package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.dao.TimeTrackerReader;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.PayrollCalculator;

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
 * @author Group13
 * @version 2.1
 */
public class Admin extends Employee implements PayrollCalculable, AdminOperations {

    /**
     * Lazily-initialized EmployeeService — NOT set in the constructor to avoid infinite
     * recursion: EmployeeService.reloadEmployees() creates Admin objects, and an eager
     * new EmployeeService() call inside the Admin constructor would cycle back into
     * reloadEmployees().  The service is created on first use instead.
     */
    private EmployeeService employeeService;

    public Admin(String employeeID, String lastName, String firstName, String birthday,
                 String address, String phoneNumber, double basicSalary, double hourlyRate,
                 double grossSemiMonthlyRate, String status, String position,
                 String immediateSupervisor, Allowance allowance,
                 GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
        // employeeService is intentionally NOT initialized here — see field Javadoc
    }

    /** Returns the lazily-initialized EmployeeService, creating it on the first call. */
    private EmployeeService getEmployeeService() {
        if (employeeService == null) {
            employeeService = new EmployeeService();
        }
        return employeeService;
    }

    public String getDepartment() { return "Administration"; }

    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    @Override
    public double calculateGrossSalary(double salary) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

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

    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return overtimeHours * getHourlyRate() * 1.25;
    }

    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary())
             + PayrollCalculator.computeWithholdingTax(getBasicSalary());
    }

    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public boolean manageUsers(int userId, String action) {
        if (action == null) return false;
        String empId = String.valueOf(userId);

        return switch (action.toLowerCase()) {
            case "create" -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    com.group33.cp2.motorph.forms.NewEmployeeFrame frame =
                            new com.group33.cp2.motorph.forms.NewEmployeeFrame();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
                yield true;
            }
            case "update" -> {
                Employee target = getEmployeeService().getEmployeeById(empId);
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
                Employee target = getEmployeeService().getEmployeeById(empId);
                if (target == null) yield false;
                target.setStatus("Inactive");
                getEmployeeService().updateEmployee(target);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public Report generateSystemReport(String reportType) {
        if (reportType == null) return new Report("");

        return switch (reportType.toLowerCase()) {
            case "payroll"    -> generatePayrollReport();
            case "attendance" -> generateAttendanceReport();
            default           -> new Report("Unknown report type: " + reportType);
        };
    }

    private Report generatePayrollReport() {
        List<Employee> employees = getEmployeeService().getAllEmployees();
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

    private Report generateAttendanceReport() {
        List<Employee> employees = getEmployeeService().getAllEmployees();
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

    private double parseHoursWorked(String hoursStr) {
        try {
            String[] parts = hoursStr.split(":");
            if (parts.length < 2) return 0.0;
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]) / 60.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String toString() {
        return "Admin {" + super.toString() + "}";
    }
}
