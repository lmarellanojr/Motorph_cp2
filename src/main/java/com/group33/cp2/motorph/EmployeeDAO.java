package com.group33.cp2.motorph;

/**
 * Data Access Object (DAO) contract for employee CRUD operations.
 *
 * <p>Defines the standard create, read, update, and delete operations for
 * {@link Employee} records. Implementations may use CSV files, databases,
 * or any other persistence mechanism.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface rather than on a specific storage implementation, enabling the persistence
 * strategy to be swapped without modifying business logic.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface EmployeeDAO {

    /**
     * Persists a new employee record.
     *
     * @param employee the {@link Employee} to create; must not be null
     * @return {@code true} if the employee was created successfully; {@code false} otherwise
     */
    boolean create(Employee employee);

    /**
     * Retrieves an employee record by unique ID.
     *
     * @param employeeID the unique identifier to search for
     * @return the matching {@link Employee}, or {@code null} if not found
     */
    Employee read(String employeeID);

    /**
     * Updates an existing employee record.
     *
     * @param employee the {@link Employee} with updated fields; must not be null
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    boolean update(Employee employee);

    /**
     * Deletes an employee record by unique ID.
     *
     * @param employeeID the unique identifier of the employee to delete
     * @return {@code true} if deleted; {@code false} if not found or error
     */
    boolean delete(String employeeID);
}
