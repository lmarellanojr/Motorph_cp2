package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.EmployeeLeaveTracker;
import com.group33.cp2.motorph.dao.LeaveRequestReader;
import com.group33.cp2.motorph.model.LeaveRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer facade for all leave-related operations.
 *
 * <p>Aggregates {@link LeaveRequestReader}, {@link EmployeeLeaveTracker}, and
 * {@link LeaveProcessor} so that {@code forms/} classes have a single service
 * import for every leave concern.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> All DAO and validation details are
 * hidden behind this class. Forms never need to know whether leave data comes from
 * {@code LeaveRequests.csv} or {@code LeaveBalances.csv}.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class LeaveService {

    private final LeaveRequestReader leaveRequestReader;
    private final LeaveProcessor     leaveProcessor;

    /**
     * Constructs a LeaveService with default DAO and processor instances.
     */
    public LeaveService() {
        this.leaveRequestReader = new LeaveRequestReader();
        this.leaveProcessor     = new LeaveProcessor();
    }

    // -------------------------------------------------------------------------
    //  Leave balance queries
    // -------------------------------------------------------------------------

    /**
     * Returns the leave balance for the given employee and leave type.
     *
     * @param empId     the employee number
     * @param leaveType one of "Sick Leave", "Vacation Leave", or "Birthday Leave"
     * @return the remaining balance in days; {@code 0} if not found or on I/O error
     * @throws IOException if the CSV cannot be read
     */
    public int getLeaveBalance(String empId, String leaveType) throws IOException {
        return EmployeeLeaveTracker.getLeaveBalance(empId, leaveType);
    }

    // -------------------------------------------------------------------------
    //  Leave request queries
    // -------------------------------------------------------------------------

    /**
     * Returns all leave requests for the given employee.
     *
     * @param empId the employee number to filter by
     * @return a list of {@link LeaveRequest} objects belonging to that employee;
     *         never {@code null}, may be empty
     */
    public List<LeaveRequest> getLeaveRequestsByEmployee(String empId) {
        return leaveRequestReader.getAllLeaveRequests().stream()
                .filter(req -> req.getEmployeeID().equals(empId))
                .collect(Collectors.toList());
    }

    /**
     * Returns all leave requests from the CSV (all employees).
     *
     * @return a list of all {@link LeaveRequest} records; never {@code null}
     */
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestReader.getAllLeaveRequests();
    }

    // -------------------------------------------------------------------------
    //  Leave submission
    // -------------------------------------------------------------------------

    /**
     * Validates and submits a new leave request for an employee.
     *
     * <p>Delegates all validation and persistence to {@link LeaveProcessor}.</p>
     *
     * @param empId     the employee number of the requester
     * @param leaveType one of "Sick Leave", "Vacation Leave", or "Birthday Leave"
     * @param startDate first day of leave (inclusive)
     * @param endDate   last day of leave (inclusive)
     * @param reason    free-text reason for the leave
     * @throws IllegalArgumentException if any validation rule is violated
     * @throws IOException              if the CSV cannot be read or written
     */
    public void submitLeaveRequest(String empId, String leaveType,
            LocalDate startDate, LocalDate endDate, String reason) throws IOException {
        leaveProcessor.processLeaveRequest(empId, leaveType, startDate, endDate, reason);
    }
}
