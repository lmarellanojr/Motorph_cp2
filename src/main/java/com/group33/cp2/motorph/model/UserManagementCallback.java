package com.group33.cp2.motorph.model;

/**
 * Callback interface used by {@link Admin#manageUsers(int, String, UserManagementCallback)}
 * to delegate UI frame creation back to the presentation layer.
 *
 * <p>This interface decouples the domain model ({@code Admin}) from the Swing
 * forms layer ({@code forms/}). The model signals <em>what</em> needs to happen
 * (open a create-user or update-user form); the {@code AdminDashboard} decides
 * <em>how</em> to open it.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The callback contract is expressed
 * as an interface so that any UI toolkit (Swing, JavaFX, headless test) can
 * provide its own implementation without changing the domain class.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public interface UserManagementCallback {

    /**
     * Called when the "create" action is requested.
     * The implementor is responsible for opening a new-employee form.
     */
    void onCreateUser();

    /**
     * Called when the "update" action is requested and the target employee exists.
     *
     * @param employeeId the ID of the employee to update; never {@code null}
     */
    void onUpdateUser(String employeeId);
}
