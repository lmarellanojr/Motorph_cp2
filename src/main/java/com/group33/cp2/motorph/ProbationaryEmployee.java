package com.group33.cp2.motorph;

/**
 * Represents a probationary employee in the MotorPH Payroll System.
 *
 * <p>Probationary employees:</p>
 * <ul>
 *   <li>Are <strong>not</strong> eligible for overtime pay — {@link #calculateOvertimePay(double)}
 *       always returns {@code 0.0}.</li>
 *   <li>Are subject to basic government deductions only:
 *       SSS, PhilHealth, and Pag-IBIG — <strong>no withholding tax</strong>.</li>
 *   <li>Do not receive bonuses — {@link #calculateGrossSalary(double, double)} ignores
 *       the bonus parameter and returns the base salary only.</li>
 * </ul>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Inheritance</em> — extends {@link Employee}, inheriting all shared state
 *       and overriding only what differs for this employee type.</li>
 *   <li><em>Polymorphism</em> — overrides abstract methods with Probationary-specific
 *       rules, enabling runtime dispatch via the {@link Employee} reference type.</li>
 *   <li><em>Abstraction</em> — implements {@link PayrollCalculable}, allowing service
 *       layers to treat all employee types uniformly.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.0
 */
public class ProbationaryEmployee extends Employee implements PayrollCalculable {

    /**
     * Constructs a {@code ProbationaryEmployee} with all required fields.
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
    public ProbationaryEmployee(String employeeID, String lastName, String firstName, String birthday,
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
     * No overtime component is added — probationary employees do not earn overtime.
     *
     * @return the basic salary
     */
    @Override
    public double calculateGrossSalary() {
        return getBasicSalary();
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
     * Returns the base salary only — the bonus parameter is ignored because
     * probationary employees do not receive bonuses.
     *
     * @param salary custom base salary; must be &gt; 0
     * @param bonus  ignored for probationary employees
     * @return {@code salary} (bonus not applied)
     * @throws IllegalArgumentException if {@code salary} &le; 0
     */
    @Override
    public double calculateGrossSalary(double salary, double bonus) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary; // bonus is intentionally not applied
    }

    /**
     * Probationary employees are not eligible for overtime pay.
     *
     * @param overtimeHours ignored
     * @return always {@code 0.0}
     */
    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return 0.0;
    }

    /**
     * Calculates basic deductions only: SSS + PhilHealth + Pag-IBIG.
     * Withholding tax is <strong>not</strong> applied for probationary employees.
     *
     * @return total basic deductions
     */
    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary());
    }

    /**
     * Calculates net salary as gross salary minus basic deductions.
     *
     * @return net salary
     */
    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public String toString() {
        return "ProbationaryEmployee {" + super.toString() + "}";
    }
}
