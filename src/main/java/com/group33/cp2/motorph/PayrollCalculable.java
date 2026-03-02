package com.group33.cp2.motorph;

/**
 * Contract for payroll calculation behaviour in the MotorPH Payroll System.
 *
 * <p>Implemented by every concrete employee type ({@link RegularEmployee},
 * {@link ProbationaryEmployee}, and the department subclasses). Each implementation
 * applies the rules appropriate for that employee type — for example,
 * {@code RegularEmployee} applies overtime at 1.25× rate while
 * {@code ProbationaryEmployee} always returns 0.0 from
 * {@link #calculateOvertimePay(double)}.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface, not on concrete implementations, enabling polymorphic dispatch.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface PayrollCalculable {

    /**
     * Calculates gross salary using the employee's stored basic salary.
     *
     * @return gross salary amount
     */
    double calculateGrossSalary();

    /**
     * Calculates gross salary using a caller-supplied base salary.
     *
     * @param salary custom base salary; must be &gt; 0
     * @return gross salary amount
     */
    double calculateGrossSalary(double salary);

    /**
     * Calculates gross salary with an additional bonus amount.
     *
     * @param salary custom base salary; must be &gt; 0
     * @param bonus  non-negative bonus; probationary employees may ignore this parameter
     * @return gross salary plus applicable bonus
     */
    double calculateGrossSalary(double salary, double bonus);

    /**
     * Calculates total applicable government deductions.
     *
     * @return total deductions amount
     */
    double calculateDeductions();

    /**
     * Calculates net salary (gross minus applicable deductions).
     *
     * @return net salary amount
     */
    double calculateNetSalary();

    /**
     * Calculates overtime pay for the given number of hours worked beyond 8 hours.
     *
     * <p>Regular employees earn 1.25× their hourly rate per overtime hour.
     * Probationary employees are not eligible — this method returns 0.0.</p>
     *
     * @param overtimeHours hours worked beyond 8 in a day; must be &ge; 0
     * @return overtime pay amount
     */
    double calculateOvertimePay(double overtimeHours);
}
