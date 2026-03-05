package com.group33.cp2.motorph;

import java.io.IOException;

/**
 * Represents an IT department employee in the MotorPH Payroll System.
 *
 * <p>IT employees receive full regular-employee payroll treatment
 * (all four deductions, overtime at 1.25x) and additionally implement
 * the system access-management operations defined by {@link ITOperations}.</p>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Inheritance</em> — extends {@link Employee}; inherits all personal/salary state.</li>
 *   <li><em>Polymorphism</em> — overrides abstract payroll methods with IT-specific rules.</li>
 *   <li><em>Abstraction</em> — implements {@link PayrollCalculable} and {@link ITOperations};
 *       password reset functionality is exposed through {@link #resetPassword(String)}.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.1
 */
public class IT extends Employee implements PayrollCalculable, ITOperations {

    /**
     * Constructs an {@code IT} department employee with all required fields.
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
    public IT(String employeeID, String lastName, String firstName, String birthday,
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
     * @return "IT Department"
     */
    public String getDepartment() { return "IT Department"; }

    // =========================================================================
    //  PayrollCalculable implementation
    // =========================================================================

    /**
     * Gross salary = basic salary + total allowance for IT employees.
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
     * IT employees are eligible for overtime at 1.25x their hourly rate.
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
     * Overtime pay at 1.25x the hourly rate for IT employees.
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

    // =========================================================================
    //  ITOperations implementation
    // =========================================================================

    /**
     * Manages system access for a given user.
     *
     * <p>When {@code accessLevel} is {@code "reset"}, delegates to
     * {@link #resetPassword(String)} to reset the user's login password.
     * All other access-level values are acknowledged but not yet implemented
     * (the interface contract is satisfied by returning {@code false}).</p>
     *
     * @param userId      the employee number whose access is being managed
     * @param accessLevel the access action to apply — {@code "reset"} triggers a
     *                    password reset; other values ("read", "write", "admin")
     *                    are reserved for future implementation
     * @return {@code true} if the action was successfully executed; {@code false} otherwise
     */
    @Override
    public boolean manageSystemAccess(int userId, String accessLevel) {
        if ("reset".equalsIgnoreCase(accessLevel)) {
            return resetPassword(String.valueOf(userId));
        }
        // "read", "write", "admin" — reserved for future access-control implementation
        return false;
    }

    /**
     * Resets the login password for the given employee.
     *
     * <p>Constructs a {@link PasswordResetService} and calls
     * {@link PasswordResetService#resetPassword(String, String, String)},
     * supplying this IT employee as the admin. On success, a temporary
     * BCrypt-hashed password is written to {@code Login.csv} and the matching
     * pending request in {@code Password_Reset_Requests.csv} is marked Approved.</p>
     *
     * <p><strong>OOP Pillar — Polymorphism:</strong> This method is reachable
     * through {@link #manageSystemAccess(int, String)} (interface dispatch) or
     * called directly from {@link com.group33.cp2.motorph.forms.ITDashboard}.</p>
     *
     * @param employeeId the employee number whose password will be reset
     * @return {@code true} if the reset succeeded; {@code false} if an error occurred
     */
    public boolean resetPassword(String employeeId) {
        try {
            PasswordResetService service = new PasswordResetService();
            service.resetPassword(employeeId, getFullName(), getEmployeeID());
            return true;
        } catch (PasswordResetException e) {
            System.err.println("IT.resetPassword: business rule violation — " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("IT.resetPassword: I/O error — " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "IT {" + super.toString() + "}";
    }
}
