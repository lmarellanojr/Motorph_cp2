package com.group33.cp2.motorph.model;

// Immutable record representing one completed payroll run for a single employee.
// Mirrors the columns of Payroll.csv: empNum, month, year, grossSalary,
// totalAllowance, totalDeductions, netMonthlySalary.
public record PayrollLog(
    String empNum,
    int month,
    int year,
    double grossSalary,
    double totalAllowance,
    double totalDeductions,
    double netSalary
) {}
