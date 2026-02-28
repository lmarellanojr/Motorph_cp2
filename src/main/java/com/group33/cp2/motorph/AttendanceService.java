package com.group33.cp2.motorph;

import java.util.List;

/**
 * Service class that provides access to employee attendance data.
 * Loads attendance records from a CSV file using AttendanceCSVHandler.
 *
 * @author Group13
 * @version 1.0
 */
public class AttendanceService {

    private final List<Attendance> attendanceList;
    private final AttendanceCSVHandler attendanceCSVHandler;

    /**
     * Constructs an AttendanceService and loads attendance data from the CSV file.
     */
    public AttendanceService() {
        attendanceCSVHandler = new AttendanceCSVHandler();
        attendanceList = attendanceCSVHandler.loadAllAttendanceFromCsv();
    }

    /**
     * Returns the full list of attendance records.
     *
     * @return a list of Attendance objects
     */
    public List<Attendance> getAllAttendance() {
        return attendanceList;
    }
}
