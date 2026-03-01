package com.group33.cp2.motorph;

/**
 * Represents different types of allowances given to an employee.
 *
 * <p><strong>Encapsulation (BP3):</strong> All allowance amount setters validate
 * that the supplied value is non-negative (allowances cannot be deductions).</p>
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

    /**
     * Sets the rice allowance amount.
     *
     * @param riceAllowance the amount (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    public void setRiceAllowance(double riceAllowance) {
        if (riceAllowance < 0) {
            throw new IllegalArgumentException("Rice allowance must be >= 0. Received: " + riceAllowance);
        }
        this.riceAllowance = riceAllowance;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    /**
     * Sets the phone allowance amount.
     *
     * @param phoneAllowance the amount (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    public void setPhoneAllowance(double phoneAllowance) {
        if (phoneAllowance < 0) {
            throw new IllegalArgumentException("Phone allowance must be >= 0. Received: " + phoneAllowance);
        }
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    /**
     * Sets the clothing allowance amount.
     *
     * @param clothingAllowance the amount (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    public void setClothingAllowance(double clothingAllowance) {
        if (clothingAllowance < 0) {
            throw new IllegalArgumentException("Clothing allowance must be >= 0. Received: " + clothingAllowance);
        }
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
