package com.group33.cp2.motorph;

/**
 * Represents a regular (permanent) employee in the MotorPH Payroll System.
 *
 * <p>Regular employees:</p>
 * <ul>
 *   <li>Are eligible for overtime pay at <strong>1.25×</strong> their hourly rate.</li>
 *   <li>Are subject to the full set of government deductions:
 *       SSS, PhilHealth, Pag-IBIG, <em>and</em> withholding tax.</li>
 * </ul>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Inheritance</em> — extends {@link Employee}, reusing all shared state and
 *       non-abstract methods.</li>
 *   <li><em>Polymorphism</em> — overrides the three abstract methods declared in
 *       {@link Employee} with Regular-specific payroll rules.</li>
 *   <li><em>Abstraction</em> — implements {@link PayrollCalculable}, allowing callers
 *       to dispatch payroll calculations without knowing the concrete type.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.0
 */
public class RegularEmployee extends Employee implements PayrollCalculable {

    /**
     * Constructs a {@code RegularEmployee} with all required fields.
     * All validation is delegated to the {@link Employee} superclass constructor.
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
    public RegularEmployee(String employeeID, String lastName, String firstName, String birthday,
                           String address, String phoneNumber, double basicSalary, double hourlyRate,
                           double grossSemiMonthlyRate, String status, String position,
                           String immediateSupervisor, Allowance allowance,
                           GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    // =========================================================================
    //  PayrollCalculable — inherited abstract methods from Employee
    // =========================================================================

    /**
     * Returns the employee's stored monthly basic salary.
     * Overtime pay is added separately by the {@link Payroll} engine.
     *
     * @return the basic salary
     */
    @Override
    public double calculateGrossSalary() {
        return getBasicSalary();
    }

    /**
     * Returns the supplied salary amount unchanged.
     * Used when a caller provides a custom base salary for the calculation.
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
     * Returns salary plus the bonus amount.
     * Regular employees are eligible for bonuses.
     *
     * @param salary custom base salary; must be &gt; 0
     * @param bonus  additional bonus; must be &ge; 0
     * @return {@code salary + bonus}
     * @throws IllegalArgumentException if {@code salary} &le; 0 or {@code bonus} &lt; 0
     */
    @Override
    public double calculateGrossSalary(double salary, double bonus) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        if (bonus < 0) {
            throw new IllegalArgumentException("Bonus must be >= 0. Received: " + bonus);
        }
        return salary + bonus;
    }

    /**
     * Calculates overtime pay at 1.25× the hourly rate.
     *
     * @param overtimeHours hours worked beyond 8 in a day; must be &ge; 0
     * @return overtime pay (overtimeHours × hourlyRate × 1.25)
     * @throws IllegalArgumentException if {@code overtimeHours} is negative
     */
    @Override
    public double calculateOvertimePay(double overtimeHours) {
        if (overtimeHours < 0) {
            throw new IllegalArgumentException("Overtime hours must be >= 0. Received: " + overtimeHours);
        }
        return overtimeHours * getHourlyRate() * 1.25;
    }

    /**
     * Calculates full government deductions: SSS + PhilHealth + Pag-IBIG + withholding tax.
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

    /**
     * Calculates net salary as gross salary minus all applicable deductions.
     *
     * @return net salary
     */
    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public String toString() {
        return "RegularEmployee {" + super.toString() + "}";
    }
}
