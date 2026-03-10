package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.PasswordResetRequest;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Single gatekeeper for Password_Reset_Requests.csv.
// CSV columns: [0]=empNum [1]=empName [2]=dateOfRequest [3]=status [4]=adminName [5]=adminEmpNum [6]=dateOfReset
public class PasswordResetReader {

    private static final String FILE_PATH =
            "src/main/resources/data/Password_Reset_Requests.csv";

    private static List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in Password_Reset_Requests.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    private static void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeAll(rows);
        }
    }

    public static void addRequest(PasswordResetRequest request) throws IOException {
        List<String[]> data = readAll();
        data.add(request.toArray());
        writeAll(data);
    }

    public static List<PasswordResetRequest> getAllRequests() {
        List<PasswordResetRequest> list = new ArrayList<>();
        try {
            List<String[]> rows = readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 7) {
                    list.add(new PasswordResetRequest(
                        row[0], row[1], row[2], row[3], row[4], row[5], row[6]));
                } else if (row.length >= 4) {
                    list.add(new PasswordResetRequest(
                        row[0], row[1], row[2], row[3],
                        row.length > 4 ? row[4] : "",
                        row.length > 5 ? row[5] : "",
                        row.length > 6 ? row[6] : ""));
                }
            }
        } catch (IOException e) {
            System.err.println("PasswordResetReader.getAllRequests: " + e.getMessage());
        }
        return list;
    }

    public static boolean approveRequest(String employeeNumber, String adminName, String adminEmpNum)
            throws IOException {
        List<String[]> rows = readAll();
        boolean updated = false;
        String currentDate = new SimpleDateFormat("MM/dd/yyyy - hh:mm:ss a").format(new Date());

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 0 && row[0].trim().equals(employeeNumber.trim())
                    && row.length > 3 && "Pending".equalsIgnoreCase(row[3].trim())) {

                String[] extended = new String[7];
                System.arraycopy(row, 0, extended, 0, Math.min(row.length, 7));
                for (int j = row.length; j < 7; j++) {
                    extended[j] = "";
                }

                extended[3] = "Approved";
                extended[4] = adminName;
                extended[5] = adminEmpNum;
                extended[6] = currentDate;
                rows.set(i, extended);
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAll(rows);
        }
        return updated;
    }
}
