package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object (DAO) contract for payroll CRUD operations.
 *
 * <p>Defines the standard create, read, update, and delete operations for
 * {@link Payroll} records. Implementations may use CSV files, databases,
 * or any other persistence mechanism.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface rather than on a specific storage implementation, enabling the persistence
 * strategy to be swapped without modifying business logic.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface PayrollDAO {

    /**
     * Persists a new payroll record.
     *
     * @param payroll the {@link Payroll} to create; must not be null
     * @return {@code true} if the payroll was created successfully; {@code false} otherwise
     */
    boolean create(Payroll payroll);

    /**
     * Retrieves a payroll record by unique ID.
     *
     * @param payrollID the unique identifier to search for
     * @return the matching {@link Payroll}, or {@code null} if not found
     */
    Payroll read(String payrollID);

    /**
     * Updates an existing payroll record.
     *
     * @param payroll the {@link Payroll} with updated fields; must not be null
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    boolean update(Payroll payroll);

    /**
     * Deletes a payroll record by unique ID.
     *
     * @param payrollID the unique identifier of the payroll to delete
     * @return {@code true} if deleted; {@code false} if not found or error
     */
    boolean delete(String payrollID);
}
