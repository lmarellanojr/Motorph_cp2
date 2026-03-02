package com.group33.cp2.motorph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory implementation of {@link PayrollDAO} for payroll record management.
 *
 * <p>Stores {@link Payroll} records in an internal list. Because this system does not
 * yet persist payroll data to disk, records exist only for the lifetime of the
 * application session. A future implementation can implement {@link PayrollDAO} with
 * CSV or database persistence without changing any caller that holds a
 * {@link PayrollDAO} reference.</p>
 *
 * <p><strong>OOP Pillars demonstrated:</strong></p>
 * <ul>
 *   <li><em>Abstraction</em> — implements {@link PayrollDAO}; callers depend on the
 *       interface contract, not on this in-memory implementation detail.</li>
 *   <li><em>Encapsulation</em> — the internal payroll list is {@code private}; callers
 *       retrieve a read-only view via {@link #getAllPayrolls()}.</li>
 * </ul>
 *
 * @author Group13
 * @version 2.0
 */
public class PayrollService implements PayrollDAO {

    /** In-memory store of all payroll records for the current session. */
    private final List<Payroll> payrollRecords = new ArrayList<>();

    // =========================================================================
    //  PayrollDAO interface implementation
    // =========================================================================

    /**
     * Adds a new payroll record to the in-memory store.
     *
     * @param payroll the {@link Payroll} to persist; must not be null
     * @return {@code true} if added successfully; {@code false} if {@code payroll} is null
     */
    @Override
    public boolean create(Payroll payroll) {
        if (payroll == null) {
            return false;
        }
        payrollRecords.add(payroll);
        return true;
    }

    /**
     * Retrieves a payroll record by its unique payroll ID.
     *
     * @param payrollID the unique identifier to search for
     * @return the matching {@link Payroll}, or {@code null} if not found
     */
    @Override
    public Payroll read(String payrollID) {
        if (payrollID == null || payrollID.isBlank()) {
            return null;
        }
        for (Payroll payroll : payrollRecords) {
            if (payroll.getPayrollID().equals(payrollID)) {
                return payroll;
            }
        }
        return null;
    }

    /**
     * Replaces an existing payroll record with an updated version.
     * The record to replace is identified by matching {@link Payroll#getPayrollID()}.
     *
     * @param payroll the {@link Payroll} with updated fields; must not be null
     * @return {@code true} if a matching record was found and replaced; {@code false} otherwise
     */
    @Override
    public boolean update(Payroll payroll) {
        if (payroll == null) {
            return false;
        }
        for (int i = 0; i < payrollRecords.size(); i++) {
            if (payrollRecords.get(i).getPayrollID().equals(payroll.getPayrollID())) {
                payrollRecords.set(i, payroll);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a payroll record by its unique payroll ID.
     *
     * @param payrollID the unique identifier of the payroll to remove
     * @return {@code true} if a record was found and removed; {@code false} if not found
     */
    @Override
    public boolean delete(String payrollID) {
        if (payrollID == null || payrollID.isBlank()) {
            return false;
        }
        return payrollRecords.removeIf(p -> p.getPayrollID().equals(payrollID));
    }

    // =========================================================================
    //  Additional query methods
    // =========================================================================

    /**
     * Returns a read-only view of all payroll records in the current session.
     * Use {@link #create(Payroll)} to add new records.
     *
     * @return unmodifiable list of all payroll records
     */
    public List<Payroll> getAllPayrolls() {
        return Collections.unmodifiableList(payrollRecords);
    }

    /**
     * Returns all payroll records for a given employee ID.
     *
     * @param employeeID the employee whose payroll records to retrieve
     * @return list of matching payroll records; empty if none found
     */
    public List<Payroll> getPayrollsByEmployeeId(String employeeID) {
        if (employeeID == null || employeeID.isBlank()) {
            return Collections.emptyList();
        }
        List<Payroll> result = new ArrayList<>();
        for (Payroll payroll : payrollRecords) {
            if (payroll.getEmployeeID().equals(employeeID)) {
                result.add(payroll);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
