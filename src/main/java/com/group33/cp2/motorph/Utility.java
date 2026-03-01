package com.group33.cp2.motorph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class providing shared formatting helpers and date-related methods
 * used throughout the MotorPH Payroll System.
 *
 * <p><strong>Encapsulation (BP10):</strong> The {@link DecimalFormat} instances are
 * declared {@code private static final} to prevent external code from calling mutating
 * methods ({@code applyPattern()}, {@code setGroupingSize()}, etc.) on shared
 * formatting objects. Formatting is exposed only through static methods that return
 * a formatted {@code String}, hiding the implementation detail of which formatter
 * is used. Access is synchronized to guard against potential concurrent use.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class Utility {

    // BP10: private — DecimalFormat is mutable; hide it behind static formatting methods
    private static final DecimalFormat twoDecimalFormat = new DecimalFormat("#,##0.00");
    private static final DecimalFormat threeDecimalFormat = new DecimalFormat("0.000");
    private static final DecimalFormat phpFormat = new DecimalFormat("Php #,##0.00");

    /**
     * Formats a numeric value with two decimal places and thousands separators
     * (e.g., {@code 1234.5} → {@code "1,234.50"}).
     *
     * @param value the number to format
     * @return the formatted string
     */
    public static synchronized String formatTwoDecimal(double value) {
        return twoDecimalFormat.format(value);
    }

    /**
     * Formats a numeric value with three decimal places
     * (e.g., {@code 1.2345} → {@code "1.235"}).
     *
     * @param value the number to format
     * @return the formatted string
     */
    public static synchronized String formatThreeDecimal(double value) {
        return threeDecimalFormat.format(value);
    }

    /**
     * Formats a numeric value as a Philippine Peso amount
     * (e.g., {@code 1234.5} → {@code "Php 1,234.50"}).
     *
     * @param value the number to format
     * @return the formatted peso string
     */
    public static synchronized String formatPhp(double value) {
        return phpFormat.format(value);
    }

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
