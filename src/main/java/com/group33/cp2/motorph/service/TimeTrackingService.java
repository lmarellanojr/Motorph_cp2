package com.group33.cp2.motorph.service;

import com.group33.cp2.motorph.dao.TimeTrackerReader;

import java.io.IOException;
import java.util.List;

/**
 * Service layer facade for all time-tracking operations.
 *
 * <p>Wraps {@link TimeTrackerReader} so that {@code forms/} classes never import
 * from the {@code dao/} package for time-tracking concerns.</p>
 *
 * <p><strong>OOP Pillar — Encapsulation:</strong> The underlying DAO class and its
 * file path are hidden. Callers depend only on this service interface.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class TimeTrackingService {

    /**
     * Records a clock-in event for the given employee at the current system time.
     *
     * @param empId the employee number
     * @throws IOException if the TimeTracker CSV cannot be read or written
     */
    public void clockIn(String empId) throws IOException {
        TimeTrackerReader.clockIn(empId);
    }

    /**
     * Records a clock-out event for the given employee's most recent open clock-in.
     *
     * @param empId the employee number
     * @throws IOException if no active clock-in is found, or if the CSV cannot be written
     */
    public void clockOut(String empId) throws IOException {
        TimeTrackerReader.clockOut(empId);
    }

    /**
     * Returns all time-log rows for the given employee.
     *
     * <p>Each row is a {@code String[]} with columns:
     * [0]=empNum, [1]=date, [2]=timeIn, [3]=timeOut, [4]=hoursWorked.</p>
     *
     * @param empId the employee number
     * @return a list of time-log rows; never {@code null}, may be empty
     * @throws IOException if the TimeTracker CSV cannot be read
     */
    public List<String[]> getTimeLogs(String empId) throws IOException {
        return TimeTrackerReader.getTimeLogs(empId);
    }
}
