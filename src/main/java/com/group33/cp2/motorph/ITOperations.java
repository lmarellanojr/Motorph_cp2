package com.group33.cp2.motorph;

/**
 * Contract for IT department-specific operations in the MotorPH Payroll System.
 *
 * <p>Implemented by the {@link IT} department employee subclass. Defines the
 * system access-management operations exclusive to IT staff.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — the interface hides the
 * details of how access control is implemented behind a clear behavioural contract.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface ITOperations {

    /**
     * Grants or revokes system access for a user account.
     *
     * @param userId      the unique identifier of the user whose access is being managed
     * @param accessLevel the access level to assign — must be {@code "read"},
     *                    {@code "write"}, or {@code "admin"}
     * @return {@code true} if the access level was successfully changed; {@code false} otherwise
     */
    boolean manageSystemAccess(int userId, String accessLevel);
}
