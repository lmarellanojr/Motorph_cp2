package com.group33.cp2.motorph;

// CRUD contract for employee data access.
public interface EmployeeDAO {
    boolean create(Employee employee);
    Employee read(String employeeID);
    boolean update(Employee employee);
    boolean delete(String employeeID);
}
