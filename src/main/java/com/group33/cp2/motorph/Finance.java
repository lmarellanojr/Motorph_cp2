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
     * Finance employees are eligible for overtime at 1.25× their hourly rate.
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
