package com.group33.cp2.motorph;

/**
 * Represents the government-related identification details for an employee,
 * including SSS, PhilHealth, TIN, and Pag-IBIG numbers.
 *
 * @author Group13
 * @version 1.0
 */
public class GovernmentDetails {

    private String employeeID;
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagibigNumber;

    /**
     * Constructs a GovernmentDetails object with all government ID numbers.
     *
     * @param employeeID       the unique ID of the employee
     * @param sssNumber        the Social Security System (SSS) number
     * @param philHealthNumber the PhilHealth number
     * @param tinNumber        the Tax Identification Number (TIN)
     * @param pagibigNumber    the Pag-IBIG Fund number
     */
    public GovernmentDetails(String employeeID, String sssNumber, String philHealthNumber,
                             String tinNumber, String pagibigNumber) {
        this.employeeID = employeeID;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagibigNumber = pagibigNumber;
    }

    public String getEmployeeID() {
        return this.employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber;
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getPagibigNumber() {
        return pagibigNumber;
    }

    public void setPagibigNumber(String pagibigNumber) {
        this.pagibigNumber = pagibigNumber;
    }
}
