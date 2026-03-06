package com.group33.cp2.motorph.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Utility helper methods.
 *
 * Covers:
 *   - round() — HALF_UP rounding with BigDecimal
 *   - formatTwoDecimal() — thousands-grouped, 2 decimal places
 *   - formatThreeDecimal() — 3 decimal places
 *   - formatPhp() — PHP currency prefix
 *   - isEndOfWeek() — Friday detection
 */
class UtilityTest {

    // =========================================================================
    //  round()
    // =========================================================================

    @Test
    void round_twoDecimalPlaces_halfUp() {
        assertEquals(1.24, Utility.round(1.235, 2), 0.0001);
    }

    @Test
    void round_zeroDecimalPlaces_wholePart() {
        assertEquals(3.0, Utility.round(3.4999, 0), 0.0001);
    }

    @Test
    void round_halfUp_roundsAwayFromZero() {
        assertEquals(1.25, Utility.round(1.245, 2), 0.0001);
    }

    @Test
    void round_negativeDecimalPlaces_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Utility.round(10.0, -1));
    }

    @Test
    void round_zero_returnsZero() {
        assertEquals(0.0, Utility.round(0.0, 2), 0.0001);
    }

    // =========================================================================
    //  formatTwoDecimal()
    // =========================================================================

    @ParameterizedTest(name = "{0} formats to ''{1}''")
    @CsvSource({
        "1234.5,   '1,234.50'",
        "0.0,      '0.00'",
        "1000000,  '1,000,000.00'",
        "0.12345,  '0.12'"   // rounds half-up at 2 decimal places
    })
    void formatTwoDecimal_variousValues(double input, String expected) {
        assertEquals(expected, Utility.formatTwoDecimal(input));
    }

    // =========================================================================
    //  formatThreeDecimal()
    // =========================================================================

    @Test
    void formatThreeDecimal_threeDecimalPlaces() {
        // DecimalFormat with pattern "0.000" uses HALF_DOWN by default:
        // 1.2345 → the fourth decimal (5) triggers HALF_DOWN on the third → 1.234
        assertEquals("1.234", Utility.formatThreeDecimal(1.2345));
    }

    @Test
    void formatThreeDecimal_zero() {
        assertEquals("0.000", Utility.formatThreeDecimal(0.0));
    }

    // =========================================================================
    //  formatPhp()
    // =========================================================================

    @Test
    void formatPhp_includesPhpPrefix() {
        String result = Utility.formatPhp(1234.5);
        assertTrue(result.startsWith("Php"), "Result should start with 'Php'");
    }

    @Test
    void formatPhp_includesFormattedAmount() {
        String result = Utility.formatPhp(1234.5);
        assertTrue(result.contains("1,234.50"), "Result should contain formatted amount");
    }

    // =========================================================================
    //  isEndOfWeek()
    // =========================================================================

    @Test
    void isEndOfWeek_friday_returnsTrue() {
        // 2025-01-10 is a Friday
        LocalDate friday = LocalDate.of(2025, 1, 10);
        assertTrue(Utility.isEndOfWeek(friday));
    }

    @Test
    void isEndOfWeek_monday_returnsFalse() {
        LocalDate monday = LocalDate.of(2025, 1, 6);
        assertFalse(Utility.isEndOfWeek(monday));
    }

    @Test
    void isEndOfWeek_saturday_returnsFalse() {
        LocalDate saturday = LocalDate.of(2025, 1, 11);
        assertFalse(Utility.isEndOfWeek(saturday));
    }

    @Test
    void isEndOfWeek_thursday_returnsFalse() {
        LocalDate thursday = LocalDate.of(2025, 1, 9);
        assertFalse(Utility.isEndOfWeek(thursday));
    }
}
