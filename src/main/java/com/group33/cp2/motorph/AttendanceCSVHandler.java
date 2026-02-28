package com.group33.cp2.motorph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles loading attendance data from a CSV file into Attendance objects.
 * Uses classpath resource loading so the application works from any working directory.
 *
 * <p>Original hardcoded path "src/main/java/com/group33/cp2/motorph/csv/MotorPHEmployeeAttendance.csv"
 * was replaced with {@code getClass().getResourceAsStream("/MotorPHEmployeeAttendance.csv")}
 * to support packaged JAR execution.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class AttendanceCSVHandler {

    // Classpath-relative path to the attendance CSV resource
    private static final String RESOURCE_PATH = "/MotorPHEmployeeAttendance.csv";

    /**
     * Constructs an AttendanceCSVHandler.
     */
    public AttendanceCSVHandler() {
    }

    /**
     * Loads all attendance records from the CSV file and returns a sorted list of
     * Attendance objects. Assumes the CSV has a header and follows the format:
     * [0] Employee ID, [3] Date (MM/DD/YYYY), [4] Login time (HH:mm), [5] Logout time (HH:mm)
     *
     * @return a sorted list of Attendance objects based on employee ID
     */
    public List<Attendance> loadAllAttendanceFromCsv() {
        List<Attendance> attendanceList = new ArrayList<>();

        InputStream is = getClass().getResourceAsStream(RESOURCE_PATH);
        if (is == null) {
            System.err.println("Warning: Attendance CSV not found on classpath: " + RESOURCE_PATH);
            return attendanceList;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine(); // Skip the header line
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(",");
                if (data.length < 6) {
                    continue;
                }

                try {
                    String employeeID = data[0].trim();

                    // Parse date (format: MM/DD/YYYY)
                    String[] dateParts = data[3].trim().split("/");
                    LocalDate date = LocalDate.of(
                            Integer.parseInt(dateParts[2]), // year
                            Integer.parseInt(dateParts[0]), // month
                            Integer.parseInt(dateParts[1])  // day
                    );

                    // Parse login time (format: HH:mm)
                    String[] logInParts = data[4].trim().split(":");
                    LocalTime logIn = LocalTime.of(
                            Integer.parseInt(logInParts[0]),
                            Integer.parseInt(logInParts[1])
                    );

                    // Parse logout time (format: HH:mm)
                    String[] logOutParts = data[5].trim().split(":");
                    LocalTime logOut = LocalTime.of(
                            Integer.parseInt(logOutParts[0]),
                            Integer.parseInt(logOutParts[1])
                    );

                    Attendance attendance = new Attendance(employeeID, date, logIn, logOut);
                    attendanceList.add(attendance);
                } catch (Exception e) {
                    System.err.println("Skipping malformed attendance row: " + line + " | Error: " + e.getMessage());
                }
            }

            attendanceList.sort(Comparator.comparing(Attendance::getEmployeeID));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }
}
