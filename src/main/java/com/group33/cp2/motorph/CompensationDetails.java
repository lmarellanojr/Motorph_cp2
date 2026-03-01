package com.group33.cp2.motorph;

/**
 * Represents the full compensation details of an employee, including regular
 * pay, overtime, gross and net salary, as well as applicable allowances and deductions.
 *
 * <p><strong>Encapsulation (BP7):</strong> The partial constructor (employeeID, payrollID)
 * initializes {@code allowance} and {@code deductions} to zero-value defaults, ensuring
 * this object is always in a valid state regardless of which constructor is used.
 * This prevents NullPointerException when {@code toString()} or summary methods are
 * called before {@code calculateNetSalary()} completes.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class CompensationDetails {

    private String employeeID;
    private String payrollID;
    private double regularPay;
    private double overtimePay;
    private double grossSalary;
    private double netSalary;
    private Allowance allowance;
    private Deductions deductions;

    /**
     * Constructs a CompensationDetails object with employee ID and payroll ID only.
     * Allowance and deductions are initialized to zero-value defaults to ensure
     * this object is always in a valid, non-null state after construction.
     *
     * @param employeeID the unique identifier of the employee
     * @param payrollID  the ID of the payroll entry
     */
    public CompensationDetails(String employeeID, String payrollID) {
        this.employeeID = employeeID;
        this.payrollID = payrollID;
        // BP7: initialize to zero-value defaults — prevents NPE before calculation methods run
        this.allowance = new Allowance();
        this.deductions = new Deductions();
    }

    /**
     * Constructs a CompensationDetails object with all fields.
     *
     * @param employeeID  the unique identifier of the employee
     * @param regularPay  the amount earned from regular working hours
     * @param overtimePay the amount earned from overtime hours
     * @param grossSalary total salary before deductions
     * @param netSalary   total salary after deductions
     * @param allowance   the Allowance object containing benefits
     * @param deductions  the Deductions object containing deductions
     */
    public CompensationDetails(String employeeID, double regularPay, double overtimePay,
            double grossSalary, double netSalary,
            Allowance allowance, Deductions deductions) {
        this.employeeID = employeeID;
        this.regularPay = regularPay;
        this.overtimePay = overtimePay;
        this.grossSalary = grossSalary;
        this.netSalary = netSalary;
        this.allowance = allowance;
        this.deductions = deductions;
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

    public double getRegularPay() {
        return regularPay;
    }

    public void setRegularPay(double regularPay) {
        this.regularPay = regularPay;
    }

    public double getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(double overtimePay) {
        this.overtimePay = overtimePay;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public Allowance getAllowance() {
        return allowance;
    }

    public void setAllowance(Allowance allowance) {
        this.allowance = allowance;
    }

    public Deductions getDeductions() {
        return deductions;
    }

    public void setDeductions(Deductions deductions) {
        this.deductions = deductions;
    }
}
