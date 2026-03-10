package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.service.PayrollCalculator;

/**
 * Represents a Finance department employee in the MotorPH Payroll System.
 *
 * <p>Finance employees receive full regular-employee payroll treatment
 * (all four deductions, overtime at 1.25x).</p>
 *
 * @author Group 33
 * @version 2.0
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

    public String getDepartment() { return "Finance Department"; }

    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    @Override
    public double calculateGrossSalary(double salary) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

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
