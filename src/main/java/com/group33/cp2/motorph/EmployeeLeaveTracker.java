package com.group33.cp2.motorph;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages leave balance data stored in {@code LeaveBalances.csv}.
 *
 * <p>CSV columns: [0]=empNum, [1]=lastName, [2]=firstName,
 * [3]=sickLeave, [4]=vacationLeave, [5]=birthdayLeave</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> All file I/O is hidden behind
 * static methods. Callers never touch the file path directly.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class EmployeeLeaveTracker {

    private static final String LEAVE_BALANCES_FILE = "src/main/resources/data/LeaveBalances.csv";

    // Supported leave type identifiers
    private static final String SICK_LEAVE     = "Sick Leave";
    private static final String VACATION_LEAVE = "Vacation Leave";
    private static final String BIRTHDAY_LEAVE = "Birthday Leave";

    // CSV column indices for leave balance columns
    private static final int COL_SICK_LEAVE     = 3;
    private static final int COL_VACATION_LEAVE = 4;
    private static final int COL_BIRTHDAY_LEAVE = 5;

    // =========================================================================
    //  Internal CSV helpers
    // =========================================================================

    private static List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(LEAVE_BALANCES_FILE))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in LeaveBalances.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    private static void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(LEAVE_BALANCES_FILE))) {
            writer.writeAll(rows);
        }
    }

    // =========================================================================
    //  Public API
    // =========================================================================

    /**
     * Returns the remaining leave balance for the given employee and leave type.
     *
     * @param employeeId the employee number to look up
     * @param leaveType  one of "Sick Leave", "Vacation Leave", or "Birthday Leave"
     * @return remaining days; 0 if not found or leave type not recognised
     * @throws IOException if the file cannot be read
     */
    public static int getLeaveBalance(String employeeId, String leaveType) throws IOException {
        List<String[]> rows = readAll();
        for (String[] row : rows) {
            if (row.length > 0 && row[0].trim().equals(employeeId.trim())) {
                return switch (leaveType) {
                    case SICK_LEAVE     -> row.length > COL_SICK_LEAVE     ? parseInt(row[COL_SICK_LEAVE])     : 0;
                    case VACATION_LEAVE -> row.length > COL_VACATION_LEAVE ? parseInt(row[COL_VACATION_LEAVE]) : 0;
                    case BIRTHDAY_LEAVE -> row.length > COL_BIRTHDAY_LEAVE ? parseInt(row[COL_BIRTHDAY_LEAVE]) : 0;
                    default             -> 0;
                };
            }
        }
        return 0;
    }

    /**
     * Deducts {@code daysToDeduct} from the balance of the given leave type for the employee.
     * Does nothing if the employee is not found or the leave type is not recognised.
     *
     * @param employeeId   the employee number
     * @param leaveType    one of "Sick Leave", "Vacation Leave", or "Birthday Leave"
     * @param daysToDeduct number of days to subtract from the balance
     * @throws IOException if the file cannot be written
     */
    public static void updateLeaveBalance(String employeeId, String leaveType, int daysToDeduct)
            throws IOException {
        List<String[]> rows = readAll();
        for (String[] row : rows) {
            if (row.length > 0 && row[0].trim().equals(employeeId.trim())) {
                int colIdx = columnIndexFor(leaveType);
                if (colIdx >= 0 && row.length > colIdx) {
                    int current = parseInt(row[colIdx]);
                    row[colIdx] = String.valueOf(current - daysToDeduct);
                }
                break;
            }
        }
        writeAll(rows);
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    /** Returns the CSV column index for the given leave type, or -1 if not recognised. */
    private static int columnIndexFor(String leaveType) {
        return switch (leaveType) {
            case SICK_LEAVE     -> COL_SICK_LEAVE;
            case VACATION_LEAVE -> COL_VACATION_LEAVE;
            case BIRTHDAY_LEAVE -> COL_BIRTHDAY_LEAVE;
            default             -> -1;
        };
    }

    /** Parses an integer from a CSV cell, returning 0 on parse failure. */
    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
