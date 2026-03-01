package com.group33.cp2.motorph;

import java.util.Collections;
import java.util.List;

/**
 * Service class that provides access to employee attendance data.
 * Loads attendance records from a CSV file using AttendanceCSVHandler.
 *
 * <p><strong>Encapsulation (BP9):</strong> {@link #getAllAttendance()} returns an
 * unmodifiable list view so callers cannot alter the loaded attendance data.</p>
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
     * Returns an unmodifiable view of all loaded attendance records.
     * The underlying data cannot be altered through the returned list.
     *
     * @return an unmodifiable list of Attendance objects
     */
    public List<Attendance> getAllAttendance() {
        // BP9: return unmodifiable view — attendance data is read-only after loading
        return Collections.unmodifiableList(attendanceList);
    }
}
