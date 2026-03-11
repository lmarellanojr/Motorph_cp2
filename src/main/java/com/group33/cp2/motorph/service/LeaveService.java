package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.EmployeeLeaveTracker;
import com.group33.cp2.motorph.dao.LeaveRequestReader;
import com.group33.cp2.motorph.model.LeaveRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

// Service facade for all leave-related operations.
// Aggregates LeaveRequestReader, EmployeeLeaveTracker, and LeaveProcessor so that
// forms/ classes never need to import from dao/.
// HR model delegates approveLeave/rejectLeave here to keep model/ free of dao/ imports.
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

    // Approves a pending leave request, persists the updated status, and deducts the
    // corresponding days from the employee's leave balance.
    // Returns true on success; false if the request is not found or is not Pending.
    // Called by HR.approveLeave() so the HR model class has no direct dao/ imports.
    public boolean approveLeave(String leaveId, String hrName, String remark) throws IOException {
        LeaveRequest request = LeaveRequestReader.getLeaveById(leaveId);
        if (request == null || !"Pending".equalsIgnoreCase(request.getStatus())) {
            return false;
        }
        request.approve(hrName);
        if (remark != null && !remark.isBlank()) {
            request.setRemark(remark);
        }
        LeaveRequestReader.updateLeaveRequest(request);

        long daysRequested = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        EmployeeLeaveTracker.updateLeaveBalance(
                request.getEmployeeID(), request.getLeaveType(), (int) daysRequested);
        return true;
    }

    // Rejects a pending leave request and persists the updated status with the supplied remark.
    // Returns true on success; false if the request is not found or is not Pending.
    // Called by HR.rejectLeave() so the HR model class has no direct dao/ imports.
    public boolean rejectLeave(String leaveId, String hrName, String remark) throws IOException {
        LeaveRequest request = LeaveRequestReader.getLeaveById(leaveId);
        if (request == null || !"Pending".equalsIgnoreCase(request.getStatus())) {
            return false;
        }
        request.reject(hrName, remark);
        LeaveRequestReader.updateLeaveRequest(request);
        return true;
    }
}
