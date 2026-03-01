package com.group33.cp2.motorph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an employee with personal details, salary information, allowances,
 * government-related details, attendance records, and payslips.
 *
 * <p><strong>Encapsulation (BP2 + BP3):</strong> All fields are {@code private}.
 * Numeric fields (basicSalary, hourlyRate) and string identity fields are validated
 * in their setters per the Method Dictionary. The internal attendance and payslip
 * lists are never returned directly — callers receive an unmodifiable view and must
 * use {@link #addAttendance(Attendance)} or {@link #addPayslip(Payslip)} to append
 * individual records.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class Employee {

    // BP3: Salary validation bounds per Method Dictionary
    private static final double MAX_BASIC_SALARY = 500_000.0;
    private static final double MAX_HOURLY_RATE = 5_000.0;

    // BP3: Valid employment status values (Option B: design spec + actual CSV values)
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
     * Constructs an Employee with all required fields.
     * Validation is applied via setters for all constrained fields.
     *
     * @param employeeID           unique employee identifier (must not be blank)
     * @param lastName             employee last name (must not be blank)
     * @param firstName            employee first name (must not be blank)
     * @param birthday             employee birthday string (MM/dd/yyyy)
     * @param address              home address (must not be null; max 200 chars)
     * @param phoneNumber          contact phone number (must not be blank)
     * @param basicSalary          monthly basic salary (must be > 0 and <= 500,000)
     * @param hourlyRate           hourly pay rate (must be > 0 and <= 5,000)
     * @param grossSemiMonthlyRate gross semi-monthly salary
     * @param status               employment status (must be a recognized value)
     * @param position             job title / position
     * @param immediateSupervisor  immediate supervisor name
     * @param allowance            Allowance object for the employee
     * @param governmentDetails    GovernmentDetails object holding government IDs
     * @throws IllegalArgumentException if any validated field fails its constraint
     */
    public Employee(String employeeID, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, double basicSalary, double hourlyRate, double grossSemiMonthlyRate,
                    String status, String position, String immediateSupervisor,
                    Allowance allowance, GovernmentDetails governmentDetails) {
        // Delegate to setters so validation runs consistently in constructor and mutation paths
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

    // -------------------------------------------------------------------------
    // Getters and validated Setters
    // -------------------------------------------------------------------------

    public String getEmployeeID() { return employeeID; }

    /**
     * Sets the employee's unique identifier.
     *
     * @param employeeID the identifier (must not be null or blank)
     * @throws IllegalArgumentException if the value is null or blank
     */
    public void setEmployeeID(String employeeID) {
        if (employeeID == null || employeeID.isBlank()) {
            throw new IllegalArgumentException("Employee ID must not be null or blank.");
        }
        this.employeeID = employeeID;
    }

    public String getLastName() { return lastName; }

    /**
     * Sets the employee's last name.
     *
     * @param lastName the last name (must not be null or blank)
     * @throws IllegalArgumentException if the value is null or blank
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank.");
        }
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    /**
     * Sets the employee's first name.
     *
     * @param firstName the first name (must not be null or blank)
     * @throws IllegalArgumentException if the value is null or blank
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank.");
        }
        this.firstName = firstName;
    }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }

    /**
     * Sets the employee's address.
     *
     * @param address the address (must not be null; max 200 characters)
     * @throws IllegalArgumentException if null or longer than 200 characters
     */
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

    /**
     * Sets the employee's contact phone number.
     * Format is not enforced (Option C) to preserve compatibility with
     * existing CSV data. Value must not be null or blank.
     *
     * @param phoneNumber the contact number (must not be null or blank)
     * @throws IllegalArgumentException if the value is null or blank
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank.");
        }
        this.phoneNumber = phoneNumber;
    }

    public double getBasicSalary() { return basicSalary; }

    /**
     * Sets the employee's basic monthly salary.
     *
     * @param basicSalary the salary amount (must be > 0 and <= 500,000)
     * @throws IllegalArgumentException if outside the valid range
     */
    public void setBasicSalary(double basicSalary) {
        if (basicSalary <= 0 || basicSalary > MAX_BASIC_SALARY) {
            throw new IllegalArgumentException(
                    "Basic salary must be > 0 and <= " + MAX_BASIC_SALARY + ". Received: " + basicSalary);
        }
        this.basicSalary = basicSalary;
    }

    public double getHourlyRate() { return hourlyRate; }

    /**
     * Sets the employee's hourly pay rate.
     *
     * @param hourlyRate the hourly rate (must be > 0 and <= 5,000)
     * @throws IllegalArgumentException if outside the valid range
     */
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

    /**
     * Sets the employee's employment status.
     * Valid values (Option B): "Regular", "Probationary", "Active",
     * "Inactive", "On Leave", "Terminated".
     *
     * @param status the employment status (must be one of the recognized values)
     * @throws IllegalArgumentException if the value is not in the valid status list
     */
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

    public Allowance getAllowance() { return allowance; }
    public void setAllowance(Allowance allowance) { this.allowance = allowance; }

    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }
    public void setGovernmentDetails(GovernmentDetails governmentDetails) {
        this.governmentDetails = governmentDetails;
    }

    public Login getLogin() { return login; }
    public void setLogin(Login login) { this.login = login; }

    // -------------------------------------------------------------------------
    // BP2: Controlled collection access — unmodifiable views + mutator methods
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of this employee's attendance records.
     * Use {@link #addAttendance(Attendance)} to append individual records.
     *
     * @return an unmodifiable list of attendance records
     */
    public List<Attendance> getAttendanceList() {
        return Collections.unmodifiableList(attendanceList);
    }

    /**
     * Adds a single attendance record to this employee's list.
     *
     * @param attendance the attendance record to add (must not be null)
     * @throws IllegalArgumentException if the attendance is null
     */
    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record must not be null.");
        }
        this.attendanceList.add(attendance);
    }

    /**
     * Replaces the entire attendance list. Intended for bulk data-loading only
     * (e.g., test setup or full list replacement). Use {@link #addAttendance(Attendance)}
     * for individual record additions.
     *
     * @param attendanceList the replacement list (must not be null)
     * @throws IllegalArgumentException if the list is null
     */
    public void setAttendanceList(List<Attendance> attendanceList) {
        if (attendanceList == null) {
            throw new IllegalArgumentException("Attendance list must not be null.");
        }
        this.attendanceList = new ArrayList<>(attendanceList);
    }

    /**
     * Returns an unmodifiable view of this employee's payslip records.
     * Use {@link #addPayslip(Payslip)} to append individual payslips.
     *
     * @return an unmodifiable list of payslips
     */
    public List<Payslip> getPayslips() {
        return Collections.unmodifiableList(payslips);
    }

    /**
     * Adds a single payslip to this employee's list.
     *
     * @param payslip the payslip to add (must not be null)
     * @throws IllegalArgumentException if the payslip is null
     */
    public void addPayslip(Payslip payslip) {
        if (payslip == null) {
            throw new IllegalArgumentException("Payslip must not be null.");
        }
        this.payslips.add(payslip);
    }

    /**
     * Replaces the entire payslip list. Intended for bulk data-loading only.
     *
     * @param payslips the replacement list (must not be null)
     * @throws IllegalArgumentException if the list is null
     */
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
}
