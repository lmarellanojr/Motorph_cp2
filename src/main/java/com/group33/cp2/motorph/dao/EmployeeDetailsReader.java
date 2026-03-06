package com.group33.cp2.motorph.dao;

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

    private void writeAll(String path, List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeAll(rows);
        }
    }

    // -------------------------------------------------------------------------
    //  Employee.csv operations
    // -------------------------------------------------------------------------

    public List<String[]> getAllEmployees() {
        try {
            List<String[]> all = readAll(EMP_CSV);
            return all.size() > 1 ? new ArrayList<>(all.subList(1, all.size())) : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("EmployeeDetailsReader.getAllEmployees: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String[] getEmployeeDetails(String empId) {
        for (String[] row : getAllEmployees()) {
            if (row[0].trim().equals(empId.trim())) {
                return row;
            }
        }
        return null;
    }

    public void addEmployee(String[] employeeRow) throws IOException {
        List<String[]> all = readAll(EMP_CSV);
        all.add(employeeRow);
        writeAll(EMP_CSV, all);
    }

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

    public boolean deleteEmployee(String empId) throws IOException {
        List<String[]> all = readAll(EMP_CSV);
        boolean removed = all.removeIf(r -> r.length > 0 && r[0].trim().equals(empId.trim()));
        if (removed) writeAll(EMP_CSV, all);
        return removed;
    }

    // -------------------------------------------------------------------------
    //  Login.csv operations
    // -------------------------------------------------------------------------

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
     * Updates the BCrypt password hash and the {@code changePassword} flag for the given employee.
     *
     * @param empId              the employee number to update
     * @param newHashedPassword  the new BCrypt-hashed password
     * @param changePassword     {@code "YES"} if the employee must change their password on next login;
     *                           {@code "NO"} if the password change requirement is cleared
     * @return {@code true} if the row was found and updated
     */
    public boolean changeUserPassword(String empId, String newHashedPassword, String changePassword)
            throws IOException {
        List<String[]> all = readAll(LOGIN_CSV);
        boolean found = false;
        for (int i = 1; i < all.size(); i++) {
            String[] row = all.get(i);
            if (row[0].trim().equals(empId.trim())) {
                row[3] = newHashedPassword;
                row[4] = changePassword != null ? changePassword : "NO";
                found = true;
                break;
            }
        }
        if (found) writeAll(LOGIN_CSV, all);
        return found;
    }

    /**
     * Convenience overload that clears the {@code changePassword} flag (sets it to {@code "NO"}).
     * Used when an employee sets their own password through the forced-change flow.
     *
     * @param empId             the employee number to update
     * @param newHashedPassword the new BCrypt-hashed password
     * @return {@code true} if the row was found and updated
     */
    public boolean changeUserPassword(String empId, String newHashedPassword) throws IOException {
        return changeUserPassword(empId, newHashedPassword, "NO");
    }
}
