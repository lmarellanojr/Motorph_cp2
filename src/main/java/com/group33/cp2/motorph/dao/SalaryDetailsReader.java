package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.Salary;
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

// Owns read/write access to Salary.csv.
// Lazy-loads into an in-memory map on first access; invalidates cache after writes.
// Callers receive Salary objects and never see raw CSV rows.
public class SalaryDetailsReader {

    private static final String SALARY_CSV = "src/main/resources/data/Salary.csv";

    private Map<String, Salary> cache = null;

    private List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(SALARY_CSV))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error in Salary.csv: " + e.getMessage(), e);
        }
        return rows;
    }

    private void writeAll(List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(SALARY_CSV))) {
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
                    cache.put(row[0].trim(), new Salary(
                        Double.parseDouble(row[1].trim()),
                        Double.parseDouble(row[2].trim()),
                        Double.parseDouble(row[3].trim())
                    ));
                } catch (NumberFormatException e) {
                    System.err.println("SalaryDetailsReader: skipping malformed row " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("SalaryDetailsReader.loadCache: " + e.getMessage());
        }
    }

    public Salary getSalary(String empId) {
        if (cache == null) loadCache();
        return cache.get(empId.trim());
    }

    public Map<String, Salary> getAllSalaries() {
        if (cache == null) loadCache();
        return Collections.unmodifiableMap(cache);
    }

    public void addSalary(String empId, Salary salary) throws IOException {
        List<String[]> all = readAll();
        all.add(new String[]{
            empId.trim(),
            String.valueOf(salary.getBasicSalary()),
            String.valueOf(salary.getHourlyRate()),
            String.valueOf(salary.getGrossSMRate())
        });
        writeAll(all);
        cache = null;
    }

    public void updateSalary(String empId, Salary salary) throws IOException {
        List<String[]> all = readAll();
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i)[0].trim().equals(empId.trim())) {
                all.get(i)[1] = String.valueOf(salary.getBasicSalary());
                all.get(i)[2] = String.valueOf(salary.getHourlyRate());
                all.get(i)[3] = String.valueOf(salary.getGrossSMRate());
                break;
            }
        }
        writeAll(all);
        cache = null;
    }

    public void deleteSalary(String empId) throws IOException {
        List<String[]> all = readAll();
        all.removeIf(r -> r.length > 0 && r[0].trim().equals(empId.trim()));
        writeAll(all);
        cache = null;
    }
}
