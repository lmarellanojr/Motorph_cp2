package com.group33.cp2.motorph;

/**
 * Represents different types of allowances given to an employee.
 *
 * @author Group13
 * @version 1.0
 */
public class Allowance {

    private String employeeID;
    private double riceAllowance;
    private double phoneAllowance;
    private double clothingAllowance;

    /**
     * Constructs an Allowance object with specific amounts for each type of allowance.
     *
     * @param employeeID        Employee's unique ID
     * @param riceAllowance     Amount allocated for rice
     * @param phoneAllowance    Amount allocated for phone usage
     * @param clothingAllowance Amount allocated for clothing/uniform
     */
    public Allowance(String employeeID, double riceAllowance, double phoneAllowance, double clothingAllowance) {
        this.employeeID = employeeID;
        this.riceAllowance = riceAllowance;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }

    /**
     * Default constructor initializing all allowance values to zero.
     */
    public Allowance() {
        this.riceAllowance = 0.0;
        this.phoneAllowance = 0.0;
        this.clothingAllowance = 0.0;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public double getRiceAllowance() {
        return riceAllowance;
    }

    public void setRiceAllowance(double riceAllowance) {
        this.riceAllowance = riceAllowance;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        this.clothingAllowance = clothingAllowance;
    }

    /**
     * Calculates the total amount of all allowances.
     *
     * @return Sum of rice, phone, and clothing allowances
     */
    public double getTotal() {
        return riceAllowance + phoneAllowance + clothingAllowance;
    }

    @Override
    public String toString() {
        return String.format(
                "\n  Rice Allowance: %.2f\n  Phone Allowance: %.2f\n  Clothing Allowance: %.2f\n",
                riceAllowance, phoneAllowance, clothingAllowance
        );
    }
}
