package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.Payroll;

/**
 * Data Access Object (DAO) contract for payroll CRUD operations.
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface rather than on a specific storage implementation, enabling the persistence
 * strategy to be swapped without modifying business logic.</p>
 *
 * @author Group13
 * @version 2.0
 */
public interface PayrollDAO {

    boolean create(Payroll payroll);
    Payroll read(String payrollID);
    boolean update(Payroll payroll);
    boolean delete(String payrollID);
}
