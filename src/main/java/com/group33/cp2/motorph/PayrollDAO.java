package com.group33.cp2.motorph;

// CRUD contract for payroll data access.
public interface PayrollDAO {
    boolean create(Payroll payroll);
    Payroll read(String payrollID);
    boolean update(Payroll payroll);
    boolean delete(String payrollID);
}
