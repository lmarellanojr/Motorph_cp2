package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.Employee;

/**
 * Data Access Object (DAO) contract for employee CRUD operations.
 *
 * <p><strong>OOP Pillar demonstrated:</strong> Abstraction — callers depend on this
 * interface rather than on a specific storage implementation, enabling the persistence
 * strategy to be swapped without modifying business logic.</p>
 *
 * @author Group 33
 * @version 2.0
 */
public interface EmployeeDAO {

    boolean create(Employee employee);
    Employee read(String employeeID);
    boolean update(Employee employee);
    boolean delete(String employeeID);
}
