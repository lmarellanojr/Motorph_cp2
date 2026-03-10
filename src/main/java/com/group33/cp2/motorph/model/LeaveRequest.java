package com.group33.cp2.motorph.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a single leave request submitted by an employee.
 *
 * <p>Encapsulates all state for one leave request: who submitted it, what type
 * of leave, when, the approval status, and the HR responder details.</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> All fields are private.
 * Business-state transitions (approve/reject) are done through dedicated methods
 * rather than raw setters, ensuring the object always stays consistent.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class LeaveRequest {

    private static final List<String> VALID_STATUSES =
        List.of("Pending", "Approved", "Rejected");

    private String leaveID;
    private String employeeID;
    private String leaveType;
    private LocalDate dateRequest;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private String approver;
    private LocalDate dateResponded;
    private String remark;

    /**
     * Constructs a {@code LeaveRequest} with all fields populated (used when reading from CSV).
     */
    public LeaveRequest(String leaveID, String employeeID, String leaveType,
                        LocalDate dateRequest, LocalDate startDate, LocalDate endDate,
                        String reason, String status, String approver,
                        LocalDate dateResponded, String remark) {
        this.leaveID = leaveID;
        this.employeeID = employeeID;
        this.leaveType = leaveType;
        this.dateRequest = dateRequest;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approver = approver;
        this.dateResponded = dateResponded;
        this.remark = remark;
    }

    // =========================================================================
    //  Business-state transition methods
    // =========================================================================

    /**
     * Marks this leave request as Approved, recording the approver name and today's date.
     *
     * @throws IllegalStateException if the current status is not "Pending"
     */
    public void approve(String approverName) {
        if (!"Pending".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException(
                "Cannot approve leave request '" + leaveID + "': current status is '" + status + "'");
        }
        this.status = "Approved";
        this.approver = approverName;
        this.dateResponded = LocalDate.now();
    }

    /**
     * Marks this leave request as Rejected, recording the approver name, today's date,
     * and the rejection remark.
     *
     * @throws IllegalStateException if the current status is not "Pending"
     */
    public void reject(String approverName, String rejectionRemark) {
        if (!"Pending".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException(
                "Cannot reject leave request '" + leaveID + "': current status is '" + status + "'");
        }
        this.status = "Rejected";
        this.approver = approverName;
        this.dateResponded = LocalDate.now();
        this.remark = rejectionRemark;
    }

    // =========================================================================
    //  Getters and setters
    // =========================================================================

    public String getLeaveID() { return leaveID; }
    public void setLeaveID(String leaveID) { this.leaveID = leaveID; }

    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getDateRequest() { return dateRequest; }
    public void setDateRequest(LocalDate dateRequest) { this.dateRequest = dateRequest; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }

    /**
     * Sets the status of this leave request.
     *
     * @param status must be one of "Pending", "Approved", or "Rejected"
     * @throws IllegalArgumentException if status is null or not in the whitelist
     */
    public void setStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                "Status must be one of " + VALID_STATUSES + ". Received: '" + status + "'");
        }
        this.status = status;
    }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public LocalDate getDateResponded() { return dateResponded; }
    public void setDateResponded(LocalDate dateResponded) { this.dateResponded = dateResponded; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString() {
        return "LeaveRequest{leaveID='" + leaveID + "', employeeID='" + employeeID
                + "', type='" + leaveType + "', status='" + status + "'}";
    }
}
