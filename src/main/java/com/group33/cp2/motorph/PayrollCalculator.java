package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Utility class for computing payroll-related values including government
 * deductions, withholding tax, and week-based logic for bi-weekly payroll periods.
 *
 * @author Group13
 * @version 1.0
 */
public class PayrollCalculator {

    /**
     * Returns the week-of-month number for the given date, using the default locale.
     *
     * @param date the date to inspect
     * @return week number within the month (1-based)
     */
    public static int getWeekOfMonth(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.get(weekFields.weekOfMonth());
    }

    /**
     * Checks whether week 1 of the month containing {@code date} overlaps with
     * the previous month (i.e., the 1st falls mid-week).
     *
     * @param date any date within the month to check
     * @return {@code true} if week 1 starts in the previous month
     */
    public static boolean isWeek1OverlappingPreviousMonth(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int weekNumberOfFirstDay = firstDayOfMonth.get(weekFields.weekOfMonth());
        return weekNumberOfFirstDay != 1;
    }

    /**
     * Returns {@code true} if the month containing {@code date} spans five calendar weeks.
     *
     * @param date any date within the month to check
     * @return {@code true} if the month has five or more weeks
     */
    public static boolean hasFiveWeeks(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());
        int firstWeek = getWeekOfMonth(firstDay);
        int lastWeek = getWeekOfMonth(lastDay);
        return lastWeek - firstWeek + 1 >= 5;
    }

    /**
     * Returns the divisor used to pro-rate deductions for the given period type.
     *
     * @param periodType the payroll period type
     * @return 4 for WEEKLY, 2 for BIWEEKLY, 1 for MONTHLY or null
     */
    public static int getDivisor(PeriodType periodType) {
        if (periodType == null) {
            return 1;
        }
        return switch (periodType) {
            case WEEKLY -> 4;
            case BIWEEKLY -> 2;
            default -> 1;
        };
    }

    /**
     * Calculates deductions for a BIWEEKLY payroll period.
     * Deductions are only applied on the 2nd and 4th weeks of a 4-week month,
     * or the 3rd and 5th weeks of a 5-week month.
     *
     * @param employeeID   the employee's unique identifier
     * @param basicSalary  the employee's basic monthly salary
     * @param periodStart  the start date of the payroll period
     * @return a {@link Deductions} object with computed amounts, or zero deductions if not applicable
     */
    public static Deductions getDeductionsBiWeekly(String employeeID, double basicSalary, LocalDate periodStart) {
        int weekOfMonth = getWeekOfMonth(periodStart);
        boolean isFiveWeekMonth = hasFiveWeeks(periodStart);
        int divisor = getDivisor(PeriodType.BIWEEKLY);

        boolean apply = (isFiveWeekMonth && (weekOfMonth == 3 || weekOfMonth == 5))
                     || (!isFiveWeekMonth && (weekOfMonth == 2 || weekOfMonth == 4));

        if (apply) {
            double sss = computeSSSDeduction(basicSalary) / divisor;
            double philhealth = computePhilhealthDeduction(basicSalary) / divisor;
            double pagibig = computePagibigDeduction(basicSalary) / divisor;
            double tax = computeWithholdingTax(basicSalary) / divisor;
            return new Deductions(employeeID, sss, philhealth, pagibig, tax);
        }

        return new Deductions(); // Zero deductions outside the deduction period
    }

    /**
     * Calculates full monthly deductions for the given employee and salary.
     *
     * @param employeeID   the employee's unique identifier
     * @param basicSalary  the employee's basic monthly salary
     * @param periodStart  the start date of the payroll period (unused but kept for API consistency)
     * @return a {@link Deductions} object with full monthly amounts
     */
    public static Deductions getDeductionsMonthly(String employeeID, double basicSalary, LocalDate periodStart) {
        int divisor = getDivisor(PeriodType.MONTHLY);
        double sss = computeSSSDeduction(basicSalary) / divisor;
        double philhealth = computePhilhealthDeduction(basicSalary) / divisor;
        double pagibig = computePagibigDeduction(basicSalary) / divisor;
        double tax = computeWithholdingTax(basicSalary) / divisor;
        return new Deductions(employeeID, sss, philhealth, pagibig, tax);
    }

    /**
     * Returns the combined total of SSS, PhilHealth, and Pag-IBIG contributions.
     *
     * @param basicSalary the employee's basic monthly salary
     * @return total mandatory contributions (excluding withholding tax)
     */
    public static double getSSSPhilhealthPagibig(double basicSalary) {
        return computeSSSDeduction(basicSalary)
             + computePhilhealthDeduction(basicSalary)
             + computePagibigDeduction(basicSalary);
    }

    /**
     * Computes the monthly withholding tax using the BIR progressive tax bracket.
     * Taxable income is calculated as basic salary minus SSS, PhilHealth, and Pag-IBIG.
     *
     * @param basicSalary the employee's basic monthly salary
     * @return the computed withholding tax amount
     */
    public static double computeWithholdingTax(double basicSalary) {
        double deductions = getSSSPhilhealthPagibig(basicSalary);
        double taxableIncome = basicSalary - deductions;

        if (taxableIncome < 20832) {
            return 0;
        } else if (taxableIncome < 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome < 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    /**
     * Computes the employee's Pag-IBIG contribution.
     * Rate is 1% for salaries between PHP 1,000 and 1,500; 2% above PHP 1,500.
     * Contribution is capped at PHP 100.
     *
     * @param basicSalary the employee's basic monthly salary
     * @return the Pag-IBIG employee contribution, capped at PHP 100
     */
    public static double computePagibigDeduction(double basicSalary) {
        double rate = (basicSalary <= 1500) ? 0.01 : 0.02;
        return Math.min(basicSalary * rate, 100);
    }

    /**
     * Computes the employee's share of the PhilHealth premium contribution.
     * Premium rate is 3% of salary; employee pays half. No contribution if salary is below PHP 10,000.
     *
     * @param basicSalary the employee's basic monthly salary
     * @return the PhilHealth employee contribution
     */
    public static double computePhilhealthDeduction(double basicSalary) {
        return (basicSalary >= 10000) ? (basicSalary * 0.03) / 2 : 0;
    }

    /**
     * Retrieves the SSS employee contribution using a floor-entry lookup against the bracket table.
     *
     * @param basicSalary the employee's basic monthly salary
     * @return the SSS contribution amount from the bracket table
     */
    public static double computeSSSDeduction(double basicSalary) {
        return SSSDeductionsBracket.getDeductions()
                                   .floorEntry(basicSalary)
                                   .getValue();
    }
}
