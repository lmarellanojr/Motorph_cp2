package com.group33.cp2.motorph;

import java.util.UUID;
import java.time.*;

/**
 * Represents an attendance record for an employee, including login/logout
 * times, shift handling, and hour calculations.
 *
 * <p><strong>Encapsulation:</strong> Business-rule constants (SHIFT_START, SHIFT_END,
 * GRACE_PERIOD_MINUTES) are declared {@code private static final} so no external
 * class can read or alter the shift rules directly. The auto-generated
 * {@code attendanceID} has no public setter — it is immutable after construction.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class Attendance {

    // BP1: Business-rule constants are private static final — no external access or mutation
    private static final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0);
    private static final int GRACE_PERIOD_MINUTES = 10;

    // BP5: attendanceID is auto-generated at construction — no public setter provided
    private final String attendanceID;
    private String employeeID;
    private LocalDate date;
    private LocalTime loginTime;
    private LocalTime logoutTime;

    /**
     * Constructs an Attendance object with the specified employee ID, date,
     * login time, and logout time. A unique attendance ID is automatically generated.
     *
     * @param employeeID the ID of the employee
     * @param date       the date of the attendance record
     * @param loginTime  the time the employee logged in
     * @param logoutTime the time the employee logged out
     */
    public Attendance(String employeeID, LocalDate date, LocalTime loginTime, LocalTime logoutTime) {
        this.attendanceID = UUID.randomUUID().toString();
        this.employeeID = employeeID;
        this.date = date;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    /**
     * Returns the auto-generated unique attendance identifier.
     * This value is immutable after construction.
     *
     * @return the attendance UUID string
     */
    public String getAttendanceID() {
        return attendanceID;
    }

    // BP5: setAttendanceID() removed — attendanceID is set once via UUID in the constructor

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalTime loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * Returns logout time. If employee is late, caps the logout time at 5:00 PM
     * (no overtime).
     *
     * @return the logout time, possibly capped at shift end
     */
    public LocalTime getLogoutTime() {
        if (isLate()) {
            if (logoutTime.isAfter(SHIFT_END)) {
                logoutTime = SHIFT_END;
            }
        }
        return logoutTime;
    }

    public void setLogoutTime(LocalTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    /**
     * Calculates the total number of hours worked in a day.
     * Deducts 1 hour for lunch if the total exceeds 5 hours.
     *
     * @return Total hours worked, as a double
     */
    public double getTotalHours() {
        Duration workedDuration = Duration.between(getLoginTime(), getLogoutTime());
        double totalHours = workedDuration.toMinutes() / 60.0;

        if (totalHours > 5) {
            totalHours -= 1.0;
        }
        return totalHours;
    }

    /**
     * Calculates the number of regular hours worked. Capped at 8 hours max per day.
     *
     * @return regular hours worked (max 8.0)
     */
    public double getRegularHours() {
        return Math.min(getTotalHours(), 8.0);
    }

    /**
     * Calculates overtime hours only if the employee was not late.
     * Overtime = Total hours - 8, if applicable.
     *
     * @return overtime hours, or 0 if employee was late
     */
    public double getOvertimeHours() {
        if (!isLate()) {
            return Math.max(0, getTotalHours() - 8.0);
        }
        return 0.0;
    }

    /**
     * Returns regular hours rounded to 2 decimal places.
     *
     * @return rounded regular hours
     */
    public double getRoundedRegularHours() {
        double regularHours = Math.min(getTotalHours(), 8.0);
        return Math.round(regularHours * 100.0) / 100.0;
    }

    /**
     * Returns overtime hours rounded to 2 decimal places.
     *
     * @return rounded overtime hours
     */
    public double getRoundedOvertimeHours() {
        double overtimeHours = Math.max(0, getTotalHours() - 8.0);
        return Math.round(overtimeHours * 100.0) / 100.0;
    }

    /**
     * Determines if the employee is considered late.
     * A grace period of {@value #GRACE_PERIOD_MINUTES} minutes is allowed past 8:00 AM.
     *
     * @return true if late, false if on time or within grace period
     */
    public boolean isLate() {
        // Within grace window: at or after shift start but before shift start + grace period
        if (!loginTime.isBefore(SHIFT_START)
                && loginTime.isBefore(SHIFT_START.plusMinutes(GRACE_PERIOD_MINUTES))) {
            return false;
        }
        // Strictly after grace window
        if (loginTime.isAfter(SHIFT_START.plusMinutes(GRACE_PERIOD_MINUTES))) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return " Employee ID: " + employeeID
                + ", Date: " + date
                + ", Login Time: " + loginTime
                + ", Logout Time: " + logoutTime
                + ", Total Hours Worked: " + getTotalHours();
    }
}
