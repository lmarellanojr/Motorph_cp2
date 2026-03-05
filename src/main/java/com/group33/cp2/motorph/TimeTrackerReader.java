package com.group33.cp2.motorph;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides clock-in/clock-out and time-log operations against {@code TimeTracker.csv}.
 *
 * <p>CSV columns: [0]=empNum, [1]=date (MM/dd/yyyy), [2]=timeIn (HH:mm),
 * [3]=timeOut (HH:mm, empty if not yet clocked out), [4]=hoursWorked (H:mm)</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> All file I/O is hidden behind
 * static methods. Callers never access the CSV file directly.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class TimeTrackerReader {

    private static final String FILE_PATH = "src/main/resources/data/TimeTracker.csv";
    private static final DateTimeFormatter CSV_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy,HH:mm");

    // =========================================================================
    //  Internal CSV helpers
    // =========================================================================

    private static List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in TimeTracker.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    private static void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeAll(rows);
        }
    }

    // =========================================================================
    //  Public API
    // =========================================================================

    /**
     * Records a clock-in entry for the given employee at the current timestamp.
     *
     * @param employeeId the employee number
     * @throws IOException if the file cannot be written
     */
    public static void clockIn(String employeeId) throws IOException {
        List<String[]> data = readAll();
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(CSV_DATE_TIME_FORMATTER);
        String[] parts = formattedDateTime.split(",");
        // columns: empNum, date, timeIn, timeOut (empty), hoursWorked (empty)
        data.add(new String[]{employeeId, parts[0], parts[1], "", ""});
        writeAll(data);
    }

    /**
     * Records the clock-out time for the most recent open clock-in entry for the given employee.
     * Also calculates and writes the hours worked.
     *
     * @param employeeId the employee number
     * @throws IOException if the file cannot be written or no open clock-in is found
     */
    public static void clockOut(String employeeId) throws IOException {
        List<String[]> data = readAll();
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(CSV_DATE_TIME_FORMATTER);
        String[] parts = formattedDateTime.split(",");
        String timeOutStr = parts[1];
        boolean updated = false;

        // Scan in reverse to find the most recent open clock-in
        for (int i = data.size() - 1; i >= 1; i--) {
            String[] row = data.get(i);
            if (row.length >= 4 && row[0].trim().equals(employeeId) && row[3].isBlank()) {
                row[3] = timeOutStr;
                // Calculate hours worked
                try {
                    LocalDateTime timeIn = LocalDateTime.parse(
                            row[1].trim() + "," + row[2].trim(), CSV_DATE_TIME_FORMATTER);
                    LocalDateTime timeOut = LocalDateTime.parse(
                            row[1].trim() + "," + timeOutStr, CSV_DATE_TIME_FORMATTER);
                    Duration duration = Duration.between(timeIn, timeOut);
                    long hours = duration.toHours();
                    long minutes = duration.toMinutes() % 60;
                    if (row.length > 4) {
                        row[4] = String.format("%d:%02d", hours, minutes);
                    }
                } catch (Exception e) {
                    System.err.println("TimeTrackerReader: error calculating duration — " + e.getMessage());
                    if (row.length > 4) {
                        row[4] = "Error";
                    }
                }
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAll(data);
        } else {
            throw new IOException("No active clock-in found for employee " + employeeId);
        }
    }

    /**
     * Returns all time-log rows for the given employee as raw String arrays.
     * Each array: [empNum, date, timeIn, timeOut, hoursWorked].
     *
     * @param employeeId the employee number to filter by
     * @return list of matching rows; empty list if none
     * @throws IOException if the file cannot be read
     */
    public static List<String[]> getTimeLogs(String employeeId) throws IOException {
        List<String[]> logs = new ArrayList<>();
        List<String[]> all = readAll();
        // skip header at index 0
        for (int i = 1; i < all.size(); i++) {
            String[] row = all.get(i);
            if (row.length >= 1 && row[0].trim().equals(employeeId.trim())) {
                // Normalise to exactly 5 elements
                String empNum    = row.length > 0 ? row[0].trim() : "";
                String date      = row.length > 1 ? row[1].trim() : "";
                String timeIn    = row.length > 2 ? row[2].trim() : "";
                String timeOut   = row.length > 3 ? row[3].trim() : "";
                String hoursWork = row.length > 4 ? row[4].trim() : "";
                logs.add(new String[]{empNum, date, timeIn, timeOut, hoursWork});
            }
        }
        return logs;
    }
}
