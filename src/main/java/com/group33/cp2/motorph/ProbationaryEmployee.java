package com.group33.cp2.motorph;

/**
 * Probationary employee. Not eligible for overtime pay.
 * Deductions include SSS, PhilHealth, and Pag-IBIG only (no withholding tax).
 */
public class ProbationaryEmployee extends Employee implements PayrollCalculable {

    public ProbationaryEmployee(String employeeID, String lastName, String firstName, String birthday,
                                String address, String phoneNumber, double basicSalary, double hourlyRate,
                                double grossSemiMonthlyRate, String status, String position,
                                String immediateSupervisor, Allowance allowance,
                                GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    // returns the stored monthly basic salary; no overtime for probationary employees
    @Override
    public double calculateGrossSalary() {
        return getBasicSalary();
    }

    // returns the given salary; overtime is not applied for probationary employees
    @Override
    public double calculateGrossSalary(double salary) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

    // bonus is not applied; probationary employees do not receive bonuses
    @Override
    public double calculateGrossSalary(double salary, double bonus) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

    // probationary employees are not eligible for overtime pay
    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return 0.0;
    }

    // basic deductions only: SSS + PhilHealth + Pag-IBIG (no withholding tax)
    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary());
    }

    // net = gross salary minus basic deductions
    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public String toString() {
        return "ProbationaryEmployee {" + super.toString() + "}";
    }
}
