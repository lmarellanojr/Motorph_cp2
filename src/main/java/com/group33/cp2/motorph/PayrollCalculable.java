package com.group33.cp2.motorph;

/**
 * Contract for payroll calculations on an employee.
 * Implemented by RegularEmployee and ProbationaryEmployee.
 */
public interface PayrollCalculable {

    // calculates gross salary using the employee's stored basicSalary
    double calculateGrossSalary();

    // calculates gross salary using a custom base salary
    double calculateGrossSalary(double salary);

    // calculates gross salary with an additional bonus amount (bonus >= 0)
    double calculateGrossSalary(double salary, double bonus);

    // calculates total applicable government deductions
    double calculateDeductions();

    // calculates net salary: gross minus total deductions
    double calculateNetSalary();

    // calculates overtime pay for the given hours worked beyond 8 hours
    double calculateOvertimePay(double overtimeHours);
}
