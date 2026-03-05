package com.group33.cp2.motorph;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles all business-rule validation and submission logic for employee leave requests.
 *
 * <p>Ported from the MotorPH OOP reference project and adapted for the cp2 package.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> All leave submission business rules are hidden
 * behind the single public method {@link #processLeaveRequest(String, String, LocalDate, LocalDate, String)}.
 * Callers (e.g., {@code EmployeeDashboard}) do not need to know about past-date guards, weekend
 * checks, weekday-only duration counting, balance verification, or ID generation — that complexity
 * lives entirely inside this class.</p>
 *
 * <p>Note: Leave balance is NOT deducted on submission. Deduction happens only when HR
 * approves the request via {@link HR#approveLeave(String, String)}, preventing
 * double-deduction.</p>
 *
 * @author Group13
 */
public class LeaveProcessor {

    /**
     * Validates and submits a new leave request for an employee.
     *
     * <p>Validation steps (in order):</p>
     * <ol>
     *   <li>Start and end dates must not be in the past.</li>
     *   <li>Start date must not be after end date.</li>
     *   <li>Neither start nor end date may fall on a Saturday or Sunday.</li>
     *   <li>Employee must have sufficient leave balance (weekday count of the range).</li>
     * </ol>
     *
     * <p>If all checks pass, a leave ID in {@code L001} / {@code L002} format is auto-generated,
     * a {@link LeaveRequest} is constructed in "Pending" status, and it is written to
     * {@code LeaveRequests.csv} via {@link LeaveRequestReader#addLeaveRequest(LeaveRequest)}.</p>
     *
     * @param employeeId the employee number of the requester
     * @param leaveType  one of "Sick Leave", "Vacation Leave", or "Birthday Leave"
     * @param startDate  first day of leave (inclusive)
     * @param endDate    last day of leave (inclusive)
     * @param reason     free-text reason for the leave
     * @throws IllegalArgumentException if any validation rule is violated
     * @throws IOException              if the leave requests CSV cannot be read or written
     */
    public void processLeaveRequest(String employeeId, String leaveType,
            LocalDate startDate, LocalDate endDate, String reason) throws IOException {

        // Guard: dates must not be in the past
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave cannot be in the past!");
        }

        // Guard: start must not be after end
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be later than end date!");
        }

        // Guard: neither boundary may fall on a weekend
        if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY
                || startDate.getDayOfWeek() == DayOfWeek.SUNDAY
                || endDate.getDayOfWeek() == DayOfWeek.SATURDAY
                || endDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Leave cannot start or end on a weekend!");
        }

        // Balance check (weekday-only duration)
        int leaveDuration = calculateWeekdays(startDate, endDate);
        int balance = EmployeeLeaveTracker.getLeaveBalance(employeeId, leaveType);
        if (balance < leaveDuration) {
            throw new IllegalArgumentException("Insufficient leave balance.");
        }

        // Submit the request (balance deduction deferred to HR approval)
        String leaveId = generateLeaveId();
        LeaveRequest leaveRequest = new LeaveRequest(
                leaveId, employeeId, leaveType, LocalDate.now(),
                startDate, endDate, reason, "Pending", "", null, "");
        LeaveRequestReader.addLeaveRequest(leaveRequest);
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    /**
     * Counts only Monday through Friday days in the inclusive range [startDate, endDate].
     *
     * @param startDate first day of the range
     * @param endDate   last day of the range
     * @return number of weekday days in the range
     */
    private int calculateWeekdays(LocalDate startDate, LocalDate endDate) {
        int weekdays = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                weekdays++;
            }
        }
        return weekdays;
    }

    /**
     * Generates the next leave ID by reading the current maximum numeric suffix from
     * {@code LeaveRequests.csv} and incrementing it.
     *
     * <p>Examples: if the highest existing ID is {@code L003}, this returns {@code L004}.
     * If no requests exist yet, this returns {@code L001}.</p>
     *
     * @return next leave ID in {@code L###} zero-padded format
     */
    private String generateLeaveId() {
        List<LeaveRequest> requests = new LeaveRequestReader().getAllLeaveRequests();
        int maxId = 0;
        for (LeaveRequest request : requests) {
            String idStr = request.getLeaveID().replace("L", "");
            try {
                int idNum = Integer.parseInt(idStr);
                if (idNum > maxId) {
                    maxId = idNum;
                }
            } catch (NumberFormatException ignored) {
                // Non-numeric IDs (e.g. legacy timestamp IDs) are skipped
            }
        }
        return "L" + String.format("%03d", maxId + 1);
    }
}
