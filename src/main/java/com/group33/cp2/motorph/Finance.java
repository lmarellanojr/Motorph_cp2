package com.group33.cp2.motorph;

/**
 * Finance department employee. Handles payroll management operations.
 * Payroll calculation behaviour will be overridden in the Polymorphism milestone.
 */
public class Finance extends Employee implements PayrollCalculable {

    public Finance(String employeeID, String lastName, String firstName, String birthday,
                   String address, String phoneNumber, double basicSalary, double hourlyRate,
                   double grossSemiMonthlyRate, String status, String position,
                   String immediateSupervisor, Allowance allowance,
                   GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    public String getDepartment() {
        return "Finance Department";
    }

    // --- PayrollCalculable ---

    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    @Override
    public double calculateGrossSalary(double salary) {
        return salary;
    }

    @Override
    public double calculateGrossSalary(double salary, double bonus) {
        return salary + bonus;
    }

    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return overtimeHours * getHourlyRate() * 1.25;
    }

    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary())
             + PayrollCalculator.computeWithholdingTax(getBasicSalary());
    }

    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public String toString() {
        return "Finance {" + super.toString() + "}";
    }
}
