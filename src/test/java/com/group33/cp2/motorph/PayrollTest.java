package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Standalone test class that creates sample data and exercises the payroll pipeline.
 * Not a unit test framework class — run the {@code main} method directly to verify output.
 *
 * @author Group13
 * @version 1.0
 */
public class PayrollTest {

    public static EmployeeService employeeService = new EmployeeService();

    /**
     * Entry point for the payroll smoke test. Creates a sample employee with
     * attendance records, runs payroll calculation, and prints the resulting payslip.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Create sample attendance records
            Attendance att1 = new Attendance("E001", LocalDate.of(2024, 4, 1),
                    LocalTime.of(8, 0), LocalTime.of(17, 0));
            Attendance att2 = new Attendance("E001", LocalDate.of(2024, 4, 2),
                    LocalTime.of(8, 0), LocalTime.of(17, 0));
            Attendance att3 = new Attendance("E001", LocalDate.of(2024, 4, 3),
                    LocalTime.of(8, 0), LocalTime.of(17, 0));
            Attendance att4 = new Attendance("E001", LocalDate.of(2024, 4, 4),
                    LocalTime.of(8, 0), LocalTime.of(17, 0));
            Attendance att5 = new Attendance("E001", LocalDate.of(2024, 4, 5),
                    LocalTime.of(8, 0), LocalTime.of(19, 0)); // 2h overtime

            List<Attendance> attendanceList = Arrays.asList(att1, att2, att3, att4, att5);
            for (Attendance att : attendanceList) {
                System.out.println(att);
            }

            // Create sample login and role
            Login login = new Login("E001", "johndoe", "securedpassword", Role.EMPLOYEE);

            // Create sample allowance
            Allowance allowance = new Allowance("E001", 1500, 1000, 500);

            // Create sample government details
            GovernmentDetails governmentDetails = new GovernmentDetails(
                    "E001", "1223", "12454", "1245789", "125965");

            // Create empty payslip list
            List<Payslip> payslips = Arrays.asList();

            // Create employee
            Employee emp = new Employee(
                    employeeService.generateEmployeeID(),
                    "Doe",
                    "John",
                    LocalDate.of(1995, 5, 15).toString(),
                    "Tennessee",
                    "0911-123-4567",
                    30000,
                    187.5,
                    75,
                    "Regular",
                    "Assistant",
                    "Boss",
                    allowance,
                    governmentDetails);
            emp.setAttendanceList(attendanceList);
            emp.setPayslips(payslips);
            emp.setLogin(login);
            employeeService.addEmployee(emp);

            // Create payroll instance and compute
            Payroll payroll = new Payroll("E001", emp,
                    LocalDate.of(2024, 4, 1),
                    LocalDate.of(2024, 4, 14));
            payroll.calculateNetSalary();

            Payslip payslip = payroll.generatePayslip();
            System.err.println("payslip : " + payslip);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
