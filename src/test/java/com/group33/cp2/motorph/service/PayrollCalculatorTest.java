package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.model.PeriodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for PayrollCalculator: SSS brackets, PhilHealth, Pag-IBIG cap, tax tiers, and divisors.
// Expected values derived by hand from documented business rules.
class PayrollCalculatorTest {

    private static final double DELTA = 0.001;

    // =========================================================================
    //  SSS Deduction Tests
    // =========================================================================

    @Test
    void sssDeduction_salaryAtLowestBracket_returns135() {
        // Salary 0.0 maps to bracket key 0.0 → PHP 135.00
        assertEquals(135.00, PayrollCalculator.computeSSSDeduction(0.0), DELTA);
    }

    @Test
    void sssDeduction_salaryJustBelow3250_returns135() {
        // 3249 < 3250, so floorEntry returns key 0.0 → PHP 135.00
        assertEquals(135.00, PayrollCalculator.computeSSSDeduction(3249.99), DELTA);
    }

    @Test
    void sssDeduction_salaryAt3250_returns157_50() {
        assertEquals(157.50, PayrollCalculator.computeSSSDeduction(3250.0), DELTA);
    }

    @Test
    void sssDeduction_salaryAt22250_returns1012_50() {
        assertEquals(1012.50, PayrollCalculator.computeSSSDeduction(22250.0), DELTA);
    }

    @Test
    void sssDeduction_salary22500_returns1012_50() {
        // 22500 is between keys 22250 and 22750 → floorEntry = 22250 → PHP 1012.50
        assertEquals(1012.50, PayrollCalculator.computeSSSDeduction(22500.0), DELTA);
    }

    @Test
    void sssDeduction_salaryAt24750_returns1125() {
        // Highest bracket key is 24750 → PHP 1125.00
        assertEquals(1125.00, PayrollCalculator.computeSSSDeduction(24750.0), DELTA);
    }

    @Test
    void sssDeduction_salaryAboveMaxBracket_capsAt1125() {
        // Any salary above 24750 still hits the floor at 24750 → PHP 1125.00
        assertEquals(1125.00, PayrollCalculator.computeSSSDeduction(50000.0), DELTA);
    }

    @ParameterizedTest(name = "salary={0} -> SSS={1}")
    @CsvSource({
        "3750.0,  180.00",
        "4250.0,  202.50",
        "9250.0,  427.50",
        "19750.0, 900.00",
        "20250.0, 922.50",
        "24250.0, 1102.50"
    })
    void sssDeduction_selectedBracketBoundaries(double salary, double expected) {
        assertEquals(expected, PayrollCalculator.computeSSSDeduction(salary), DELTA);
    }

    // =========================================================================
    //  PhilHealth Deduction Tests
    // =========================================================================

    @Test
    void philHealth_salaryBelow10000_returnsZero() {
        assertEquals(0.0, PayrollCalculator.computePhilhealthDeduction(9999.99), DELTA);
    }

    @Test
    void philHealth_salaryExactly10000_returns150() {
        // 10000 * 3% / 2 = 150.00
        assertEquals(150.00, PayrollCalculator.computePhilhealthDeduction(10000.0), DELTA);
    }

    @Test
    void philHealth_salary20000_returns300() {
        // 20000 * 0.03 / 2 = 300.00
        assertEquals(300.00, PayrollCalculator.computePhilhealthDeduction(20000.0), DELTA);
    }

    @Test
    void philHealth_salary30000_returns450() {
        assertEquals(450.00, PayrollCalculator.computePhilhealthDeduction(30000.0), DELTA);
    }

    @Test
    void philHealth_salary0_returnsZero() {
        assertEquals(0.0, PayrollCalculator.computePhilhealthDeduction(0.0), DELTA);
    }

    // =========================================================================
    //  Pag-IBIG Deduction Tests
    // =========================================================================

    @Test
    void pagibig_salary1000_applies1Percent() {
        // 1000 * 1% = 10.00
        assertEquals(10.00, PayrollCalculator.computePagibigDeduction(1000.0), DELTA);
    }

    @Test
    void pagibig_salaryExactly1500_applies1Percent() {
        // 1500 <= 1500, so 1% → 15.00
        assertEquals(15.00, PayrollCalculator.computePagibigDeduction(1500.0), DELTA);
    }

    @Test
    void pagibig_salary1501_applies2Percent() {
        // 1501 > 1500, so 2% → 30.02; not capped
        assertEquals(30.02, PayrollCalculator.computePagibigDeduction(1501.0), DELTA);
    }

    @Test
    void pagibig_salary5000_cappedAt100() {
        // 5000 * 2% = 100.00 — exactly at cap
        assertEquals(100.00, PayrollCalculator.computePagibigDeduction(5000.0), DELTA);
    }

    @Test
    void pagibig_salaryHighEnoughToExceedCap_cappedAt100() {
        // 100000 * 2% = 2000, capped at 100
        assertEquals(100.00, PayrollCalculator.computePagibigDeduction(100000.0), DELTA);
    }

    @Test
    void pagibig_salary4999_justBelowCapBoundary() {
        // 4999 * 2% = 99.98, not yet capped
        assertEquals(99.98, PayrollCalculator.computePagibigDeduction(4999.0), DELTA);
    }

    // =========================================================================
    //  Withholding Tax Tests
    // =========================================================================

