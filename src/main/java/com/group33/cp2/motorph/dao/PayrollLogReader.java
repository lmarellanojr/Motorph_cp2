package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.PayrollLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Reads and writes payroll run records to/from Payroll.csv.
// CSV path is relative to the working directory (must run from Motorph_cp2/).
// Columns: empNum, month, year, grossSalary, totalAllowance, totalDeductions, netMonthlySalary
public class PayrollLogReader {

    private static final String CSV_PATH = "src/main/resources/data/Payroll.csv";
    private static final String CSV_HEADER = "empNum,month,year,grossSalary,totalAllowance,totalDeductions,netMonthlySalary";

    // Column indices — must match CSV_HEADER order exactly
    private static final int COL_EMP_NUM         = 0;
    private static final int COL_MONTH           = 1;
    private static final int COL_YEAR            = 2;
    private static final int COL_GROSS_SALARY    = 3;
    private static final int COL_TOTAL_ALLOWANCE = 4;
    private static final int COL_TOTAL_DEDUCTIONS = 5;
    private static final int COL_NET_SALARY      = 6;

    private static final int EXPECTED_COLUMNS = 7;

    // Returns true if a payroll run already exists for the given employee/month/year combination.
    public boolean isAlreadyLogged(String empNum, int month, int year) throws IOException {
        for (PayrollLog log : getAllLogs()) {
            if (log.empNum().equals(empNum)
                    && log.month() == month
                    && log.year() == year) {
                return true;
            }
        }
        return false;
    }

    // Appends a new payroll run record to Payroll.csv.
    // Opens in append mode so prior records are not overwritten.
    public void savePayrollLog(PayrollLog log) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_PATH, true))) {
            writer.printf("%s,%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                    log.empNum(),
                    log.month(),
                    log.year(),
                    log.grossSalary(),
                    log.totalAllowance(),
                    log.totalDeductions(),
                    log.netSalary());
        }
    }

    // Returns all payroll log records from Payroll.csv; skips the header and malformed rows.
    public List<PayrollLog> getAllLogs() throws IOException {
        List<PayrollLog> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < EXPECTED_COLUMNS) continue;
                try {
                    String empNum        = parts[COL_EMP_NUM].trim();
                    int month            = Integer.parseInt(parts[COL_MONTH].trim());
                    int year             = Integer.parseInt(parts[COL_YEAR].trim());
                    double grossSalary   = Double.parseDouble(parts[COL_GROSS_SALARY].trim());
                    double totalAllow    = Double.parseDouble(parts[COL_TOTAL_ALLOWANCE].trim());
                    double totalDeduct   = Double.parseDouble(parts[COL_TOTAL_DEDUCTIONS].trim());
                    double netSalary     = Double.parseDouble(parts[COL_NET_SALARY].trim());
                    logs.add(new PayrollLog(empNum, month, year, grossSalary, totalAllow, totalDeduct, netSalary));
                } catch (NumberFormatException e) {
                    System.err.println("PayrollLogReader: skipping malformed row: " + line);
                }
            }
        }
        return Collections.unmodifiableList(logs);
    }
}
