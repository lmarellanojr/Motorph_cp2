package com.group33.cp2.motorph;

/**
 * Represents government-mandated payroll deductions for an employee,
 * including SSS, PhilHealth, Pag-IBIG, and withholding tax.
 *
 * @author Group13
 * @version 1.0
 */
public class Deductions {

    private String employeeID;
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double tax;

    /**
     * Constructs a Deductions object with all deduction amounts.
     *
     * @param employeeID the employee's unique ID
     * @param sss        SSS contribution amount
     * @param philHealth PhilHealth contribution amount
     * @param pagIbig    Pag-IBIG contribution amount
     * @param tax        withholding tax amount
     */
    public Deductions(String employeeID, double sss, double philHealth, double pagIbig, double tax) {
        this.employeeID = employeeID;
        this.sss = sss;
        this.philHealth = philHealth;
        this.pagIbig = pagIbig;
        this.tax = tax;
    }

    /**
     * Default constructor initializing all deductions to zero.
     */
    public Deductions() {
        this.sss = 0.0;
        this.philHealth = 0.0;
        this.pagIbig = 0.0;
        this.tax = 0.0;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public double getSss() {
        return sss;
    }

    public void setSssRate(double sss) {
        this.sss = sss;
    }

    public double getPhilHealth() {
        return philHealth;
    }

    public void setPhilHealth(double philHealth) {
        this.philHealth = philHealth;
    }

    public double getPagIbig() {
        return pagIbig;
    }

    public void setPagIbigRate(double pagIbig) {
        this.pagIbig = pagIbig;
    }

    public double getTax() {
        return tax;
    }

    public void setTaxRate(double tax) {
        this.tax = tax;
    }

    /**
     * Calculates the total of all deductions.
     *
     * @return sum of SSS, PhilHealth, Pag-IBIG, and tax
     */
    public double getTotal() {
        return sss + philHealth + pagIbig + tax;
    }

    @Override
    public String toString() {
        return "\n  SSS : " + sss
                + ",\n  PhilHealth : " + philHealth
                + ",\n  Pag-IBIG : " + pagIbig
                + ",\n  Tax : " + tax
                + "\n";
    }
}
