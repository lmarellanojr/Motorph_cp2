package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.service.PayrollCalculator;

/**
 * Represents a regular (permanent) employee in the MotorPH Payroll System.
 *
 * <p>Regular employees are eligible for overtime at 1.25x and subject to full deductions:
 * SSS, PhilHealth, Pag-IBIG, and withholding tax.</p>
 *
 * @author Group13
 * @version 2.0
 */
public class RegularEmployee extends Employee implements PayrollCalculable {

    public RegularEmployee(String employeeID, String lastName, String firstName, String birthday,
                           String address, String phoneNumber, double basicSalary, double hourlyRate,
                           double grossSemiMonthlyRate, String status, String position,
                           String immediateSupervisor, Allowance allowance,
                           GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    @Override
    public double calculateGrossSalary() {
        return getBasicSalary();
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
        if (overtimeHours < 0) {
            throw new IllegalArgumentException("Overtime hours must be >= 0. Received: " + overtimeHours);
        }
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
        return "RegularEmployee {" + super.toString() + "}";
    }
}
