package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.Allowance;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Owns read/write access to Allowance.csv.
// Lazy-loads into an in-memory map on first access; invalidates cache after writes.
// Callers receive Allowance objects and never see raw CSV rows.
public class AllowanceDetailsReader {

    private static final String ALLOWANCE_CSV = "src/main/resources/data/Allowance.csv";

    private Map<String, Allowance> cache = null;

    private List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(ALLOWANCE_CSV))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in Allowance.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    private void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(ALLOWANCE_CSV))) {
            writer.writeAll(rows);
        }
    }

    private void loadCache() {
        cache = new LinkedHashMap<>();
        try {
            List<String[]> all = readAll();
            for (int i = 1; i < all.size(); i++) {
                String[] row = all.get(i);
                if (row.length < 4) continue;
                try {
                    String empId = row[0].trim();
                    cache.put(empId, new Allowance(
                        empId,
                        Double.parseDouble(row[1].trim()),
                        Double.parseDouble(row[2].trim()),
                        Double.parseDouble(row[3].trim())
                    ));
                } catch (NumberFormatException e) {
                    System.err.println("AllowanceDetailsReader: skipping malformed row " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("AllowanceDetailsReader.loadCache: " + e.getMessage());
        }
    }

    public Allowance getAllowance(String empId) {
        if (cache == null) loadCache();
        return cache.get(empId.trim());
    }

    public Map<String, Allowance> getAllAllowances() {
        if (cache == null) loadCache();
        return Collections.unmodifiableMap(cache);
    }

    public void addAllowance(String empId, Allowance allowance) throws IOException {
        List<String[]> all = readAll();
        all.add(new String[]{
            empId.trim(),
            String.valueOf(allowance.getRiceAllowance()),
            String.valueOf(allowance.getPhoneAllowance()),
            String.valueOf(allowance.getClothingAllowance())
        });
        writeAll(all);
        cache = null;
    }

    public void updateAllowance(String empId, Allowance allowance) throws IOException {
        List<String[]> all = readAll();
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i)[0].trim().equals(empId.trim())) {
                all.get(i)[1] = String.valueOf(allowance.getRiceAllowance());
                all.get(i)[2] = String.valueOf(allowance.getPhoneAllowance());
                all.get(i)[3] = String.valueOf(allowance.getClothingAllowance());
                break;
            }
        }
        writeAll(all);
        cache = null;
    }

    public void deleteAllowance(String empId) throws IOException {
        List<String[]> all = readAll();
        all.removeIf(r -> r.length > 0 && r[0].trim().equals(empId.trim()));
        writeAll(all);
        cache = null;
    }
}
