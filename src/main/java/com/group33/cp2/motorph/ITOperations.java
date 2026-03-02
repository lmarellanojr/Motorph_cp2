package com.group33.cp2.motorph;

/**
 * Contract for IT department-specific operations.
 * Implemented by IT employee subclass.
 */
public interface ITOperations {

    // grants or revokes system access; accessLevel must be "read", "write", or "admin"
    boolean manageSystemAccess(int userId, String accessLevel);
}
