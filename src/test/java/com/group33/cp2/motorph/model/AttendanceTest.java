package com.group33.cp2.motorph.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Attendance business logic.
 *
 * Business rules under test:
 *   - Grace period: login <= 08:10 is on-time (isLate = false)
 *   - Late:         login > 08:10 → isLate() = true; logout capped at 17:00 (no OT)
 *   - Lunch deduction: subtract 1 hour if totalHours > 5
 *   - Regular hours: min(totalHours, 8.0)
 *   - Overtime: max(0, totalHours - 8.0) only if not late
 */
class AttendanceTest {

    private static final LocalDate SOME_DATE = LocalDate.of(2025, 1, 15);

    // =========================================================================
    //  isLate() — grace period boundary tests
    // =========================================================================

    @Test
    void isLate_loginAt0800_returnsFalse() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertFalse(a.isLate(), "Login exactly at 08:00 is on-time");
    }

    @Test
    void isLate_loginAt0805_returnsFalse() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 5), LocalTime.of(17, 0));
        assertFalse(a.isLate(), "Login at 08:05 is within grace period");
    }

    @Test
    void isLate_loginAt0809_returnsFalse() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 9), LocalTime.of(17, 0));
        assertFalse(a.isLate(), "Login at 08:09 is within grace period");
    }

    @Test
    void isLate_loginAt0810_returnsFalse() {
        // 08:10 is the last second of the grace window (strictly before graceEnd=08:10:00?)
        // isLate() checks: loginTime.isAfter(graceEnd) where graceEnd = 08:10
        // 08:10 is NOT after 08:10, so returns false
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 10), LocalTime.of(17, 0));
        assertFalse(a.isLate(), "Login exactly at 08:10 is still within grace period");
    }

    @Test
    void isLate_loginAt0811_returnsTrue() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 11), LocalTime.of(17, 0));
        assertTrue(a.isLate(), "Login at 08:11 is past grace period — late");
    }

    @Test
    void isLate_loginAt0900_returnsTrue() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(9, 0), LocalTime.of(17, 0));
        assertTrue(a.isLate(), "Login at 09:00 is clearly late");
    }

    @Test
    void isLate_loginBefore0800_earlyArrival_returnsFalse() {
        // Employee arrived at 07:30 — early; !loginTime.isBefore(08:00) = false,
        // so the first branch does not apply; loginTime.isAfter(08:10) = false,
        // so the second branch also does not apply → returns false (not late)
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(7, 30), LocalTime.of(17, 0));
        assertFalse(a.isLate(), "Early arrival (07:30) should not be treated as late");
    }

    // =========================================================================
    //  getLogoutTime() — late employee cap
    // =========================================================================

    @Test
    void getLogoutTime_lateEmployee_logoutAfter1700_cappedAt1700() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 30), LocalTime.of(18, 0));
        assertEquals(LocalTime.of(17, 0), a.getLogoutTime(), "Late employee logout should be capped at 17:00");
    }

    @Test
    void getLogoutTime_lateEmployee_logoutBefore1700_notCapped() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 30), LocalTime.of(16, 30));
        assertEquals(LocalTime.of(16, 30), a.getLogoutTime(), "Late employee who left before 17:00 is not capped");
    }

    @Test
    void getLogoutTime_onTimeEmployee_allowsOvertimeBeyond1700() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(19, 0));
        assertEquals(LocalTime.of(19, 0), a.getLogoutTime(), "On-time employee keeps actual logout time");
    }

    // =========================================================================
    //  getTotalHours() — lunch deduction
    // =========================================================================

    @Test
    void getTotalHours_exactlyFiveHours_noLunchDeduction() {
        // 08:00 to 13:00 = 5 hours, NOT > 5 → no deduction
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(13, 0));
        assertEquals(5.0, a.getTotalHours(), 0.001);
    }

    @Test
    void getTotalHours_moreThanFiveHours_lunchDeducted() {
        // 08:00 to 17:00 = 9 hours worked; > 5 → subtract 1 → 8.0
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertEquals(8.0, a.getTotalHours(), 0.001);
    }

    @Test
    void getTotalHours_lessThanFiveHours_noLunchDeduction() {
        // 08:00 to 11:00 = 3 hours, NOT > 5 → no deduction
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(11, 0));
        assertEquals(3.0, a.getTotalHours(), 0.001);
    }

    @Test
    void getTotalHours_nineHoursRaw_returnseight() {
        // 08:00 to 18:00 = 10 raw hours; > 5 → -1 lunch → 9.0
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(18, 0));
        assertEquals(9.0, a.getTotalHours(), 0.001);
    }

    // =========================================================================
    //  getRegularHours() — capped at 8.0
    // =========================================================================

    @Test
    void getRegularHours_standardShift0800to1700_returns8() {
        // 9 raw hours - 1 lunch = 8 hours; min(8,8) = 8
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertEquals(8.0, a.getRegularHours(), 0.001);
    }

    @Test
    void getRegularHours_shortShift_returnActualHours() {
        // 08:00 to 12:00 = 4 raw hours; not > 5 → no lunch; min(4,8) = 4
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(12, 0));
        assertEquals(4.0, a.getRegularHours(), 0.001);
    }

    @Test
    void getRegularHours_overtimeShift_cappedAt8() {
        // 08:00 to 20:00 = 12 raw - 1 lunch = 11; min(11,8) = 8
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(20, 0));
        assertEquals(8.0, a.getRegularHours(), 0.001);
    }

    // =========================================================================
    //  getOvertimeHours() — only for on-time employees
    // =========================================================================

    @Test
    void getOvertimeHours_onTimeWithOvertime_returnsPositiveOT() {
        // 08:00 to 20:00 = 12 raw - 1 lunch = 11; OT = max(0, 11-8) = 3
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(20, 0));
        assertEquals(3.0, a.getOvertimeHours(), 0.001);
    }

    @Test
    void getOvertimeHours_onTimeNoOvertime_returnsZero() {
        // 08:00 to 17:00 = 9 raw - 1 lunch = 8; OT = max(0, 8-8) = 0
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertEquals(0.0, a.getOvertimeHours(), 0.001);
    }

    @Test
    void getOvertimeHours_lateEmployeeWithExtraHours_returnsZero() {
        // Late employee logged out at 20:00 but logout is capped to 17:00
        // Even if not capped, late employees get 0 OT regardless
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(9, 0), LocalTime.of(20, 0));
        assertEquals(0.0, a.getOvertimeHours(), 0.001, "Late employees never get overtime");
    }

    @Test
    void getOvertimeHours_onTimeShortShift_returnsZero() {
        // 08:00 to 12:00 = 4 raw hours; OT = max(0, 4-8) = 0
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(12, 0));
        assertEquals(0.0, a.getOvertimeHours(), 0.001);
    }

    // =========================================================================
    //  attendanceID immutability
    // =========================================================================

    @Test
    void attendanceID_isAutoGenerated_andNonNull() {
        Attendance a = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertNotNull(a.getAttendanceID(), "Attendance ID should be auto-generated");
    }

    @Test
    void attendanceID_twoDistinctRecords_haveDifferentIDs() {
        Attendance a1 = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        Attendance a2 = new Attendance("E001", SOME_DATE, LocalTime.of(8, 0), LocalTime.of(17, 0));
        assertNotEquals(a1.getAttendanceID(), a2.getAttendanceID(),
                "Each Attendance record should have a unique ID");
    }
}
