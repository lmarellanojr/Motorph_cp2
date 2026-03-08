package com.group33.cp2.motorph.model;

import com.group33.cp2.motorph.service.PayrollCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests demonstrating polymorphic behaviour between RegularEmployee and ProbationaryEmployee.
 *
 * Key differences:
 *   - RegularEmployee: overtime eligible (1.25x); all 4 deductions (SSS+PH+PI+tax)
 *   - ProbationaryEmployee: no overtime; only 3 deductions (SSS+PH+PI, no withholding tax)
 */
class EmployeeTypeTest {

    private static final double DELTA = 0.01;
    private static final double TEST_SALARY = 30000.0;
    private static final double TEST_HOURLY = 178.57;

    private RegularEmployee regular;
    private ProbationaryEmployee probationary;

    @BeforeEach
    void setUp() {
        Allowance allowance = new Allowance("10001", 1500, 800, 500);
        GovernmentDetails gov = new GovernmentDetails("10001", "SSS001", "PH001", "TIN001", "PI001");

        regular = new RegularEmployee(
                "10001", "Dela Cruz", "Juan", "1990-01-15",
                "123 Main St", "09171234567",
                TEST_SALARY, TEST_HOURLY, 15000.0, "Regular", "Engineer",
                "Manager", allowance, gov);

        probationary = new ProbationaryEmployee(
                "10002", "Reyes", "Ana", "1995-06-20",
                "456 Ortigas Ave", "09281234567",
                TEST_SALARY, TEST_HOURLY, 15000.0, "Probationary", "Trainee",
                "Manager", allowance, gov);
    }

    // =========================================================================
    //  calculateGrossSalary
    // =========================================================================

    @Test
    void calculateGrossSalary_regular_returnsBasicSalaryPlusAllowance() {
        // Allowance: rice 1500 + phone 800 + clothing 500 = 2800
        assertEquals(TEST_SALARY + 2800.0, regular.calculateGrossSalary(), DELTA);
    }

    @Test
    void calculateGrossSalary_probationary_returnsBasicSalaryPlusAllowance() {
        // Allowance: rice 1500 + phone 800 + clothing 500 = 2800
        assertEquals(TEST_SALARY + 2800.0, probationary.calculateGrossSalary(), DELTA);
    }

    // =========================================================================
    //  calculateOvertimePay — key polymorphic difference
    // =========================================================================

    @Test
    void calculateOvertimePay_regular_positiveHours_returns125xRate() {
        double ot = regular.calculateOvertimePay(2.0);
        assertEquals(2.0 * TEST_HOURLY * 1.25, ot, DELTA);
    }

    @Test
    void calculateOvertimePay_regular_zeroHours_returnsZero() {
        assertEquals(0.0, regular.calculateOvertimePay(0.0), DELTA);
    }

    @Test
    void calculateOvertimePay_regular_negativeHours_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> regular.calculateOvertimePay(-1.0));
    }

    @Test
    void calculateOvertimePay_probationary_alwaysReturnsZero() {
        // Probationary employees are never eligible for overtime
        assertEquals(0.0, probationary.calculateOvertimePay(5.0), DELTA);
        assertEquals(0.0, probationary.calculateOvertimePay(0.0), DELTA);
    }

    // =========================================================================
    //  calculateDeductions — Regular includes withholding tax
    // =========================================================================

    @Test
    void calculateDeductions_regular_includesWithholdingTax() {
        double deductions = regular.calculateDeductions();
        double expectedWithTax = PayrollCalculator.computeSSSDeduction(TEST_SALARY)
                + PayrollCalculator.computePhilhealthDeduction(TEST_SALARY)
                + PayrollCalculator.computePagibigDeduction(TEST_SALARY)
                + PayrollCalculator.computeWithholdingTax(TEST_SALARY);
        assertEquals(expectedWithTax, deductions, DELTA);
    }

    @Test
    void calculateDeductions_probationary_excludesWithholdingTax() {
        double deductions = probationary.calculateDeductions();
        double expectedNoTax = PayrollCalculator.computeSSSDeduction(TEST_SALARY)
                + PayrollCalculator.computePhilhealthDeduction(TEST_SALARY)
                + PayrollCalculator.computePagibigDeduction(TEST_SALARY);
        assertEquals(expectedNoTax, deductions, DELTA);
    }

    @Test
    void calculateDeductions_regular_greaterThan_probationary() {
        // Regular employee at same salary always pays more (withholding tax added)
        assertTrue(regular.calculateDeductions() > probationary.calculateDeductions(),
                "Regular deductions should exceed probationary deductions at same salary");
    }

    // =========================================================================
    //  calculateNetSalary = grossSalary - deductions
    // =========================================================================

    @Test
    void calculateNetSalary_regular_isGrossMinusDeductions() {
        assertEquals(
                regular.calculateGrossSalary() - regular.calculateDeductions(),
                regular.calculateNetSalary(),
                DELTA);
    }

    @Test
    void calculateNetSalary_probationary_isGrossMinusDeductions() {
        assertEquals(
                probationary.calculateGrossSalary() - probationary.calculateDeductions(),
                probationary.calculateNetSalary(),
                DELTA);
    }

    // =========================================================================
    //  calculateGrossSalary overloads
    // =========================================================================

    @Test
    void calculateGrossSalary_withSalaryParam_regular_returnsSalary() {
        assertEquals(40000.0, regular.calculateGrossSalary(40000.0), DELTA);
    }

    @Test
    void calculateGrossSalary_withSalaryParam_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> regular.calculateGrossSalary(-1.0));
    }

    @Test
    void calculateGrossSalary_withSalaryAndOT_regular_includesOT() {
        double expected = 40000.0 + (2.0 * TEST_HOURLY * 1.25);
        assertEquals(expected, regular.calculateGrossSalary(40000.0, 2.0), DELTA);
    }

    @Test
    void calculateGrossSalary_withSalaryAndOT_probationary_ignoredOT() {
        // Probationary ignores overtime hours in this overload
        assertEquals(40000.0, probationary.calculateGrossSalary(40000.0, 5.0), DELTA);
    }

    @Test
    void calculateGrossSalary_withNegativeOT_regular_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> regular.calculateGrossSalary(30000.0, -1.0));
    }
}
