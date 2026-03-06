package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.AllowanceDetailsReader;
import com.group33.cp2.motorph.dao.SalaryDetailsReader;
import com.group33.cp2.motorph.model.Allowance;
import com.group33.cp2.motorph.model.Salary;
import com.group33.cp2.motorph.model.SalaryDetails;

import java.io.IOException;

/**
 * Service that assembles a complete {@link SalaryDetails} record for a single employee.
 *
 * <p>Reads salary and allowance data from their respective CSVs, delegates all
 * deduction formulas to {@link PayrollCalculator}, and returns a single immutable
 * result object. Callers have zero knowledge of CSV layouts or deduction math.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The entire payroll pipeline
 * (CSV reads, gross computation, four deduction formulas, net calculation) is hidden
 * behind one method call: {@link #getSalaryDetails(String)}.</p>
 */
public class PayrollCalculatorService {

    private final SalaryDetailsReader salaryReader;
    private final AllowanceDetailsReader allowanceReader;

    /** Creates a service backed by fresh reader instances. */
    public PayrollCalculatorService() {
        this.salaryReader   = new SalaryDetailsReader();
        this.allowanceReader = new AllowanceDetailsReader();
    }

    /**
     * Computes and returns a full payroll breakdown for the given employee.
     *
     * @param employeeId the employee ID to look up (e.g. "10001")
     * @return a {@link SalaryDetails} record, or {@code null} if salary or allowance
     *         data is not found for the employee
     * @throws IOException if the underlying CSV files cannot be read
     */
    public SalaryDetails getSalaryDetails(String employeeId) throws IOException {
        Salary salary      = salaryReader.getSalary(employeeId);
        Allowance allowance = allowanceReader.getAllowance(employeeId);

        if (salary == null || allowance == null) {
            return null;
        }

        double basicSalary    = salary.getBasicSalary();
        double hourlyRate     = salary.getHourlyRate();

        double riceSubsidy    = allowance.getRiceAllowance();
        double phoneAllow     = allowance.getPhoneAllowance();
        double clothingAllow  = allowance.getClothingAllowance();
        double totalAllowances = allowance.getTotal();

        double grossSalary    = basicSalary + totalAllowances;

        double sss            = PayrollCalculator.computeSSSDeduction(basicSalary);
        double philHealth     = PayrollCalculator.computePhilhealthDeduction(basicSalary);
        double pagibig        = PayrollCalculator.computePagibigDeduction(basicSalary);
        double tax            = PayrollCalculator.computeWithholdingTax(basicSalary);

        double totalDeductions = sss + philHealth + pagibig + tax;
        double netSalary       = grossSalary - totalDeductions;

        return new SalaryDetails(
            grossSalary, netSalary, hourlyRate,
            riceSubsidy, phoneAllow, clothingAllow, totalAllowances,
            pagibig, philHealth, sss, tax, totalDeductions
        );
    }
}
