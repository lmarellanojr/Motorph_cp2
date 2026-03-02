package com.group33.cp2.motorph;

/**
 * Represents a Finance department employee in the MotorPH Payroll System.
 *
 * <p>Finance employees receive full regular-employee payroll treatment
 * (all four deductions, overtime at 1.25×).</p>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Inheritance</em> — extends {@link Employee}; inherits all personal/salary state.</li>
 *   <li><em>Polymorphism</em> — overrides abstract payroll methods with Finance-specific rules.</li>
 *   <li><em>Abstraction</em> — implements {@link PayrollCalculable}.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.0
 */
public class Finance extends Employee implements PayrollCalculable {

    /**
     * Constructs a {@code Finance} department employee with all required fields.
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
    public Finance(String employeeID, String lastName, String firstName, String birthday,
                   String address, String phoneNumber, double basicSalary, double hourlyRate,
                   double grossSemiMonthlyRate, String status, String position,
                   String immediateSupervisor, Allowance allowance,
                   GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    /**
     * Returns the department name for this employee type.
     *
     * @return "Finance Department"
     */
    public String getDepartment() { return "Finance Department"; }

    // =========================================================================
    //  PayrollCalculable implementation
    // =========================================================================

    /**
     * Gross salary = basic salary + total allowance for Finance employees.
     *
     * @return gross salary
     */
    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    /** @return {@code salary} unchanged */
    @Override
    public double calculateGrossSalary(double salary) {
        return salary;
    }

    /** @return {@code salary + bonus} */
    @Override
    public double calculateGrossSalary(double salary, double bonus) {
        return salary + bonus;
    }

    /**
     * Overtime pay at 1.25× the hourly rate for Finance employees.
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

    @Override
    public String toString() {
        return "Finance {" + super.toString() + "}";
    }
}
