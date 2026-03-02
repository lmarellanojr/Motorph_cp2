package com.group33.cp2.motorph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all employee types in the MotorPH Payroll System.
 *
 * <p>Defines shared personal, salary, and identification data common to every
 * employee regardless of employment status. Concrete subclasses ({@link RegularEmployee}
 * and {@link ProbationaryEmployee}) must implement the three abstract payroll methods
 * to supply type-specific calculation behaviour.</p>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Encapsulation</em> — all fields are {@code private}; validated setters
 *       guard against illegal values; {@code attendanceList} and {@code payslips}
 *       are exposed as unmodifiable views.</li>
 *   <li><em>Inheritance</em> — this class is the root of the employee hierarchy;
 *       subclasses inherit all shared state and override the abstract methods.</li>
 *   <li><em>Abstraction</em> — {@code calculateGrossSalary()}, {@code calculateDeductions()},
 *       and {@code calculateNetSalary()} are declared abstract, hiding implementation
 *       details in each concrete type.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.0
 */
public abstract class Employee {

    // ---- Validation constants ----
    /** Maximum accepted basic salary (exclusive upper bound guard). */
    private static final double MAX_BASIC_SALARY = 500_000.0;

    /** Maximum accepted hourly rate (exclusive upper bound guard). */
    private static final double MAX_HOURLY_RATE = 5_000.0;

    /**
     * Accepted employment-status values.
     * The CSV uses "Regular" and "Probationary"; the others appear in HR flows.
     */
    private static final List<String> VALID_STATUSES = List.of(
            "Regular", "Probationary", "Active", "Inactive", "On Leave", "Terminated"
    );

    // ---- Personal / identity fields ----
    private String employeeID;
    private String lastName;
    private String firstName;
    private String birthday;
    private String address;
    private String phoneNumber;

    // ---- Compensation fields ----
    private double basicSalary;
    private double hourlyRate;
    private double grossSemiMonthlyRate;

    // ---- HR classification fields ----
    private String status;
    private String position;
    private String immediateSupervisor;

    // ---- Composed objects ----
    private Login login;
    private Allowance allowance;
    private GovernmentDetails governmentDetails;

    // ---- Collections (mutable internally, unmodifiable externally) ----
    private List<Attendance> attendanceList;
    private List<Payslip> payslips;

    // =========================================================================
    //  Constructor
    // =========================================================================

