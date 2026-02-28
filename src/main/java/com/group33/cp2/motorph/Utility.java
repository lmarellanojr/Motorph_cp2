package com.group33.cp2.motorph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class providing shared formatting constants and helper methods
 * used throughout the MotorPH Payroll System.
 *
 * @author Group13
 * @version 1.0
 */
public class Utility {

    /** Formats a number with two decimal places and thousands separators (e.g., 1,234.56). */
    public static final DecimalFormat twoDecimalFormat = new DecimalFormat("#,##0.00");

    /** Formats a number with three decimal places (e.g., 1.234). */
    public static final DecimalFormat threeDecimalFormat = new DecimalFormat("0.000");

    /** Formats a number as a Philippine Peso amount (e.g., Php 1,234.56). */
    public static final DecimalFormat phpFormat = new DecimalFormat("Php #,##0.00");

    /**
     * Rounds a double value to the specified number of decimal places using HALF_UP rounding.
     *
     * @param value  the value to round
     * @param places the number of decimal places (must be non-negative)
     * @return the rounded value
     * @throws IllegalArgumentException if {@code places} is negative
     */
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative");
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * Determines whether the given date falls on a Friday (end of work week).
     *
     * @param date the date to check
     * @return {@code true} if the date is a Friday; {@code false} otherwise
     */
    public static boolean isEndOfWeek(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    /**
     * Creates a {@link DateTimeFormatter} from the specified pattern string.
     *
     * @param pattern the date format pattern (e.g., {@code "MM/dd/yyyy"})
     * @return a {@code DateTimeFormatter} for the given pattern
     */
    public static DateTimeFormatter getDateFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }
}
