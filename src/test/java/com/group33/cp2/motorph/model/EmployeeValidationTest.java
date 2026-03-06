package com.group33.cp2.motorph.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Employee validated setters.
 *
 * Uses RegularEmployee as the concrete subclass since Employee is abstract.
 *
 * Validated fields:
 *   - basicSalary:   > 0, <= 500,000
 *   - hourlyRate:    > 0, <= 5,000
 *   - firstName:     non-blank
 *   - lastName:      non-blank
 *   - address:       non-null, <= 200 chars
 *   - phoneNumber:   non-blank
 *   - employeeID:    non-blank
 *   - status:        whitelist (Regular/Probationary/Active/Inactive/On Leave/Terminated)
 */
class EmployeeValidationTest {

    private RegularEmployee employee;

    /** Builds a valid RegularEmployee used as base for mutation tests. */
    @BeforeEach
    void setUp() {
        Allowance allowance = new Allowance("10001", 1500, 800, 500);
        GovernmentDetails gov = new GovernmentDetails("10001", "SSS001", "PH001", "TIN001", "PI001");
        employee = new RegularEmployee(
                "10001", "Dela Cruz", "Juan", "1990-01-15",
                "123 Main St, Makati City", "09171234567",
                25000.0, 148.44, 12500.0, "Regular", "Software Engineer",
                "Maria Santos", allowance, gov);
    }

    // =========================================================================
    //  basicSalary validation
    // =========================================================================

    @Test
    void setBasicSalary_positiveValue_accepted() {
        employee.setBasicSalary(30000.0);
        assertEquals(30000.0, employee.getBasicSalary(), 0.001);
    }

    @Test
    void setBasicSalary_maxBoundary_accepted() {
        employee.setBasicSalary(500_000.0);
        assertEquals(500_000.0, employee.getBasicSalary(), 0.001);
    }

    @Test
    void setBasicSalary_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setBasicSalary(0.0));
    }

    @Test
    void setBasicSalary_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setBasicSalary(-1000.0));
    }

    @Test
    void setBasicSalary_aboveMax_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setBasicSalary(500_001.0));
    }

    // =========================================================================
    //  hourlyRate validation
    // =========================================================================

    @Test
    void setHourlyRate_validValue_accepted() {
        employee.setHourlyRate(200.0);
        assertEquals(200.0, employee.getHourlyRate(), 0.001);
    }

    @Test
    void setHourlyRate_maxBoundary_accepted() {
        employee.setHourlyRate(5000.0);
        assertEquals(5000.0, employee.getHourlyRate(), 0.001);
    }

    @Test
    void setHourlyRate_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setHourlyRate(0.0));
    }

    @Test
    void setHourlyRate_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setHourlyRate(-50.0));
    }

    @Test
    void setHourlyRate_aboveMax_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setHourlyRate(5001.0));
    }

    // =========================================================================
    //  firstName validation
    // =========================================================================

    @Test
    void setFirstName_validName_accepted() {
        employee.setFirstName("Maria");
        assertEquals("Maria", employee.getFirstName());
    }

    @Test
    void setFirstName_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setFirstName(null));
    }

    @Test
    void setFirstName_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setFirstName("   "));
    }

    @Test
    void setFirstName_emptyString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setFirstName(""));
    }

    // =========================================================================
    //  lastName validation
    // =========================================================================

    @Test
    void setLastName_validName_accepted() {
        employee.setLastName("Santos");
        assertEquals("Santos", employee.getLastName());
    }

    @Test
    void setLastName_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setLastName(null));
    }

    @Test
    void setLastName_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setLastName(""));
    }

    // =========================================================================
    //  address validation
    // =========================================================================

    @Test
    void setAddress_validAddress_accepted() {
        employee.setAddress("456 Rizal Ave, Quezon City");
        assertEquals("456 Rizal Ave, Quezon City", employee.getAddress());
    }

    @Test
    void setAddress_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setAddress(null));
    }

    @Test
    void setAddress_exactly200Chars_accepted() {
        String addr = "A".repeat(200);
        employee.setAddress(addr);
        assertEquals(200, employee.getAddress().length());
    }

    @Test
    void setAddress_201Chars_throwsIllegalArgumentException() {
        String addr = "A".repeat(201);
        assertThrows(IllegalArgumentException.class, () -> employee.setAddress(addr));
    }

    // =========================================================================
    //  phoneNumber validation
    // =========================================================================

    @Test
    void setPhoneNumber_validNumber_accepted() {
        employee.setPhoneNumber("09281234567");
        assertEquals("09281234567", employee.getPhoneNumber());
    }

    @Test
    void setPhoneNumber_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setPhoneNumber(null));
    }

    @Test
    void setPhoneNumber_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setPhoneNumber("  "));
    }

    // =========================================================================
    //  employeeID validation
    // =========================================================================

    @Test
    void setEmployeeID_valid_accepted() {
        employee.setEmployeeID("99999");
        assertEquals("99999", employee.getEmployeeID());
    }

    @Test
    void setEmployeeID_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setEmployeeID(null));
    }

    @Test
    void setEmployeeID_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setEmployeeID(""));
    }

    // =========================================================================
    //  status validation — whitelist
    // =========================================================================

    @ParameterizedTest(name = "status=''{0}'' is valid")
    @ValueSource(strings = {"Regular", "Probationary", "Active", "Inactive", "On Leave", "Terminated"})
    void setStatus_validStatus_accepted(String status) {
        employee.setStatus(status);
        assertEquals(status, employee.getStatus());
    }

    @Test
    void setStatus_invalidValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setStatus("Fired"));
    }

    @Test
    void setStatus_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setStatus(null));
    }

    @Test
    void setStatus_caseSensitive_invalidCase_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.setStatus("regular"));
    }

    // =========================================================================
    //  getFullName
    // =========================================================================

    @Test
    void getFullName_returnsFirstSpaceLast() {
        assertEquals("Juan Dela Cruz", employee.getFullName());
    }

    // =========================================================================
    //  addAttendance / addPayslip null guard
    // =========================================================================

    @Test
    void addAttendance_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.addAttendance(null));
    }

    @Test
    void addPayslip_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employee.addPayslip(null));
    }

    @Test
    void getAttendanceList_isUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> employee.getAttendanceList().add(null));
    }
}
