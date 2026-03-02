package com.group33.cp2.motorph;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Computes government deductions, withholding tax, and bi-weekly period logic.
 */
public class PayrollCalculator {

    public static int getWeekOfMonth(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.get(weekFields.weekOfMonth());
    }

    // returns true if week 1 of the month bleeds into the previous month
    public static boolean isWeek1OverlappingPreviousMonth(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int weekNumberOfFirstDay = firstDayOfMonth.get(weekFields.weekOfMonth());
        return weekNumberOfFirstDay != 1;
    }

    public static boolean hasFiveWeeks(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());
        int firstWeek = getWeekOfMonth(firstDay);
        int lastWeek = getWeekOfMonth(lastDay);
        return lastWeek - firstWeek + 1 >= 5;
    }

    // WEEKLY=4, BIWEEKLY=2, MONTHLY=1
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

    // applies deductions on weeks 2&4 (4-week month) or weeks 3&5 (5-week month)
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

        return new Deductions(); // no deductions this week
    }

    public static Deductions getDeductionsMonthly(String employeeID, double basicSalary, LocalDate periodStart) {
        int divisor = getDivisor(PeriodType.MONTHLY);
        double sss = computeSSSDeduction(basicSalary) / divisor;
        double philhealth = computePhilhealthDeduction(basicSalary) / divisor;
        double pagibig = computePagibigDeduction(basicSalary) / divisor;
        double tax = computeWithholdingTax(basicSalary) / divisor;
        return new Deductions(employeeID, sss, philhealth, pagibig, tax);
    }

    // used as the deductions base when computing taxable income
    public static double getSSSPhilhealthPagibig(double basicSalary) {
        return computeSSSDeduction(basicSalary)
             + computePhilhealthDeduction(basicSalary)
             + computePagibigDeduction(basicSalary);
    }

    // BIR 6-tier progressive tax; taxable income = salary minus SSS/PhilHealth/PagIbig
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

    // 1% if salary <= 1500, 2% if above; capped at PHP 100
    public static double computePagibigDeduction(double basicSalary) {
        double rate = (basicSalary <= 1500) ? 0.01 : 0.02;
        return Math.min(basicSalary * rate, 100);
    }

    // 3% premium split in half; no contribution below PHP 10,000
    public static double computePhilhealthDeduction(double basicSalary) {
        return (basicSalary >= 10000) ? (basicSalary * 0.03) / 2 : 0;
    }

    // looks up the SSS contribution from the bracket table
    public static double computeSSSDeduction(double basicSalary) {
        return SSSDeductionsBracket.getDeductions()
                                   .floorEntry(basicSalary)
                                   .getValue();
    }
}
