package com.group33.cp2.motorph;

import java.time.LocalDate;

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
 * @author Group13
 * @version 1.0
 */
public class LeaveRequest {

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
     *
     * @param leaveID       unique leave request identifier
     * @param employeeID    the employee who submitted the request
     * @param leaveType     type of leave (e.g., "Sick Leave", "Vacation Leave", "Birthday Leave")
     * @param dateRequest   date the request was submitted
     * @param startDate     first day of the leave
     * @param endDate       last day of the leave
     * @param reason        reason stated by the employee
     * @param status        current status: "Pending", "Approved", or "Rejected"
     * @param approver      name of the HR approver (empty string if not yet actioned)
     * @param dateResponded date HR responded (null if not yet actioned)
     * @param remark        HR remark (empty string if none)
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
     * @param approverName name of the HR employee approving the request
     */
    public void approve(String approverName) {
        this.status = "Approved";
        this.approver = approverName;
        this.dateResponded = LocalDate.now();
    }

    /**
     * Marks this leave request as Rejected, recording the approver name, today's date,
     * and the rejection remark.
     *
     * @param approverName name of the HR employee rejecting the request
     * @param rejectionRemark reason for rejection
     */
    public void reject(String approverName, String rejectionRemark) {
        this.status = "Rejected";
        this.approver = approverName;
        this.dateResponded = LocalDate.now();
        this.remark = rejectionRemark;
    }

    // =========================================================================
    //  Getters and setters
    // =========================================================================

    /** @return the unique leave request identifier */
    public String getLeaveID() { return leaveID; }

    /** @param leaveID the leave request identifier */
    public void setLeaveID(String leaveID) { this.leaveID = leaveID; }

    /** @return the employee ID who submitted this request */
    public String getEmployeeID() { return employeeID; }

    /** @param employeeID the employee ID */
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    /** @return the type of leave */
    public String getLeaveType() { return leaveType; }

    /** @param leaveType type of leave */
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    /** @return the date the request was submitted */
    public LocalDate getDateRequest() { return dateRequest; }

    /** @param dateRequest the date the request was submitted */
    public void setDateRequest(LocalDate dateRequest) { this.dateRequest = dateRequest; }

    /** @return the first day of leave */
    public LocalDate getStartDate() { return startDate; }

    /** @param startDate the first day of leave */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /** @return the last day of leave */
    public LocalDate getEndDate() { return endDate; }

    /** @param endDate the last day of leave */
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /** @return the reason stated by the employee */
    public String getReason() { return reason; }

    /** @param reason the reason for the leave */
    public void setReason(String reason) { this.reason = reason; }

    /** @return current status: "Pending", "Approved", or "Rejected" */
    public String getStatus() { return status; }

    /** @param status new status */
    public void setStatus(String status) { this.status = status; }

    /** @return name of the HR approver, or empty string if not yet actioned */
    public String getApprover() { return approver; }

    /** @param approver name of the HR approver */
    public void setApprover(String approver) { this.approver = approver; }

    /** @return date HR responded, or null if not yet actioned */
    public LocalDate getDateResponded() { return dateResponded; }

    /** @param dateResponded date HR responded */
    public void setDateResponded(LocalDate dateResponded) { this.dateResponded = dateResponded; }

    /** @return HR remark, or empty string if none */
    public String getRemark() { return remark; }

    /** @param remark HR remark */
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString() {
        return "LeaveRequest{leaveID='" + leaveID + "', employeeID='" + employeeID
                + "', type='" + leaveType + "', status='" + status + "'}";
    }
}
