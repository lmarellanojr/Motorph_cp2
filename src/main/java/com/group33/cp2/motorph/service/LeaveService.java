package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.EmployeeLeaveTracker;
import com.group33.cp2.motorph.dao.LeaveRequestReader;
import com.group33.cp2.motorph.model.LeaveRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Service facade for all leave-related operations.
// Aggregates LeaveRequestReader, EmployeeLeaveTracker, and LeaveProcessor so that
// forms/ classes never need to import from dao/.
public class LeaveService {

    private final LeaveRequestReader leaveRequestReader;
    private final LeaveProcessor     leaveProcessor;

    public LeaveService() {
        this.leaveRequestReader = new LeaveRequestReader();
        this.leaveProcessor     = new LeaveProcessor();
    }

    // Returns the leave balance for the given employee and leave type.
    public int getLeaveBalance(String empId, String leaveType) throws IOException {
        return EmployeeLeaveTracker.getLeaveBalance(empId, leaveType);
    }

    // Returns all leave requests for the given employee.
    public List<LeaveRequest> getLeaveRequestsByEmployee(String empId) {
        return leaveRequestReader.getAllLeaveRequests().stream()
                .filter(req -> req.getEmployeeID().equals(empId))
                .collect(Collectors.toList());
    }

    // Returns all leave requests from the CSV (all employees).
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestReader.getAllLeaveRequests();
    }

    // Validates and submits a leave request; delegates all logic to LeaveProcessor.
    public void submitLeaveRequest(String empId, String leaveType,
            LocalDate startDate, LocalDate endDate, String reason) throws IOException {
        leaveProcessor.processLeaveRequest(empId, leaveType, startDate, endDate, reason);
    }
}
