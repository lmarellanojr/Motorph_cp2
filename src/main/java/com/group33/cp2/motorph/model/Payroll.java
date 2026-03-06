package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.service.PayrollCalculator;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Holds payroll data for one employee over a date range. Calculates hours, pay, deductions, and net salary.
 */
public class Payroll {

    private final String payrollID;
    private String employeeID;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private double totalRegularHours;
    private double totalOvertimeHours;
    private CompensationDetails compensationDetails;
    private PayrollStatus status;
    private final Employee employee;

    /**
     * Creates a payroll record. Status starts as PENDING.
     */
    public Payroll(String employeeID, Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        this.payrollID = UUID.randomUUID().toString();
        this.employeeID = employeeID;
        this.employee = employee;
        this.periodStartDate = periodStart;
        this.periodEndDate = periodEnd;
        this.status = PayrollStatus.PENDING;
        this.compensationDetails = new CompensationDetails(employeeID, this.payrollID);
    }

    public String getPayrollID() { return payrollID; }

    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public LocalDate getPeriodStart() { return periodStartDate; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStartDate = periodStart; }

    public LocalDate getPeriodEnd() { return periodEndDate; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEndDate = periodEnd; }

    public PayrollStatus getStatus() { return status; }
    public void setStatus(PayrollStatus status) { this.status = status; }

    public CompensationDetails getCompensationDetails() { return this.compensationDetails; }
    public void setCompensationDetails(CompensationDetails compensationDetails) {
        this.compensationDetails = compensationDetails;
    }

    // checks if a date falls within the payroll period
    private boolean isWithinPeriod(LocalDate date) {
        return !date.isBefore(this.periodStartDate) && !date.isAfter(this.periodEndDate);
    }

    // adds up regular and overtime hours from attendance records within the period
    protected void calculateWorkHours() {
        double regularHours = 0;
        double overtimeHours = 0;

        for (Attendance attendance : this.employee.getAttendanceList()) {
            if (isWithinPeriod(attendance.getDate())) {
                regularHours += attendance.getRoundedRegularHours();
                overtimeHours += attendance.getRoundedOvertimeHours();
            }
        }

        totalRegularHours = regularHours;
        totalOvertimeHours = overtimeHours;
    }

    // triggers calculation if not yet done
    public double getTotalRegularHours() {
        if (this.totalRegularHours == 0) {
            calculateWorkHours();
        }
        return (Math.round(this.totalRegularHours * 100.0) / 100.0);
    }

    // private: only calculateWorkHours() should set this
    private void setTotalRegularHours(double totalRegularHours) {
        this.totalRegularHours = totalRegularHours;
    }

    // triggers calculation if not yet done
    public double getTotalOvertimeHours() {
        if (this.totalOvertimeHours == 0) {
            calculateWorkHours();
        }
        return (Math.round(this.totalOvertimeHours * 100.0) / 100.0);
    }

    // private: only calculateWorkHours() should set this
    private void setTotalOvertimeHours(double totalOvertimeHours) {
        this.totalOvertimeHours = totalOvertimeHours;
    }

    public void calculateRegularPay() {
        double totalRegularPay = getTotalRegularHours() * this.employee.getHourlyRate();
        compensationDetails.setRegularPay(totalRegularPay);
    }

    public void calculateOvertimePay() {
        double totalOvertimePay = this.getTotalOvertimeHours() * this.employee.getHourlyRate() * 1.25;
        compensationDetails.setOvertimePay(totalOvertimePay);
    }

    public void calculateGrossSalary() {
        calculateWorkHours();
        calculateRegularPay();
        calculateOvertimePay();
        double grossSalary = compensationDetails.getRegularPay() + compensationDetails.getOvertimePay();
        compensationDetails.setGrossSalary(grossSalary);
    }

    // zero deductions if no hours were worked
    public void calculateDeductions(PeriodType periodType) {
        Deductions deductions;
        if (getTotalRegularHours() > 0) {
            if (periodType.equals(PeriodType.MONTHLY)) {
                deductions = PayrollCalculator.getDeductionsMonthly(employeeID, employee.getBasicSalary(), periodStartDate);
            } else {
                deductions = PayrollCalculator.getDeductionsBiWeekly(employeeID, employee.getBasicSalary(), periodStartDate);
            }
        } else {
            deductions = new Deductions();
        }
        compensationDetails.setDeductions(deductions);
    }

    // pro-rates allowances: WEEKLY /4, BIWEEKLY /2, MONTHLY /1
    public void calculateAllowances(PeriodType periodType) {
        Allowance allowance;
        if (getTotalRegularHours() != 0) {
            int divisor = 1;
            if (periodType == PeriodType.WEEKLY) {
                divisor = 4;
            } else if (periodType == PeriodType.BIWEEKLY) {
                divisor = 2;
            }
            double riceAllowance = this.employee.getAllowanceDetails().getRiceAllowance() / divisor;
            double phoneAllowance = this.employee.getAllowanceDetails().getPhoneAllowance() / divisor;
            double clothingAllowance = this.employee.getAllowanceDetails().getClothingAllowance() / divisor;
            allowance = new Allowance(employeeID, riceAllowance, phoneAllowance, clothingAllowance);
        } else {
            allowance = new Allowance();
        }
        compensationDetails.setAllowance(allowance);
    }

    // net = gross - deductions + allowances; marks as PROCESSED
    public void calculateNetSalary() {
        this.calculateGrossSalary();
        this.calculateDeductions(PeriodType.MONTHLY);
        this.calculateAllowances(PeriodType.MONTHLY);
        double netSalary = (compensationDetails.getGrossSalary()
                - compensationDetails.getDeductions().getTotal())
                + compensationDetails.getAllowance().getTotal();
        compensationDetails.setNetSalary(netSalary);
        setStatus(PayrollStatus.PROCESSED);
    }

    public void process() {
        status = PayrollStatus.PROCESSED;
    }

    public void revertToPending() {
        status = PayrollStatus.PENDING;
    }

    public Payslip generatePayslip() {
        return new Payslip(
                this.employeeID,
                this.payrollID,
                this.employee.getBirthday(),
                this.periodStartDate,
                this.periodEndDate,
                this.getTotalRegularHours(),
                this.getTotalOvertimeHours(),
                LocalDate.now(),
                compensationDetails
        );
    }

    @Override
    public String toString() {
        return "Payroll {"
                + "\n  payrollID='" + payrollID + '\''
                + ",\n  employeeID='" + employeeID + '\''
                + ",\n  periodStartDate=" + periodStartDate
                + ",\n  periodEndDate=" + periodEndDate
                + ",\n  totalRegularHours=" + totalRegularHours
                + ",\n  totalOvertimeHours=" + totalOvertimeHours
                + ",\n  totalRegularPay=" + compensationDetails.getRegularPay()
                + ",\n  totalOvertimePay=" + compensationDetails.getOvertimePay()
                + ",\n  grossSalary=" + compensationDetails.getGrossSalary()
                + ",\n  totalAllowance=" + compensationDetails.getAllowance().getTotal()
                + ",\n  totalDeductions=" + compensationDetails.getDeductions().getTotal()
                + ",\n  netSalary=" + compensationDetails.getNetSalary()
                + ",\n  status=" + status
                + "\n}";
    }
}
