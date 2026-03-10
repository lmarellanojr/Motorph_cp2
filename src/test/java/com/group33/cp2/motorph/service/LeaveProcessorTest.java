package com.group33.cp2.motorph.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for LeaveProcessor pure-validation guards (past-date, date-order, weekend).
// Balance-check and CSV-write paths require live files; covered by SimulationRunner instead.
class LeaveProcessorTest {

    private final LeaveProcessor processor = new LeaveProcessor();

    // Returns a Monday that is at least 7 days in the future.
    private LocalDate nextMonday() {
        LocalDate date = LocalDate.now().plusDays(7);
        while (date.getDayOfWeek().getValue() != 1) { // 1 = MONDAY
            date = date.plusDays(1);
        }
        return date;
    }

    // Returns the Friday of the same week as the given Monday.
    private LocalDate fridayOf(LocalDate monday) {
        return monday.plusDays(4);
    }

    // =========================================================================
    //  Past-date guard
    // =========================================================================

    @Test
    void processLeaveRequest_startDateInPast_throwsIllegalArgument() {
        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate future = LocalDate.now().plusDays(5);
        // Use Monday-Friday range to avoid weekend rejection before date check
        LocalDate futureMonday = nextMonday();
        LocalDate futureFriday = fridayOf(futureMonday);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", past, futureFriday, "Flu"));
        assertTrue(ex.getMessage().toLowerCase().contains("past"),
                "Error message should mention 'past'");
    }

    @Test
    void processLeaveRequest_endDateInPast_throwsIllegalArgument() {
        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate monday = nextMonday();

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", monday, past, "Flu"));
    }

    @Test
    void processLeaveRequest_bothDatesInPast_throwsIllegalArgument() {
        LocalDate past1 = LocalDate.now().minusDays(3);
        LocalDate past2 = LocalDate.now().minusDays(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Vacation Leave", past1, past2, "Trip"));
    }

    // =========================================================================
    //  Date-order guard (startDate after endDate)
    // =========================================================================

    @Test
    void processLeaveRequest_startAfterEnd_throwsIllegalArgument() {
        LocalDate monday = nextMonday();
        LocalDate friday = fridayOf(monday);

        // Intentionally swap start and end
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", friday, monday, "Swap"));
        assertTrue(ex.getMessage().toLowerCase().contains("start"),
                "Error message should mention 'start'");
    }

    // =========================================================================
    //  Weekend guard
    // =========================================================================

    @Test
    void processLeaveRequest_startOnSaturday_throwsIllegalArgument() {
        LocalDate monday = nextMonday();
        LocalDate saturday = monday.plusDays(5); // Monday + 5 = Saturday
        LocalDate followingMonday = monday.plusDays(7);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", saturday, followingMonday, "Sick"));
        assertTrue(ex.getMessage().toLowerCase().contains("weekend"),
                "Error message should mention 'weekend'");
    }

    @Test
    void processLeaveRequest_startOnSunday_throwsIllegalArgument() {
        LocalDate monday = nextMonday();
        LocalDate sunday = monday.plusDays(6); // Monday + 6 = Sunday
        LocalDate followingMonday = monday.plusDays(7);

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", sunday, followingMonday, "Sick"));
    }

    @Test
    void processLeaveRequest_endOnSaturday_throwsIllegalArgument() {
        LocalDate monday = nextMonday();
        LocalDate saturday = monday.plusDays(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", monday, saturday, "Sick"));
    }

    @Test
    void processLeaveRequest_endOnSunday_throwsIllegalArgument() {
        LocalDate monday = nextMonday();
        LocalDate sunday = monday.plusDays(6);

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest("10001", "Sick Leave", monday, sunday, "Sick"));
    }

    // =========================================================================
    //  Validation order — past-date is checked before date-order
    // =========================================================================

    @Test
    void processLeaveRequest_pastDateBeforeDateOrderCheck() {
        // If startDate is in the past AND start > end, the past-date error fires first
        LocalDate past = LocalDate.now().minusDays(3);
        LocalDate evenFurtherPast = LocalDate.now().minusDays(5);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> processor.processLeaveRequest(
                        "10001", "Sick Leave", past, evenFurtherPast, "Test"));
        assertTrue(ex.getMessage().toLowerCase().contains("past"),
                "Past-date guard should fire before date-order guard");
    }
}
