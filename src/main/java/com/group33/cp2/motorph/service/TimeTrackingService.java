package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.TimeTrackerReader;

import java.io.IOException;
import java.util.List;

// Service facade for time-tracking: wraps TimeTrackerReader so forms/ never import from dao/.
public class TimeTrackingService {

    // Records a clock-in for the given employee at the current system time.
    public void clockIn(String empId) throws IOException {
        TimeTrackerReader.clockIn(empId);
    }

    // Records a clock-out for the employee's most recent open clock-in.
    // Throws IOException if no active clock-in is found.
    public void clockOut(String empId) throws IOException {
        TimeTrackerReader.clockOut(empId);
    }

    // Returns all time-log rows for the given employee.
    // Each row: [0]=empNum [1]=date [2]=timeIn [3]=timeOut [4]=hoursWorked
    public List<String[]> getTimeLogs(String empId) throws IOException {
        return TimeTrackerReader.getTimeLogs(empId);
    }
}
