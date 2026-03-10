package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.dao.TimeTrackerReader;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.PayrollCalculator;

import java.io.IOException;
import java.util.List;

// Admin department employee: full payroll treatment plus user management and system reports.
// Overtime at 1.25x; all four deductions apply.
public class Admin extends Employee implements PayrollCalculable, AdminOperations {

    // Lazily initialized to break the circular chain:
    // EmployeeService.reloadEmployees() creates Admin, so new EmployeeService() inside the
    // Admin constructor would recurse infinitely. Always access via getEmployeeService().
    private EmployeeService employeeService;

    // Returns the shared EmployeeService, creating it on first access.
    // Safe because this method is never called from the Admin constructor.
    private EmployeeService getEmployeeService() {
        if (employeeService == null) {
            employeeService = new EmployeeService();
        }
        return employeeService;
    }

    public Admin(String employeeID, String lastName, String firstName, String birthday,
                 String address, String phoneNumber, double basicSalary, double hourlyRate,
                 double grossSemiMonthlyRate, String status, String position,
                 String immediateSupervisor, Allowance allowance,
                 GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
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

    // Interface-contract overload with no UI callback.
    // For "create"/"update" actions that need to open a form, use manageUsers(int, String, UserManagementCallback).
    @Override
    public boolean manageUsers(int userId, String action) {
        return manageUsers(userId, action, null);
    }

    // Full overload: signals action intent through a callback so the domain model
    // has no javax.swing imports. AdminDashboard supplies the callback lambda.
    public boolean manageUsers(int userId, String action, UserManagementCallback callback) {
        if (action == null) return false;
        String empId = String.valueOf(userId);

        return switch (action.toLowerCase()) {
            case "create" -> {
                if (callback != null) callback.onCreateUser();
                yield true;
            }
            case "update" -> {
                Employee target = getEmployeeService().getEmployeeById(empId);
                if (target == null) yield false;
                if (callback != null) callback.onUpdateUser(empId);
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

    // Delegates delete to EmployeeService via the lazy accessor to avoid circular construction.
    @Override
    public boolean deleteEmployee(String empId) {
        return getEmployeeService().deleteEmployee(empId);
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
        List<Employee> employees = new EmployeeService().getAllEmployees();
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
        List<Employee> employees = new EmployeeService().getAllEmployees();
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
