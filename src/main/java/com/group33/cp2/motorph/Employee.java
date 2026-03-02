package com.group33.cp2.motorph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all employee types. Defines shared personal and salary data.
 * Subclasses must implement payroll calculation methods.
 */
public abstract class Employee {

    // salary validation limits
    private static final double MAX_BASIC_SALARY = 500_000.0;
    private static final double MAX_HOURLY_RATE = 5_000.0;

    // accepted status values from both design spec and CSV data
    private static final List<String> VALID_STATUSES = List.of(
            "Regular", "Probationary", "Active", "Inactive", "On Leave", "Terminated"
    );

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
     * Creates an Employee with all fields. Validated fields throw IllegalArgumentException if invalid.
     */
    public Employee(String employeeID, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, double basicSalary, double hourlyRate, double grossSemiMonthlyRate,
                    String status, String position, String immediateSupervisor,
                    Allowance allowance, GovernmentDetails governmentDetails) {
        setEmployeeID(employeeID);
        setLastName(lastName);
        setFirstName(firstName);
        this.birthday = birthday;
        setAddress(address);
        setPhoneNumber(phoneNumber);
        setBasicSalary(basicSalary);
        setHourlyRate(hourlyRate);
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        setStatus(status);
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;
        this.allowance = allowance;
        this.governmentDetails = governmentDetails;
        this.attendanceList = new ArrayList<>();
        this.payslips = new ArrayList<>();
    }

    // getters and setters

    public String getEmployeeID() { return employeeID; }

    public void setEmployeeID(String employeeID) {
        if (employeeID == null || employeeID.isBlank()) {
            throw new IllegalArgumentException("Employee ID must not be null or blank.");
        }
        this.employeeID = employeeID;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank.");
        }
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    public String getFullName() { return firstName + " " + lastName; }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank.");
        }
        this.firstName = firstName;
    }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }

    public void setAddress(String address) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null.");
        }
        if (address.length() > 200) {
            throw new IllegalArgumentException("Address must not exceed 200 characters.");
        }
        this.address = address;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank.");
        }
        this.phoneNumber = phoneNumber;
    }

    public double getBasicSalary() { return basicSalary; }

    // salary must be > 0 and <= 500,000
    public void setBasicSalary(double basicSalary) {
        if (basicSalary <= 0 || basicSalary > MAX_BASIC_SALARY) {
            throw new IllegalArgumentException(
                    "Basic salary must be > 0 and <= " + MAX_BASIC_SALARY + ". Received: " + basicSalary);
        }
        this.basicSalary = basicSalary;
    }

    public double getHourlyRate() { return hourlyRate; }

    // rate must be > 0 and <= 5,000
    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0 || hourlyRate > MAX_HOURLY_RATE) {
            throw new IllegalArgumentException(
                    "Hourly rate must be > 0 and <= " + MAX_HOURLY_RATE + ". Received: " + hourlyRate);
        }
        this.hourlyRate = hourlyRate;
    }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    public String getStatus() { return status; }

    // status must be one of the values in VALID_STATUSES
    public void setStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Status must be one of " + VALID_STATUSES + ". Received: '" + status + "'");
        }
        this.status = status;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) {
        this.immediateSupervisor = immediateSupervisor;
    }

    // returns the total allowance as a double (rice + phone + clothing)
    public double getAllowance() { return allowance.getTotal(); }

    // returns the full Allowance object for detailed breakdown
    public Allowance getAllowanceDetails() { return allowance; }

    // sets a scalar total allowance; stores it as rice subsidy, clears phone and clothing
    public void setAllowance(double totalAllowance) {
        if (totalAllowance < 0) {
            throw new IllegalArgumentException("Allowance cannot be negative. Received: " + totalAllowance);
        }
        this.allowance = new Allowance(employeeID, totalAllowance, 0, 0);
    }

    public void setAllowance(Allowance allowance) { this.allowance = allowance; }

    // alias for getEmployeeID() for spec compatibility
    public String getEmployeeNumber() { return employeeID; }

    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }
    public void setGovernmentDetails(GovernmentDetails governmentDetails) {
        this.governmentDetails = governmentDetails;
    }

    public Login getLogin() { return login; }
    public void setLogin(Login login) { this.login = login; }

    // returns a read-only view; use addAttendance() to add records
    public List<Attendance> getAttendanceList() {
        return Collections.unmodifiableList(attendanceList);
    }

    // adds one attendance record; throws if null
    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record must not be null.");
        }
        this.attendanceList.add(attendance);
    }

    // replaces the whole list; used for bulk loading only
    public void setAttendanceList(List<Attendance> attendanceList) {
        if (attendanceList == null) {
            throw new IllegalArgumentException("Attendance list must not be null.");
        }
        this.attendanceList = new ArrayList<>(attendanceList);
    }

    // returns a read-only view; use addPayslip() to add records
    public List<Payslip> getPayslips() {
        return Collections.unmodifiableList(payslips);
    }

    // adds one payslip; throws if null
    public void addPayslip(Payslip payslip) {
        if (payslip == null) {
            throw new IllegalArgumentException("Payslip must not be null.");
        }
        this.payslips.add(payslip);
    }

    // replaces the whole list; used for bulk loading only
    public void setPayslips(List<Payslip> payslips) {
        if (payslips == null) {
            throw new IllegalArgumentException("Payslip list must not be null.");
        }
        this.payslips = new ArrayList<>(payslips);
    }

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
                + "\n attendanceList=" + attendanceList.toString()
                + "\n payslips=" + payslips.toString() + "\n";
    }

    // subclasses define how gross salary is calculated based on employee type
    public abstract double calculateGrossSalary();

    // subclasses define which deductions apply (Regular: all four; Probationary: no tax)
    public abstract double calculateDeductions();

    // subclasses define net salary as gross minus applicable deductions
    public abstract double calculateNetSalary();
}
