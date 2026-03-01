package com.group33.cp2.motorph;

import java.util.UUID;
import java.time.*;

/**
 * Stores one day's attendance record for an employee, including login/logout times and hour calculations.
 */
public class Attendance {

    private static final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0);
    private static final int GRACE_PERIOD_MINUTES = 10;

    // auto-generated at construction, no setter
    private final String attendanceID;
    private String employeeID;
    private LocalDate date;
    private LocalTime loginTime;
    private LocalTime logoutTime;

    /**
     * Creates an attendance record. A unique ID is auto-generated.
     */
    public Attendance(String employeeID, LocalDate date, LocalTime loginTime, LocalTime logoutTime) {
        this.attendanceID = UUID.randomUUID().toString();
        this.employeeID = employeeID;
        this.date = date;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    public String getAttendanceID() {
        return attendanceID;
    }

    // setAttendanceID() intentionally not provided

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

    // caps logout at 5 PM if employee was late (no overtime)
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

    // deducts 1 hour for lunch if total worked > 5 hours
    public double getTotalHours() {
        Duration workedDuration = Duration.between(getLoginTime(), getLogoutTime());
        double totalHours = workedDuration.toMinutes() / 60.0;

        if (totalHours > 5) {
            totalHours -= 1.0;
        }
        return totalHours;
    }

    // regular hours capped at 8 per day
    public double getRegularHours() {
        return Math.min(getTotalHours(), 8.0);
    }

    // overtime only counts if not late; returns 0 otherwise
    public double getOvertimeHours() {
        if (!isLate()) {
            return Math.max(0, getTotalHours() - 8.0);
        }
        return 0.0;
    }

    public double getRoundedRegularHours() {
        double regularHours = Math.min(getTotalHours(), 8.0);
        return Math.round(regularHours * 100.0) / 100.0;
    }

    public double getRoundedOvertimeHours() {
        double overtimeHours = Math.max(0, getTotalHours() - 8.0);
        return Math.round(overtimeHours * 100.0) / 100.0;
    }

    // late = logged in after the 10-minute grace period past 8:00 AM
    public boolean isLate() {
        if (!loginTime.isBefore(SHIFT_START)
                && loginTime.isBefore(SHIFT_START.plusMinutes(GRACE_PERIOD_MINUTES))) {
            return false;
        }
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
