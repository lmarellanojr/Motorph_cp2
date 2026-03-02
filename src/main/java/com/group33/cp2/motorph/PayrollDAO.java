package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object (DAO) contract for payroll record operations.
 *
 * <p>Defines the retrieval and persistence operations for {@link Payroll} records.
 * Implementations may use CSV files, databases, or any other persistence mechanism.</p>
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — the interface hides the
 * persistence mechanism, allowing business logic layers to query payroll data without
 * depending on a specific storage implementation.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface PayrollDAO {

    /**
     * Retrieves a single payroll record by its unique payroll ID.
     *
     * @param payrollId the unique payroll identifier
     * @return the matching {@link Payroll}, or {@code null} if not found
     */
    Payroll getPayrollById(String payrollId);

    /**
     * Retrieves all payroll records for a specific employee.
     *
     * @param employeeId the unique identifier of the employee
     * @return a list of {@link Payroll} records; never {@code null} (empty if none found)
     */
    List<Payroll> getPayrollsByEmployeeId(String employeeId);

    /**
     * Retrieves all payroll records whose period falls within the given date range.
     *
     * @param startDate the inclusive start of the period range
     * @param endDate   the inclusive end of the period range
     * @return a list of matching {@link Payroll} records; never {@code null}
     */
    List<Payroll> getPayrollsByPeriod(LocalDate startDate, LocalDate endDate);

    /**
     * Persists a payroll record (insert or update).
     *
     * @param payroll the {@link Payroll} to save; must not be null
     * @return {@code true} if saved successfully; {@code false} otherwise
     */
    boolean save(Payroll payroll);
}
