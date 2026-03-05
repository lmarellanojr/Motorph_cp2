package com.group33.cp2.motorph.data;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns read/write access to {@code Employee.csv} and read/write access to
 * {@code Login.csv}.
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> This class is the single point of
 * truth for the Employee and Login files. No other class opens these files directly.</p>
 *
 * <p>File paths are relative to the project root (working directory must be
 * {@code MO-IT110-OOP/Motorph_cp2/} when the app is run).</p>
 */
public class EmployeeDetailsReader {

    private static final String EMP_CSV   = "src/main/resources/data/Employee.csv";
    private static final String LOGIN_CSV = "src/main/resources/data/Login.csv";

    // -------------------------------------------------------------------------
    //  Internal helpers
    // -------------------------------------------------------------------------

    /** Reads every row (including header) from a CSV file. */
    private List<String[]> readAll(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in " + path + ": " + e.getMessage(), e);
        }
        return rows;
    }

    /** Writes all rows (including header) to a CSV file. */
    private void writeAll(String path, List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeAll(rows);
        }
    }

    // -------------------------------------------------------------------------
    //  Employee.csv operations
    // -------------------------------------------------------------------------

    /**
     * Returns all data rows from Employee.csv (header excluded).
     * Each row: [empNum, lastName, firstName, birthday, address, phoneNumber,
     *            sssNumber, philhealthNumber, tinNumber, pagibigNumber,
     *            status, position, supervisor]
     */
    public List<String[]> getAllEmployees() {
        try {
            List<String[]> all = readAll(EMP_CSV);
            return all.size() > 1 ? new ArrayList<>(all.subList(1, all.size())) : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("EmployeeDetailsReader.getAllEmployees: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Returns the row for the given employee ID, or {@code null} if not found. */
    public String[] getEmployeeDetails(String empId) {
        for (String[] row : getAllEmployees()) {
            if (row[0].trim().equals(empId.trim())) {
                return row;
            }
        }
        return null;
    }

    /** Appends a new employee row to Employee.csv. */
    public void addEmployee(String[] employeeRow) throws IOException {
        List<String[]> all = readAll(EMP_CSV);
        all.add(employeeRow);
        writeAll(EMP_CSV, all);
    }

    /**
     * Replaces the row whose first column matches {@code updatedRow[0]}.
     *
     * @return {@code true} if a matching row was found and replaced
     */
    public boolean updateEmployee(String[] updatedRow) throws IOException {
        List<String[]> all = readAll(EMP_CSV);
        boolean found = false;
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i)[0].trim().equals(updatedRow[0].trim())) {
                all.set(i, updatedRow);
                found = true;
                break;
            }
        }
        if (found) writeAll(EMP_CSV, all);
        return found;
    }

    /**
     * Removes the row whose first column equals {@code empId}.
     *
     * @return {@code true} if a row was removed
     */
    public boolean deleteEmployee(String empId) throws IOException {
        List<String[]> all = readAll(EMP_CSV);
        // index 0 is the header ("employeeNum"), which will never match a real ID
        boolean removed = all.removeIf(r -> r.length > 0 && r[0].trim().equals(empId.trim()));
        if (removed) writeAll(EMP_CSV, all);
        return removed;
    }

    // -------------------------------------------------------------------------
    //  Login.csv operations
    // -------------------------------------------------------------------------

    /**
     * Returns the Login.csv row for the given username, or {@code null} if not found.
     * Row: [empNum, username, roleName, password(BCrypt hash), changePassword]
     */
    public String[] getLoginDataByUsername(String username) {
        try {
            List<String[]> all = readAll(LOGIN_CSV);
            for (int i = 1; i < all.size(); i++) {
                String[] row = all.get(i);
                if (row.length >= 2 && row[1].trim().equalsIgnoreCase(username.trim())) {
                    return row;
                }
            }
        } catch (IOException e) {
            System.err.println("EmployeeDetailsReader.getLoginDataByUsername: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns the Login.csv row for the given employee number, or {@code null} if not found.
     */
    public String[] getLoginDataByEmpNum(String empNum) {
        try {
            List<String[]> all = readAll(LOGIN_CSV);
            for (int i = 1; i < all.size(); i++) {
                String[] row = all.get(i);
                if (row.length >= 1 && row[0].trim().equals(empNum.trim())) {
                    return row;
                }
            }
        } catch (IOException e) {
            System.err.println("EmployeeDetailsReader.getLoginDataByEmpNum: " + e.getMessage());
        }
        return null;
    }

    /**
     * Replaces the password hash for the given employee ID in Login.csv and
     * sets {@code changePassword} to {@code "NO"}.
     *
     * @param empId          the employee number to update
     * @param newHashedPassword the new BCrypt hash (must already be hashed by caller)
     * @return {@code true} if the row was found and updated
     */
    public boolean changeUserPassword(String empId, String newHashedPassword) throws IOException {
        List<String[]> all = readAll(LOGIN_CSV);
        boolean found = false;
        for (int i = 1; i < all.size(); i++) {
            String[] row = all.get(i);
            if (row[0].trim().equals(empId.trim())) {
                row[3] = newHashedPassword;
                row[4] = "NO";
                found = true;
                break;
            }
        }
        if (found) writeAll(LOGIN_CSV, all);
        return found;
    }
}
