package com.group33.cp2.motorph;

/**
 * Immutable value object holding a complete payroll computation result for one employee.
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> Callers receive this record from
 * {@link PayrollCalculatorService#getSalaryDetails(String)} without knowing how
 * CSV data was read or how each figure was derived.</p>
 *
 * @param grossSalary          basicSalary + totalAllowances
 * @param netSalary            grossSalary − totalDeductions
 * @param hourlyRate           employee's hourly rate from Salary.csv
 * @param riceSubsidy          rice allowance from Allowance.csv
 * @param phoneAllowance       phone allowance from Allowance.csv
 * @param clothingAllowance    clothing allowance from Allowance.csv
 * @param totalAllowances      sum of all three allowances
 * @param pagibigDeduction     computed Pag-IBIG contribution
 * @param philHealthDeduction  computed PhilHealth contribution
 * @param sssDeduction         computed SSS contribution
 * @param withholdingTax       computed BIR withholding tax
 * @param totalDeductions      sum of all four deductions
 */
public record SalaryDetails(
    double grossSalary, double netSalary, double hourlyRate,
    double riceSubsidy, double phoneAllowance, double clothingAllowance, double totalAllowances,
    double pagibigDeduction, double philHealthDeduction, double sssDeduction,
    double withholdingTax, double totalDeductions
) {}
