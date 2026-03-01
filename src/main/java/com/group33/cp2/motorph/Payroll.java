package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a payroll record for a single employee covering a specific period.
 * Handles work-hour accumulation, gross salary computation, deduction and
 * allowance calculation, net salary derivation, and payslip generation.
 *
 * <p><strong>Encapsulation (BP6):</strong> {@code totalRegularHours} and
 * {@code totalOvertimeHours} are computed fields — their setters are {@code private}
 * so only the internal {@link #calculateWorkHours()} method can write to them.
 * External code must call {@link #getTotalRegularHours()} or
 * {@link #getTotalOvertimeHours()}, which trigger the calculation on demand.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class Payroll {

    private final String payrollID;
    private String employeeID;
    /** Start and end dates strictly define the payroll period (biweekly by design). */
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private double totalRegularHours;
    private double totalOvertimeHours;
    private CompensationDetails compensationDetails;
    private PayrollStatus status;
    private final Employee employee;

    /**
     * Constructs a Payroll record with a UUID-generated ID and PENDING status.
     *
     * @param employeeID  the employee's unique identifier
     * @param employee    the Employee object for this payroll record
     * @param periodStart the inclusive start date of the payroll period
     * @param periodEnd   the inclusive end date of the payroll period
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

    // ----- Getters and Setters -----

    public String getPayrollID() {
        return payrollID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public LocalDate getPeriodStart() {
        return periodStartDate;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStartDate = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEndDate;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEndDate = periodEnd;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }

    public CompensationDetails getCompensationDetails() {
        return this.compensationDetails;
    }

    public void setCompensationDetails(CompensationDetails compensationDetails) {
        this.compensationDetails = compensationDetails;
    }

    // ----- Business Logic -----

    /**
     * Returns {@code true} if the given date falls within the payroll period, inclusive.
     */
    private boolean isWithinPeriod(LocalDate date) {
        return !date.isBefore(this.periodStartDate) && !date.isAfter(this.periodEndDate);
    }

    /**
     * Scans the employee's attendance list and accumulates regular and overtime hours
     * for dates that fall within the payroll period.
     */
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

    /**
     * Returns the total regular hours, triggering a calculation if not yet computed.
     *
     * @return regular hours rounded to two decimal places
     */
    public double getTotalRegularHours() {
        if (this.totalRegularHours == 0) {
            calculateWorkHours();
        }
        return (Math.round(this.totalRegularHours * 100.0) / 100.0);
    }

    // BP6: private — totalRegularHours is a computed value set only by calculateWorkHours()
    private void setTotalRegularHours(double totalRegularHours) {
        this.totalRegularHours = totalRegularHours;
    }

    /**
     * Returns the total overtime hours, triggering a calculation if not yet computed.
     *
     * @return overtime hours rounded to two decimal places
     */
    public double getTotalOvertimeHours() {
        if (this.totalOvertimeHours == 0) {
            calculateWorkHours();
        }
        return (Math.round(this.totalOvertimeHours * 100.0) / 100.0);
    }

    // BP6: private — totalOvertimeHours is a computed value set only by calculateWorkHours()
    private void setTotalOvertimeHours(double totalOvertimeHours) {
        this.totalOvertimeHours = totalOvertimeHours;
    }

    /**
     * Computes the employee's regular pay (regular hours x hourly rate) and stores it.
     */
    public void calculateRegularPay() {
        double totalRegularPay = getTotalRegularHours() * this.employee.getHourlyRate();
        compensationDetails.setRegularPay(totalRegularPay);
    }

    /**
     * Computes the employee's overtime pay (overtime hours x hourly rate x 1.25) and stores it.
     */
    public void calculateOvertimePay() {
        double totalOvertimePay = this.getTotalOvertimeHours() * this.employee.getHourlyRate() * 1.25;
        compensationDetails.setOvertimePay(totalOvertimePay);
    }

    /**
     * Computes gross salary = regular pay + overtime pay.
     * Also triggers work-hour calculation internally.
     */
    public void calculateGrossSalary() {
        calculateWorkHours();
        calculateRegularPay();
        calculateOvertimePay();
        double grossSalary = compensationDetails.getRegularPay() + compensationDetails.getOvertimePay();
        compensationDetails.setGrossSalary(grossSalary);
    }

    /**
     * Calculates government-mandated deductions based on the employee's basic salary and period type.
     * Returns zero deductions if no regular hours were worked.
     *
     * @param periodType the payroll period type (MONTHLY or BIWEEKLY)
     */
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

    /**
     * Calculates prorated allowances based on the period type and whether the employee worked.
     * Divisors: WEEKLY = 4, BIWEEKLY = 2, MONTHLY = 1.
     *
     * @param periodType the payroll period type
     */
    public void calculateAllowances(PeriodType periodType) {
        Allowance allowance;
        if (getTotalRegularHours() != 0) {
            int divisor = 1;
            if (periodType == PeriodType.WEEKLY) {
                divisor = 4;
            } else if (periodType == PeriodType.BIWEEKLY) {
                divisor = 2;
            }
            double riceAllowance = this.employee.getAllowance().getRiceAllowance() / divisor;
            double phoneAllowance = this.employee.getAllowance().getPhoneAllowance() / divisor;
            double clothingAllowance = this.employee.getAllowance().getClothingAllowance() / divisor;
            allowance = new Allowance(employeeID, riceAllowance, phoneAllowance, clothingAllowance);
        } else {
            allowance = new Allowance();
        }
        compensationDetails.setAllowance(allowance);
    }

    /**
     * Computes the net salary using monthly deductions and allowances, then marks the
     * payroll as PROCESSED.
     * Net salary = gross salary - total deductions + total allowances.
     */
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

    /**
     * Marks this payroll record as PROCESSED.
     */
    public void process() {
        status = PayrollStatus.PROCESSED;
    }

    /**
     * Reverts this payroll record's status back to PENDING.
     */
    public void revertToPending() {
        status = PayrollStatus.PENDING;
    }

    /**
     * Generates and returns a {@link Payslip} summarising this payroll record.
     *
     * @return a new Payslip for the current period
     */
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
