package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a payslip record for an employee covering a specific payroll period.
 * Includes working hours, compensation details, and deductions.
 * The payslip ID is auto-generated as a UUID on construction.
 *
 * @author Group13
 * @version 1.0
 */
public class Payslip {

    private String payslipID;
    private String employeeID;
    private String payrollID;
    private String birthday;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private double totalRegularHours;
    private double totalOvertimeHours;
    private CompensationDetails compensationDetails;
    private LocalDate issueDate;

    /**
     * Constructs a Payslip with all required fields. The payslipID is auto-generated.
     *
     * @param employeeID          the employee's unique identifier
     * @param payrollID           the payroll period identifier
     * @param birthday            the employee's birthday as a string
     * @param periodStartDate     the start date of the payroll period
     * @param periodEndDate       the end date of the payroll period
     * @param totalRegularHours   total regular hours worked in the period
     * @param totalOvertimeHours  total overtime hours worked in the period
     * @param issueDate           the date this payslip was issued
     * @param compensationDetails the compensation breakdown (pay, deductions, allowances)
     */
    public Payslip(String employeeID, String payrollID, String birthday,
                   LocalDate periodStartDate, LocalDate periodEndDate,
                   double totalRegularHours, double totalOvertimeHours,
                   LocalDate issueDate, CompensationDetails compensationDetails) {
        this.payslipID = UUID.randomUUID().toString();
        this.employeeID = employeeID;
        this.payrollID = payrollID;
        this.birthday = birthday;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.totalRegularHours = totalRegularHours;
        this.totalOvertimeHours = totalOvertimeHours;
        this.compensationDetails = compensationDetails;
        this.issueDate = issueDate;
    }

    /**
     * Default no-arg constructor.
     */
    public Payslip() {
    }

    public String getPayslipID() {
        return payslipID;
    }

    public void setPayslipID(String payslipID) {
        this.payslipID = payslipID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getPayrollID() {
        return payrollID;
    }

    public void setPayrollID(String payrollID) {
        this.payrollID = payrollID;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public double getTotalRegularHours() {
        return totalRegularHours;
    }

    public void setTotalRegularHours(double totalRegularHours) {
        this.totalRegularHours = totalRegularHours;
    }

    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    public void setTotalOvertimeHours(double totalOvertimeHours) {
        this.totalOvertimeHours = totalOvertimeHours;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public CompensationDetails getCompensationDetails() {
        return compensationDetails;
    }

    public void setCompensationDetails(CompensationDetails compensationDetails) {
        this.compensationDetails = compensationDetails;
    }

    @Override
    public String toString() {
        return "\n  payslipID: '" + payslipID.substring(payslipID.length() - 5) + '\''
                + "\n  employeeID: '" + employeeID + '\''
                + "\n  birthday: " + birthday
                + "\n"
                + "\n  period: " + periodStartDate + " - " + periodEndDate
                + "\n  totalRegularHours: " + totalRegularHours
                + "\n  totalOvertimeHours: " + totalOvertimeHours
                + "\n  totalRegularPay: " + compensationDetails.getRegularPay()
                + "\n  totalOvertimePay: " + compensationDetails.getOvertimePay()
                + "\n  grossSalary: " + compensationDetails.getGrossSalary()
                + "\n"
                + "\n  deductions: " + compensationDetails.getDeductions().getTotal()
                + "\n  allowance: " + compensationDetails.getAllowance().getTotal()
                + "\n  netSalary: " + compensationDetails.getNetSalary()
                + "\n";
    }
}