    @Test
    void withholdingTax_taxableIncomeBelowFirstBracket_returnsZero() {
        // Use salary that produces taxable income < 20832
        // salary=20000: SSS=floorEntry(20000)=key19750=900.00, PH=300.00, PI=100.00
        // taxable = 20000 - 1300 = 18700 → < 20832 → tax = 0
        assertEquals(0.0, PayrollCalculator.computeWithholdingTax(20000.0), DELTA);
    }

    @Test
    void withholdingTax_tier2_20pctBracket() {
        // salary=30000: SSS=floorEntry(30000)=key24750=1125.00, PH=450.00, PI=100.00
        // taxable = 30000 - 1675 = 28325 → bracket [20832, 33333): (28325-20833)*0.20
        double salary = 30000.0;
        double sss = PayrollCalculator.computeSSSDeduction(salary);    // 1125.00
        double ph  = PayrollCalculator.computePhilhealthDeduction(salary); // 450.00
        double pi  = PayrollCalculator.computePagibigDeduction(salary);    // 100.00
        double taxable = salary - sss - ph - pi;
        double expectedTax = (taxable - 20833) * 0.20;
        assertEquals(expectedTax, PayrollCalculator.computeWithholdingTax(salary), DELTA);
    }

    @Test
    void withholdingTax_tier3_25pctBracket() {
        // salary=60000: SSS=1125, PH=900, PI=100 → taxable=57875
        // bracket [33333, 66667): 2500 + (57875-33333)*0.25
        double salary = 60000.0;
        double sss = PayrollCalculator.computeSSSDeduction(salary);
        double ph  = PayrollCalculator.computePhilhealthDeduction(salary);
        double pi  = PayrollCalculator.computePagibigDeduction(salary);
        double taxable = salary - sss - ph - pi;
        double expectedTax = 2500 + (taxable - 33333) * 0.25;
        assertEquals(expectedTax, PayrollCalculator.computeWithholdingTax(salary), DELTA);
    }

    @Test
    void withholdingTax_tier4_30pctBracket() {
        // salary=100000: SSS=1125, PH=1500, PI=100 → taxable=97275
        // bracket [66667, 166667): 10833 + (97275-66667)*0.30
        double salary = 100000.0;
        double sss = PayrollCalculator.computeSSSDeduction(salary);
        double ph  = PayrollCalculator.computePhilhealthDeduction(salary);
        double pi  = PayrollCalculator.computePagibigDeduction(salary);
        double taxable = salary - sss - ph - pi;
        double expectedTax = 10833 + (taxable - 66667) * 0.30;
        assertEquals(expectedTax, PayrollCalculator.computeWithholdingTax(salary), DELTA);
    }

    @Test
    void withholdingTax_tier5_32pctBracket() {
        // salary=200000: SSS=1125, PH=3000, PI=100 → taxable=195775
        // bracket [166667, 666667): 40833.33 + (195775-166667)*0.32
        double salary = 200000.0;
        double sss = PayrollCalculator.computeSSSDeduction(salary);
        double ph  = PayrollCalculator.computePhilhealthDeduction(salary);
        double pi  = PayrollCalculator.computePagibigDeduction(salary);
        double taxable = salary - sss - ph - pi;
        double expectedTax = 40833.33 + (taxable - 166667) * 0.32;
        assertEquals(expectedTax, PayrollCalculator.computeWithholdingTax(salary), DELTA);
    }

    @Test
    void withholdingTax_tier6_35pctBracket() {
        // salary=700000: SSS=1125, PH=10500, PI=100 → taxable=688275
        // bracket [666667, ...): 200833.33 + (688275-666667)*0.35
        double salary = 700000.0;
        // Note: salary > 500000 so Employee setter would reject it, but PayrollCalculator
        // itself has no such constraint — test the formula directly at this value
        double sss = PayrollCalculator.computeSSSDeduction(salary);
        double ph  = PayrollCalculator.computePhilhealthDeduction(salary);
        double pi  = PayrollCalculator.computePagibigDeduction(salary);
        double taxable = salary - sss - ph - pi;
        double expectedTax = 200833.33 + (taxable - 666667) * 0.35;
        assertEquals(expectedTax, PayrollCalculator.computeWithholdingTax(salary), DELTA);
    }

    // =========================================================================
    //  getSSSPhilhealthPagibig helper
    // =========================================================================

    @Test
    void getSSSPhilhealthPagibig_salary30000_sumMatchesIndividualComponents() {
        double salary = 30000.0;
        double expected = PayrollCalculator.computeSSSDeduction(salary)
                        + PayrollCalculator.computePhilhealthDeduction(salary)
                        + PayrollCalculator.computePagibigDeduction(salary);
        assertEquals(expected, PayrollCalculator.getSSSPhilhealthPagibig(salary), DELTA);
    }

    // =========================================================================
    //  Period Divisor Tests
    // =========================================================================

    @Test
    void getDivisor_weekly_returns4() {
        assertEquals(4, PayrollCalculator.getDivisor(PeriodType.WEEKLY));
    }

    @Test
    void getDivisor_biweekly_returns2() {
        assertEquals(2, PayrollCalculator.getDivisor(PeriodType.BIWEEKLY));
    }

    @Test
    void getDivisor_monthly_returns1() {
        assertEquals(1, PayrollCalculator.getDivisor(PeriodType.MONTHLY));
    }

    @Test
    void getDivisor_null_returns1() {
        // Null period type defaults to MONTHLY divisor (1)
        assertEquals(1, PayrollCalculator.getDivisor(null));
    }
}
