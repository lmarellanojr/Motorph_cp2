package com.group33.cp2.motorph;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides read/write access to {@code LeaveRequests.csv}.
 *
 * <p>File path is relative to the project root (working directory must be
 * {@code MO-IT110-OOP/Motorph_cp2/} when the app is run).</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> This class is the single gatekeeper
 * for the leave requests CSV. No other class opens this file directly.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class LeaveRequestReader {

    private static final String FILE_PATH = "src/main/resources/data/LeaveRequests.csv";

    // =========================================================================
    //  Internal CSV helpers
    // =========================================================================

    /** Reads all rows including the header row. */
    private static List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in LeaveRequests.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    /** Writes all rows (including header) back to the file. */
    private static void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeAll(rows);
        }
    }

    // =========================================================================
    //  Public API
    // =========================================================================

    /**
     * Appends a new leave request row to {@code LeaveRequests.csv}.
     *
     * @param leaveRequest the leave request to persist
     * @throws IOException if the file cannot be written
     */
    public static void addLeaveRequest(LeaveRequest leaveRequest) throws IOException {
        List<String[]> data = readAll();
        data.add(new String[]{
            leaveRequest.getLeaveID(),
            leaveRequest.getEmployeeID(),
            leaveRequest.getLeaveType(),
            leaveRequest.getDateRequest().toString(),
            leaveRequest.getStartDate().toString(),
            leaveRequest.getEndDate().toString(),
            leaveRequest.getReason(),
            leaveRequest.getStatus(),
            leaveRequest.getApprover() != null ? leaveRequest.getApprover() : "",
            leaveRequest.getDateResponded() != null ? leaveRequest.getDateResponded().toString() : "",
            leaveRequest.getRemark() != null ? leaveRequest.getRemark() : ""
        });
        writeAll(data);
    }

    /**
     * Finds and returns a leave request by its ID, or {@code null} if not found.
     *
     * @param leaveID the unique leave request identifier
     * @return the matching {@link LeaveRequest}, or {@code null}
     * @throws IOException if the file cannot be read
     */
    public static LeaveRequest getLeaveById(String leaveID) throws IOException {
        List<String[]> dataList = readAll();
        // skip header at index 0
        for (int i = 1; i < dataList.size(); i++) {
            String[] data = dataList.get(i);
            if (data.length > 0 && data[0].trim().equals(leaveID.trim())) {
                return parseRow(data);
            }
        }
        return null;
    }

    /**
     * Replaces the row whose leaveID matches {@code updatedLeave.getLeaveID()}.
     * Does nothing if no matching row is found.
     *
     * @param updatedLeave the leave request with updated fields
     * @throws IOException if the file cannot be written
     */
    public static void updateLeaveRequest(LeaveRequest updatedLeave) throws IOException {
        List<String[]> data = readAll();
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).length > 0 && data.get(i)[0].trim().equals(updatedLeave.getLeaveID().trim())) {
                data.set(i, new String[]{
                    updatedLeave.getLeaveID(),
                    updatedLeave.getEmployeeID(),
                    updatedLeave.getLeaveType(),
                    updatedLeave.getDateRequest().toString(),
                    updatedLeave.getStartDate().toString(),
                    updatedLeave.getEndDate().toString(),
                    updatedLeave.getReason(),
                    updatedLeave.getStatus(),
                    updatedLeave.getApprover() != null ? updatedLeave.getApprover() : "",
                    updatedLeave.getDateResponded() != null ? updatedLeave.getDateResponded().toString() : "",
                    updatedLeave.getRemark() != null ? updatedLeave.getRemark() : ""
                });
                break;
            }
        }
        writeAll(data);
    }

    /**
     * Returns all leave requests from the CSV (header row excluded).
     *
     * @return list of all {@link LeaveRequest} objects; empty list if none
     */
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        try {
            List<String[]> dataList = readAll();
            // start at 1 to skip header
            for (int i = 1; i < dataList.size(); i++) {
                String[] data = dataList.get(i);
                if (data.length >= 8) {
                    leaveRequests.add(parseRow(data));
                } else {
                    System.err.println("LeaveRequestReader: skipping invalid row (too few columns): "
                            + String.join(",", data));
                }
            }
        } catch (IOException e) {
            System.err.println("LeaveRequestReader.getAllLeaveRequests: " + e.getMessage());
        }
        return leaveRequests;
    }

    // =========================================================================
    //  Parsing helper
    // =========================================================================

    /**
     * Converts a CSV row (String[]) into a {@link LeaveRequest}.
     * Columns: [0]=leaveID, [1]=empNum, [2]=leaveType, [3]=dateRequest,
     *          [4]=startDate, [5]=endDate, [6]=reason, [7]=status,
     *          [8]=approver (optional), [9]=dateResponded (optional),
     *          [10]=remark (optional)
     */
    private static LeaveRequest parseRow(String[] data) {
        return new LeaveRequest(
            data[0].trim(),                                                          // leaveID
            data[1].trim(),                                                          // employeeID
            data[2].trim(),                                                          // leaveType
            LocalDate.parse(data[3].trim()),                                         // dateRequest
            LocalDate.parse(data[4].trim()),                                         // startDate
            LocalDate.parse(data[5].trim()),                                         // endDate
            data[6].trim(),                                                          // reason
            data[7].trim(),                                                          // status
            data.length > 8 && !data[8].isBlank() ? data[8].trim() : "",            // approver
            data.length > 9 && !data[9].isBlank() ? LocalDate.parse(data[9].trim()) : null, // dateResponded
            data.length > 10 ? data[10].trim() : ""                                 // remark
        );
    }
}
