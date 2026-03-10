package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.PayrollLogReader;
import com.group33.cp2.motorph.model.CompensationDetails;
import com.group33.cp2.motorph.model.PayrollLog;
import com.group33.cp2.motorph.model.SalaryDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

// Service facade over PayrollLogReader.
// forms/ must call only this class — never PayrollLogReader directly.
public class PayrollLogService {

    private final PayrollLogReader reader;

    public PayrollLogService() {
        this.reader = new PayrollLogReader();
    }

    // Returns true if a payroll run is already on record for the given employee/month/year.
    // Returns false (and logs a warning) if the CSV cannot be read.
    public boolean isAlreadyLogged(String empNum, int month, int year) {
        try {
            return reader.isAlreadyLogged(empNum, month, year);
        } catch (IOException e) {
            System.err.println("PayrollLogService.isAlreadyLogged: " + e.getMessage());
            return false;
        }
    }

    // Persists a payroll run for the given employee/period using the financial data from SalaryDetails.
    public void savePayrollRun(String empNum, int month, int year, SalaryDetails details) throws IOException {
        PayrollLog log = new PayrollLog(
                empNum,
                month,
                year,
                details.grossSalary(),
                details.totalAllowances(),
                details.totalDeductions(),
                details.netSalary()
        );
        reader.savePayrollLog(log);
    }

    // Overload: persists a payroll run from a CompensationDetails produced by the Payroll pipeline.
    public void savePayrollRun(String empNum, int month, int year, CompensationDetails cd) throws IOException {
        PayrollLog log = new PayrollLog(
                empNum,
                month,
                year,
                cd.getGrossSalary(),
                cd.getAllowance().getTotal(),
                cd.getDeductions().getTotal(),
                cd.getNetSalary()
        );
        reader.savePayrollLog(log);
    }

    // Returns all persisted payroll log records; returns empty list if CSV cannot be read.
    public List<PayrollLog> getAllLogs() {
        try {
            return reader.getAllLogs();
        } catch (IOException e) {
            System.err.println("PayrollLogService.getAllLogs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Returns logs matching the given month (1–12) and year; returns empty list if none found.
    public List<PayrollLog> getLogsByPeriod(int month, int year) {
        List<PayrollLog> all = getAllLogs();
        List<PayrollLog> filtered = new java.util.ArrayList<>();
        for (PayrollLog log : all) {
            if (log.month() == month && log.year() == year) {
                filtered.add(log);
            }
        }
        return filtered;
    }
}
