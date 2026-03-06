package com.group33.cp2.motorph.model;

/**
 * Contract for payroll calculation behaviour in the MotorPH Payroll System.
 *
 * <p>Implemented by every concrete employee type ({@link RegularEmployee},
 * {@link ProbationaryEmployee}, and the department subclasses). Each implementation
 * applies the rules appropriate for that employee type.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface, not on concrete implementations, enabling polymorphic dispatch.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface PayrollCalculable {

    double calculateGrossSalary();
    double calculateGrossSalary(double salary);
    double calculateGrossSalary(double salary, double overtimeHours);
    double calculateDeductions();
    double calculateNetSalary();
    double calculateOvertimePay(double overtimeHours);
}
