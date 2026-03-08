package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.dao.EmployeeLeaveTracker;
import com.group33.cp2.motorph.dao.LeaveRequestReader;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.PayrollCalculator;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

/**
 * Represents an HR department employee in the MotorPH Payroll System.
 *
 * <p>HR employees receive full regular-employee payroll treatment
 * (all four deductions, overtime at 1.25x) and additionally implement
 * the leave-management operations defined by {@link HROperations}.</p>
 *
 * @author Group 33
 * @version 2.1
 */
public class HR extends Employee implements PayrollCalculable, HROperations {

    /** Lazily-initialized service; never created in the constructor to avoid circular instantiation. */
    private EmployeeService employeeService;

    /**
     * Returns the shared {@link EmployeeService} instance, creating it on first access.
     * Using a lazy accessor prevents the infinite-recursion that would occur if
     * {@code EmployeeService.reloadEmployees()} constructed an HR object that eagerly
     * created another {@code EmployeeService}.
     */
    private EmployeeService getEmployeeService() {
        if (employeeService == null) {
            employeeService = new EmployeeService();
        }
        return employeeService;
    }

    public HR(String employeeID, String lastName, String firstName, String birthday,
              String address, String phoneNumber, double basicSalary, double hourlyRate,
              double grossSemiMonthlyRate, String status, String position,
              String immediateSupervisor, Allowance allowance,
              GovernmentDetails governmentDetails) {
        super(employeeID, lastName, firstName, birthday, address, phoneNumber,
              basicSalary, hourlyRate, grossSemiMonthlyRate, status, position,
              immediateSupervisor, allowance, governmentDetails);
    }

    public String getDepartment() { return "HR Department"; }

    @Override
    public double calculateGrossSalary() {
        return getBasicSalary() + getAllowance();
    }

    @Override
    public double calculateGrossSalary(double salary) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        return salary;
    }

    @Override
    public double calculateGrossSalary(double salary, double overtimeHours) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be > 0. Received: " + salary);
        }
        if (overtimeHours < 0) {
            throw new IllegalArgumentException("Overtime hours must be >= 0. Received: " + overtimeHours);
        }
        return salary + (overtimeHours * getHourlyRate() * 1.25);
    }

    @Override
    public double calculateOvertimePay(double overtimeHours) {
        return overtimeHours * getHourlyRate() * 1.25;
    }

    @Override
    public double calculateDeductions() {
        return PayrollCalculator.computeSSSDeduction(getBasicSalary())
             + PayrollCalculator.computePhilhealthDeduction(getBasicSalary())
             + PayrollCalculator.computePagibigDeduction(getBasicSalary())
             + PayrollCalculator.computeWithholdingTax(getBasicSalary());
    }

    @Override
    public double calculateNetSalary() {
        return calculateGrossSalary() - calculateDeductions();
    }

    @Override
    public boolean approveLeave(String leaveId, String remark) {
        try {
            LeaveRequest request = LeaveRequestReader.getLeaveById(leaveId);
            if (request == null || !"Pending".equalsIgnoreCase(request.getStatus())) {
                return false;
            }
            request.approve(getFullName());
            if (remark != null && !remark.isBlank()) {
                request.setRemark(remark);
            }
            LeaveRequestReader.updateLeaveRequest(request);

            long daysRequested = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            EmployeeLeaveTracker.updateLeaveBalance(
                    request.getEmployeeID(), request.getLeaveType(), (int) daysRequested);
            return true;

        } catch (IOException e) {
            System.err.println("HR.approveLeave failed for leaveId=" + leaveId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rejectLeave(String leaveId, String remark) {
        try {
            LeaveRequest request = LeaveRequestReader.getLeaveById(leaveId);
            if (request == null || !"Pending".equalsIgnoreCase(request.getStatus())) {
                return false;
            }
            request.reject(getFullName(), remark);
            LeaveRequestReader.updateLeaveRequest(request);
            return true;

        } catch (IOException e) {
            System.err.println("HR.rejectLeave failed for leaveId=" + leaveId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Employee viewEmployeeRecords(String employeeId) {
        return getEmployeeService().getEmployeeById(employeeId);
    }

    @Override
    public String toString() {
        return "HR {" + super.toString() + "}";
    }
}
