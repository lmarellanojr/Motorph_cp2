package com.group33.cp2.motorph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee with personal details, salary information, allowances,
 * government-related details, attendance records, and payslips.
 *
 * @author Group13
 * @version 1.0
 */
public class Employee {

    private String employeeID;
    private String lastName;
    private String firstName;
    private String birthday;
    private String address;
    private String phoneNumber;
    private double basicSalary;
    private double hourlyRate;
    private double grossSemiMonthlyRate;
    private String status;
    private String position;
    private String immediateSupervisor;

    private Login login;
    private Allowance allowance;
    private GovernmentDetails governmentDetails;
    private List<Attendance> attendanceList;
    private List<Payslip> payslips;

    /**
     * Constructs an Employee with all required fields.
     *
     * @param employeeID           unique employee identifier
     * @param lastName             employee last name
     * @param firstName            employee first name
     * @param birthday             employee birthday string (MM/dd/yyyy)
     * @param address              home address
     * @param phoneNumber          contact phone number
     * @param basicSalary          monthly basic salary
     * @param hourlyRate           hourly pay rate
     * @param grossSemiMonthlyRate gross semi-monthly salary
     * @param status               employment status (e.g., Regular, Probationary)
     * @param position             job title / position
     * @param immediateSupervisor  immediate supervisor name
     * @param allowance            Allowance object for the employee
     * @param governmentDetails    GovernmentDetails object holding government IDs
     */
    public Employee(String employeeID, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, double basicSalary, double hourlyRate, double grossSemiMonthlyRate,
                    String status, String position, String immediateSupervisor,
                    Allowance allowance, GovernmentDetails governmentDetails) {
        this.employeeID = employeeID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.basicSalary = basicSalary;
        this.hourlyRate = hourlyRate;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.status = status;
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;
        this.allowance = allowance;
        this.governmentDetails = governmentDetails;
        this.attendanceList = new ArrayList<>();
        this.payslips = new ArrayList<>();
    }

    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }

    public Allowance getAllowance() { return allowance; }
    public void setAllowance(Allowance allowance) { this.allowance = allowance; }

    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }
    public void setGovernmentDetails(GovernmentDetails governmentDetails) { this.governmentDetails = governmentDetails; }

    public Login getLogin() { return login; }
    public void setLogin(Login login) { this.login = login; }

    public List<Attendance> getAttendanceList() { return attendanceList; }
    public void setAttendanceList(List<Attendance> attendanceList) { this.attendanceList = attendanceList; }

    public List<Payslip> getPayslips() { return payslips; }
    public void setPayslips(List<Payslip> payslips) { this.payslips = payslips; }

    @Override
    public String toString() {
        return "employeeID='" + employeeID + '\''
                + "\n lastName='" + lastName + '\''
                + "\n firstName='" + firstName + '\''
                + "\n birthday='" + birthday + '\''
                + "\n basicSalary=" + basicSalary
                + "\n hourlyRate=" + hourlyRate
                + "\n login=" + (login != null ? login.toString() : "null")
                + "\n allowance=" + (allowance != null ? allowance.toString() : "null")
                + "\n attendanceList=" + (attendanceList != null ? attendanceList.toString() : "null")
                + "\n payslips=" + (payslips != null ? payslips.toString() : "null") + "\n";
    }
}
