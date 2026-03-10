package com.group33.cp2.motorph.model;

/**
 * Contract for IT department-specific operations in the MotorPH Payroll System.
 *
 * <p>Implemented by the {@link IT} department employee subclass. Defines the
 * system access-management operations exclusive to IT staff.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — the interface hides the
 * details of how access control is implemented behind a clear behavioural contract.</p>
 *
 * @author Group 33
 * @version 2.0
 */
public interface ITOperations {

    boolean manageSystemAccess(int userId, String accessLevel);
}