    /**
     * Constructs an {@code Employee} with all required fields.
     *
     * <p>Validated parameters throw {@link IllegalArgumentException} immediately
     * if they violate their constraints.</p>
     *
     * @param employeeID           unique employee identifier (non-blank)
     * @param lastName             employee last name (non-blank)
     * @param firstName            employee first name (non-blank)
     * @param birthday             birthday as a string (e.g. MM/dd/yyyy)
     * @param address              home address (non-null, max 200 chars)
     * @param phoneNumber          contact phone number (non-blank)
     * @param basicSalary          monthly basic salary (0 &lt; value &le; 500,000)
     * @param hourlyRate           hourly pay rate (0 &lt; value &le; 5,000)
     * @param grossSemiMonthlyRate gross semi-monthly salary
     * @param status               employment status — must be one of
     *                             "Regular", "Probationary", "Active",
     *                             "Inactive", "On Leave", or "Terminated"
     * @param position             job title / position
     * @param immediateSupervisor  name of the immediate supervisor
     * @param allowance            {@link Allowance} object (rice, phone, clothing)
     * @param governmentDetails    {@link GovernmentDetails} holding SSS, PhilHealth,
     *                             TIN, and Pag-IBIG numbers
     */
    public Employee(String employeeID, String lastName, String firstName, String birthday,
                    String address, String phoneNumber, double basicSalary, double hourlyRate,
                    double grossSemiMonthlyRate, String status, String position,
                    String immediateSupervisor, Allowance allowance,
                    GovernmentDetails governmentDetails) {
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

    // =========================================================================
    //  Abstract payroll methods — implemented differently by each subclass
    // =========================================================================

    /**
     * Calculates the gross salary for this employee type.
     *
     * <ul>
     *   <li>{@link RegularEmployee} — basic salary (overtime added separately by Payroll)</li>
     *   <li>{@link ProbationaryEmployee} — basic salary only (no overtime component)</li>
     * </ul>
     *
     * @return gross salary amount
     */
    public abstract double calculateGrossSalary();

    /**
     * Calculates the total applicable government deductions for this employee type.
     *
     * <ul>
     *   <li>{@link RegularEmployee} — SSS + PhilHealth + Pag-IBIG + withholding tax</li>
     *   <li>{@link ProbationaryEmployee} — SSS + PhilHealth + Pag-IBIG (no withholding tax)</li>
     * </ul>
     *
     * @return total deductions amount
     */
    public abstract double calculateDeductions();

    /**
     * Calculates net salary as gross salary minus total applicable deductions.
     *
     * @return net salary amount
     */
    public abstract double calculateNetSalary();

    // =========================================================================
    //  Getters and validated setters
    // =========================================================================

    /** @return the employee's unique identifier */
    public String getEmployeeID() { return employeeID; }

    /**
     * Sets the employee ID.
     *
     * @param employeeID must not be null or blank
     * @throws IllegalArgumentException if value is null or blank
     */
    public void setEmployeeID(String employeeID) {
        if (employeeID == null || employeeID.isBlank()) {
            throw new IllegalArgumentException("Employee ID must not be null or blank.");
        }
        this.employeeID = employeeID;
    }

    /** @return the employee's last name */
    public String getLastName() { return lastName; }

    /**
     * Sets the last name.
     *
     * @param lastName must not be null or blank
     * @throws IllegalArgumentException if value is null or blank
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank.");
        }
        this.lastName = lastName;
    }

    /** @return the employee's first name */
    public String getFirstName() { return firstName; }

    /**
     * Sets the first name.
     *
     * @param firstName must not be null or blank
     * @throws IllegalArgumentException if value is null or blank
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank.");
        }
        this.firstName = firstName;
    }

    /** @return the full name as "firstName lastName" */
    public String getFullName() { return firstName + " " + lastName; }

    /** @return the birthday string (e.g. MM/dd/yyyy) */
    public String getBirthday() { return birthday; }

    /** @param birthday birthday string */
    public void setBirthday(String birthday) { this.birthday = birthday; }

    /** @return the home address */
    public String getAddress() { return address; }

    /**
     * Sets the home address.
     *
     * @param address must not be null; max 200 characters
     * @throws IllegalArgumentException if null or exceeds 200 characters
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

    /** @return the phone number */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber must not be null or blank
     * @throws IllegalArgumentException if value is null or blank
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank.");
        }
        this.phoneNumber = phoneNumber;
    }

    /** @return the monthly basic salary */
    public double getBasicSalary() { return basicSalary; }

    /**
     * Sets the monthly basic salary.
     *
     * @param basicSalary must be &gt; 0 and &le; 500,000
     * @throws IllegalArgumentException if out of range
     */
    public void setBasicSalary(double basicSalary) {
        if (basicSalary <= 0 || basicSalary > MAX_BASIC_SALARY) {
            throw new IllegalArgumentException(
                    "Basic salary must be > 0 and <= " + MAX_BASIC_SALARY + ". Received: " + basicSalary);
        }
        this.basicSalary = basicSalary;
    }

    /** @return the hourly pay rate */
    public double getHourlyRate() { return hourlyRate; }

    /**
     * Sets the hourly pay rate.
     *
     * @param hourlyRate must be &gt; 0 and &le; 5,000
     * @throws IllegalArgumentException if out of range
     */
    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0 || hourlyRate > MAX_HOURLY_RATE) {
            throw new IllegalArgumentException(
                    "Hourly rate must be > 0 and <= " + MAX_HOURLY_RATE + ". Received: " + hourlyRate);
        }
        this.hourlyRate = hourlyRate;
    }

    /** @return the gross semi-monthly salary */
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }

    /** @param grossSemiMonthlyRate gross semi-monthly salary */
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    /** @return the employment status */
    public String getStatus() { return status; }

    /**
     * Sets the employment status.
     *
     * @param status must be one of "Regular", "Probationary", "Active",
     *               "Inactive", "On Leave", or "Terminated"
     * @throws IllegalArgumentException if not in the whitelist
     */
    public void setStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Status must be one of " + VALID_STATUSES + ". Received: '" + status + "'");
        }
        this.status = status;
    }

    /** @return the job position / title */
    public String getPosition() { return position; }

    /** @param position job position / title */
    public void setPosition(String position) { this.position = position; }

    /** @return the immediate supervisor's name */
    public String getImmediateSupervisor() { return immediateSupervisor; }

    /** @param immediateSupervisor immediate supervisor's name */
    public void setImmediateSupervisor(String immediateSupervisor) {
        this.immediateSupervisor = immediateSupervisor;
    }

    /**
     * Returns the total allowance amount (rice + phone + clothing).
     *
     * @return total allowance as a double
     */
    public double getAllowance() { return allowance != null ? allowance.getTotal() : 0.0; }

    /**
     * Returns the full {@link Allowance} object for detailed breakdown.
     *
     * @return the Allowance object
     */
    public Allowance getAllowanceDetails() { return allowance; }

    /**
     * Sets a scalar total allowance, stored as rice subsidy; phone and clothing are set to zero.
     *
     * @param totalAllowance must be &ge; 0
     * @throws IllegalArgumentException if negative
     */
    public void setAllowance(double totalAllowance) {
        if (totalAllowance < 0) {
            throw new IllegalArgumentException("Allowance cannot be negative. Received: " + totalAllowance);
        }
        this.allowance = new Allowance(employeeID, totalAllowance, 0, 0);
    }

    /**
     * Sets the allowance using a fully-populated {@link Allowance} object.
     *
     * @param allowance the Allowance object to set
     */
    public void setAllowance(Allowance allowance) { this.allowance = allowance; }

    /**
     * Alias for {@link #getEmployeeID()} — provided for spec compatibility.
     *
     * @return the employee's unique identifier
     */
    public String getEmployeeNumber() { return employeeID; }

    /** @return the government details object */
    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }

    /** @param governmentDetails the government details to set */
    public void setGovernmentDetails(GovernmentDetails governmentDetails) {
        this.governmentDetails = governmentDetails;
    }

    /** @return the login credentials object */
    public Login getLogin() { return login; }

    /** @param login the login credentials to set */
    public void setLogin(Login login) { this.login = login; }

    /**
     * Returns a read-only view of the attendance list.
     * Use {@link #addAttendance(Attendance)} to append records.
     *
     * @return unmodifiable list of attendance records
     */
    public List<Attendance> getAttendanceList() {
        return Collections.unmodifiableList(attendanceList);
    }

    /**
     * Appends one attendance record to this employee's attendance list.
     *
     * @param attendance the record to add; must not be null
     * @throws IllegalArgumentException if {@code attendance} is null
     */
    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record must not be null.");
        }
        this.attendanceList.add(attendance);
    }

    /**
     * Replaces the entire attendance list (bulk-load use only).
     *
     * @param attendanceList replacement list; must not be null
     * @throws IllegalArgumentException if {@code attendanceList} is null
     */
    public void setAttendanceList(List<Attendance> attendanceList) {
        if (attendanceList == null) {
            throw new IllegalArgumentException("Attendance list must not be null.");
        }
        this.attendanceList = new ArrayList<>(attendanceList);
    }

    /**
     * Returns a read-only view of the payslips list.
     * Use {@link #addPayslip(Payslip)} to append records.
     *
     * @return unmodifiable list of payslips
     */
    public List<Payslip> getPayslips() {
        return Collections.unmodifiableList(payslips);
    }

    /**
     * Appends one payslip to this employee's payslips list.
     *
     * @param payslip the payslip to add; must not be null
     * @throws IllegalArgumentException if {@code payslip} is null
     */
    public void addPayslip(Payslip payslip) {
        if (payslip == null) {
            throw new IllegalArgumentException("Payslip must not be null.");
        }
        this.payslips.add(payslip);
    }

    /**
     * Replaces the entire payslips list (bulk-load use only).
     *
     * @param payslips replacement list; must not be null
     * @throws IllegalArgumentException if {@code payslips} is null
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
