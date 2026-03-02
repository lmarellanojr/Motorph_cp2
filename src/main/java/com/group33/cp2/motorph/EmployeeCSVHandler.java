package com.group33.cp2.motorph;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes employee records from CSV. On first run, copies the bundled CSV to the user's home dir.
 */
public class EmployeeCSVHandler {

    private static final String RESOURCE_PATH = "/MotorPHEmployeeData.csv";

    // uses motorph.data.dir system property, defaults to user home
    private String getWritableFilePath() {
        String dataDir = System.getProperty("motorph.data.dir",
                System.getProperty("user.home"));
        return dataDir + File.separator + "MotorPHEmployeeData.csv";
    }

    public EmployeeCSVHandler() {
        initWritableFile();
    }

    // copies the bundled CSV to disk on first run
    private void initWritableFile() {
        File writable = new File(getWritableFilePath());
        if (writable.exists()) {
            return;
        }
        try (InputStream is = getClass().getResourceAsStream(RESOURCE_PATH)) {
            if (is == null) {
                System.err.println("WARNING: Bundled resource not found: " + RESOURCE_PATH);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                 PrintWriter writer = new PrintWriter(new FileWriter(writable))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("WARNING: Could not initialise writable employee CSV: " + e.getMessage());
        }
    }

    public List<Employee> readEmployees() {
        List<Employee> employees = new ArrayList<>();
        File writable = new File(getWritableFilePath());

        try {
            CSVReader reader;
            if (writable.exists()) {
                reader = new CSVReaderBuilder(new FileReader(writable))
                        .withSkipLines(1)
                        .build();
            } else {
                InputStream is = getClass().getResourceAsStream(RESOURCE_PATH);
                if (is == null) {
                    System.err.println("ERROR: Employee data resource not found: " + RESOURCE_PATH);
                    return employees;
                }
                reader = new CSVReaderBuilder(new InputStreamReader(is))
                        .withSkipLines(1)
                        .build();
            }

            try (reader) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    try {
                        String employeeID = line[0].trim();
                        String lastName = line[1].trim();
                        String firstName = line[2].trim();
                        String birthday = line[3].trim();
                        String address = removeQuotesAndCommas(line[4].trim());
                        String phoneNumber = line[5].trim();
                        String sssNumber = line[6].trim();
                        String philHealthNumber = line[7].trim();
                        String tinNumber = line[8].trim();
                        String pagibigNumber = line[9].trim();
                        String status = line[10].trim();
                        String position = line[11].trim();
                        String immediateSupervisor = removeQuotesAndCommas(line[12].trim());

                        double basicSalary = Double.parseDouble(removeQuotesAndCommas(line[13].trim()));
                        double riceSubsidy = Double.parseDouble(removeQuotesAndCommas(line[14].trim()));
                        double phoneAllowance = Double.parseDouble(removeQuotesAndCommas(line[15].trim()));
                        double clothingAllowance = Double.parseDouble(removeQuotesAndCommas(line[16].trim()));
                        double grossSemiMonthly = Double.parseDouble(removeQuotesAndCommas(line[17].trim()));
                        double hourlyRate = Double.parseDouble(removeQuotesAndCommas(line[18].trim()));

                        Allowance allowance = new Allowance(employeeID, riceSubsidy, phoneAllowance, clothingAllowance);
                        GovernmentDetails governmentDetails = new GovernmentDetails(
                                employeeID, sssNumber, philHealthNumber, tinNumber, pagibigNumber);

                        // instantiate the concrete subclass based on employment status
                        Employee employee;
                        if ("Probationary".equalsIgnoreCase(status)) {
                            employee = new ProbationaryEmployee(
                                    employeeID, lastName, firstName, birthday, address, phoneNumber,
                                    basicSalary, hourlyRate, grossSemiMonthly, status, position,
                                    immediateSupervisor, allowance, governmentDetails
                            );
                        } else {
                            // default to RegularEmployee for "Regular" and any unknown status
                            employee = new RegularEmployee(
                                    employeeID, lastName, firstName, birthday, address, phoneNumber,
                                    basicSalary, hourlyRate, grossSemiMonthly, status, position,
                                    immediateSupervisor, allowance, governmentDetails
                            );
                        }

                        employees.add(employee);
                    } catch (Exception e) {
                        System.out.println("Skipping row due to error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return employees;
    }

    // strips quotes and commas from fields like "90,000"
    private String removeQuotesAndCommas(String value) {
        return value.replace("\"", "").replace(",", "");
    }

    public void addEmployee(Employee e) {
        List<Employee> list = readEmployees();
        list.add(e);
        writeEmployees(list);
    }

    public boolean deleteEmployee(String employeeId) {
        try {
            File writable = new File(getWritableFilePath());
            List<String> lines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(writable))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String csvEmployeeId = parts[0].trim().replace("\"", "");
                        if (csvEmployeeId.equals(employeeId)) {
                            found = true;
                            continue;
                        }
                    }
                    lines.add(line);
                }
            }

            if (!found) {
                System.out.println("Employee ID '" + employeeId + "' not found in CSV.");
                return false;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(writable))) {
                for (String l : lines) {
                    writer.println(l);
                }
            }

            System.out.println("Employee successfully deleted.");
            return true;

        } catch (IOException ex) {
            System.out.println("Error deleting employee: " + ex.getMessage());
            return false;
        }
    }

    public void updateEmployee(Employee updatedEmployee) {
        List<Employee> list = readEmployees();

        for (Employee e : list) {
            if (e.getEmployeeID().equals(updatedEmployee.getEmployeeID())) {
                e.setLastName(updatedEmployee.getLastName());
                e.setFirstName(updatedEmployee.getFirstName());
                e.setBirthday(updatedEmployee.getBirthday());
                e.setAddress(updatedEmployee.getAddress());
                e.setPhoneNumber(updatedEmployee.getPhoneNumber());

                e.getGovernmentDetails().setSssNumber(updatedEmployee.getGovernmentDetails().getSssNumber());
                e.getGovernmentDetails().setPhilHealthNumber(updatedEmployee.getGovernmentDetails().getPhilHealthNumber());
                e.getGovernmentDetails().setTinNumber(updatedEmployee.getGovernmentDetails().getTinNumber());
                e.getGovernmentDetails().setPagibigNumber(updatedEmployee.getGovernmentDetails().getPagibigNumber());

                e.setStatus(updatedEmployee.getStatus());
                e.setPosition(updatedEmployee.getPosition());
                e.setImmediateSupervisor(updatedEmployee.getImmediateSupervisor());

                e.setBasicSalary(updatedEmployee.getBasicSalary());
                e.setHourlyRate(updatedEmployee.getHourlyRate());
                e.setGrossSemiMonthlyRate(updatedEmployee.getGrossSemiMonthlyRate());

                e.getAllowanceDetails().setRiceAllowance(updatedEmployee.getAllowanceDetails().getRiceAllowance());
                e.getAllowanceDetails().setPhoneAllowance(updatedEmployee.getAllowanceDetails().getPhoneAllowance());
                e.getAllowanceDetails().setClothingAllowance(updatedEmployee.getAllowanceDetails().getClothingAllowance());

                break;
            }
        }

        writeEmployees(list);
    }

    public void writeEmployees(List<Employee> employees) {
        File writable = new File(getWritableFilePath());
        try (CSVWriter writer = new CSVWriter(new FileWriter(writable))) {
            writer.writeNext(new String[]{
                "Employee ID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                "SSS Number", "PhilHealth Number", "TIN Number", "Pag-ibig Number",
                "Status", "Position", "Immediate Supervisor",
                "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
                "Gross Semi-Monthly", "Hourly Rate"
            });

            for (Employee e : employees) {
                writer.writeNext(new String[]{
                    e.getEmployeeID(),
                    e.getLastName(),
                    e.getFirstName(),
                    e.getBirthday(),
                    e.getAddress(),
                    e.getPhoneNumber(),
                    e.getGovernmentDetails().getSssNumber(),
                    e.getGovernmentDetails().getPhilHealthNumber(),
                    e.getGovernmentDetails().getTinNumber(),
                    e.getGovernmentDetails().getPagibigNumber(),
                    e.getStatus(),
                    e.getPosition(),
                    e.getImmediateSupervisor(),
                    String.valueOf(e.getBasicSalary()),
                    String.valueOf(e.getAllowanceDetails().getRiceAllowance()),
                    String.valueOf(e.getAllowanceDetails().getPhoneAllowance()),
                    String.valueOf(e.getAllowanceDetails().getClothingAllowance()),
                    String.valueOf(e.getGrossSemiMonthlyRate()),
                    String.valueOf(e.getHourlyRate())
                });
            }

            System.out.println("Employees written to CSV successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
