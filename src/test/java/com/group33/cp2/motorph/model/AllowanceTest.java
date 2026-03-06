package com.group33.cp2.motorph.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Allowance.
 *
 * Business rules:
 *   - All three allowance amounts must be >= 0 (negative values rejected)
 *   - getTotal() returns the sum of all three components
 *   - Zero-arg constructor initialises all amounts to 0.0
 */
class AllowanceTest {

    private static final double DELTA = 0.001;

    // =========================================================================
    //  Constructor and zero-value default
    // =========================================================================

    @Test
    void defaultConstructor_allAmountsAreZero() {
        Allowance a = new Allowance();
        assertEquals(0.0, a.getRiceAllowance(), DELTA);
        assertEquals(0.0, a.getPhoneAllowance(), DELTA);
        assertEquals(0.0, a.getClothingAllowance(), DELTA);
    }

    @Test
    void defaultConstructor_totalIsZero() {
        Allowance a = new Allowance();
        assertEquals(0.0, a.getTotal(), DELTA);
    }

    @Test
    void paramConstructor_storesValues() {
        Allowance a = new Allowance("10001", 1500.0, 800.0, 500.0);
        assertEquals(1500.0, a.getRiceAllowance(), DELTA);
        assertEquals(800.0,  a.getPhoneAllowance(), DELTA);
        assertEquals(500.0,  a.getClothingAllowance(), DELTA);
    }

    // =========================================================================
    //  getTotal()
    // =========================================================================

    @Test
    void getTotal_sumOfThreeComponents() {
        Allowance a = new Allowance("10001", 1500.0, 800.0, 500.0);
        assertEquals(2800.0, a.getTotal(), DELTA);
    }

    @Test
    void getTotal_zeroPlusZeroPlusZero_returnsZero() {
        Allowance a = new Allowance("10001", 0.0, 0.0, 0.0);
        assertEquals(0.0, a.getTotal(), DELTA);
    }

    // =========================================================================
    //  setRiceAllowance — negative rejection
    // =========================================================================

    @Test
    void setRiceAllowance_zeroIsAccepted() {
        Allowance a = new Allowance();
        a.setRiceAllowance(0.0);
        assertEquals(0.0, a.getRiceAllowance(), DELTA);
    }

    @Test
    void setRiceAllowance_positiveIsAccepted() {
        Allowance a = new Allowance();
        a.setRiceAllowance(1500.0);
        assertEquals(1500.0, a.getRiceAllowance(), DELTA);
    }

    @Test
    void setRiceAllowance_negative_throwsIllegalArgumentException() {
        Allowance a = new Allowance();
        assertThrows(IllegalArgumentException.class, () -> a.setRiceAllowance(-0.01));
    }

    // =========================================================================
    //  setPhoneAllowance — negative rejection
    // =========================================================================

    @Test
    void setPhoneAllowance_zeroIsAccepted() {
        Allowance a = new Allowance();
        a.setPhoneAllowance(0.0);
        assertEquals(0.0, a.getPhoneAllowance(), DELTA);
    }

    @Test
    void setPhoneAllowance_negative_throwsIllegalArgumentException() {
        Allowance a = new Allowance();
        assertThrows(IllegalArgumentException.class, () -> a.setPhoneAllowance(-100.0));
    }

    // =========================================================================
    //  setClothingAllowance — negative rejection
    // =========================================================================

    @Test
    void setClothingAllowance_zeroIsAccepted() {
        Allowance a = new Allowance();
        a.setClothingAllowance(0.0);
        assertEquals(0.0, a.getClothingAllowance(), DELTA);
    }

    @Test
    void setClothingAllowance_negative_throwsIllegalArgumentException() {
        Allowance a = new Allowance();
        assertThrows(IllegalArgumentException.class, () -> a.setClothingAllowance(-500.0));
    }

    // =========================================================================
    //  Total reflects updates via setters
    // =========================================================================

    @Test
    void getTotal_afterSetterUpdates_reflectsNewValues() {
        Allowance a = new Allowance("10001", 1000.0, 500.0, 200.0);
        a.setRiceAllowance(2000.0);
        assertEquals(2700.0, a.getTotal(), DELTA);
    }
}
