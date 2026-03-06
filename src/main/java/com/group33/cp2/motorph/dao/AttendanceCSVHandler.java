package com.group33.cp2.motorph.dao;

import com.group33.cp2.motorph.model.Attendance;

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
 * Loads attendance records from the bundled CSV resource into Attendance objects.
 */
public class AttendanceCSVHandler {

    private static final String RESOURCE_PATH = "/MotorPHEmployeeAttendance.csv";

    public AttendanceCSVHandler() {
    }

    // columns used: [0] employee ID, [3] date MM/DD/YYYY, [4] login HH:mm, [5] logout HH:mm
    public List<Attendance> loadAllAttendanceFromCsv() {
        List<Attendance> attendanceList = new ArrayList<>();

        InputStream is = getClass().getResourceAsStream(RESOURCE_PATH);
        if (is == null) {
            System.err.println("Warning: Attendance CSV not found on classpath: " + RESOURCE_PATH);
            return attendanceList;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine(); // skip header
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

                    // date: MM/DD/YYYY
                    String[] dateParts = data[3].trim().split("/");
                    LocalDate date = LocalDate.of(
                            Integer.parseInt(dateParts[2]),
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1])
                    );

                    // login: HH:mm
                    String[] logInParts = data[4].trim().split(":");
                    LocalTime logIn = LocalTime.of(
                            Integer.parseInt(logInParts[0]),
                            Integer.parseInt(logInParts[1])
                    );

                    // logout: HH:mm
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
