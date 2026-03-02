package com.group33.cp2.motorph;

import java.util.Collections;
import java.util.List;

/**
 * Loads attendance records from CSV and provides read-only access to them.
 */
public class AttendanceService {

    private final List<Attendance> attendanceList;
    private final AttendanceCSVHandler attendanceCSVHandler;

    public AttendanceService() {
        attendanceCSVHandler = new AttendanceCSVHandler();
        attendanceList = attendanceCSVHandler.loadAllAttendanceFromCsv();
    }

    // read-only; attendance data doesn't change after loading
    public List<Attendance> getAllAttendance() {
        return Collections.unmodifiableList(attendanceList);
    }
}
