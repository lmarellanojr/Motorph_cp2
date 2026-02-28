package com.group33.cp2.motorph;

import com.group33.cp2.motorph.forms.EmployeeListFrame;
import com.group33.cp2.motorph.forms.LoginFrame;
import com.group33.cp2.motorph.forms.MenuFrame;
import com.group33.cp2.motorph.forms.NewEmployeeFrame;
import com.group33.cp2.motorph.forms.ViewEmployeeFrame;
import com.group33.cp2.motorph.forms.ViewSalaryFrame;
import java.time.LocalDate;
import javax.swing.JFrame;

/**
 * Provides static navigation methods for transitioning between application screens.
 * Each method disposes the current frame before opening the target frame.
 *
 * @author Group13
 * @version 1.0
 */
public class NavigationManager {

    /**
     * Opens the main {@link MenuFrame} and disposes the current frame.
     *
     * @param currentFrame the frame to close before navigation
     */
    public static void openMenuFrame(JFrame currentFrame) {
        currentFrame.dispose();
        MenuFrame home = new MenuFrame();
        home.setLocationRelativeTo(null);
        home.setVisible(true);
    }

    /**
     * Opens the {@link LoginFrame} and disposes the current frame.
     *
     * @param currentFrame the frame to close before navigation
     */
    public static void openLoginFrame(JFrame currentFrame) {
        currentFrame.dispose();
        LoginFrame login = new LoginFrame();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    /**
     * Opens the {@link EmployeeListFrame} and disposes the current frame.
     *
     * @param currentFrame the frame to close before navigation
     */
    public static void openEmployeeListFrame(JFrame currentFrame) {
        currentFrame.dispose();
        EmployeeListFrame list = new EmployeeListFrame();
        list.setLocationRelativeTo(null);
        list.setVisible(true);
    }

    /**
     * Opens the {@link ViewEmployeeFrame} for the specified employee and disposes the current frame.
     *
     * @param currentFrame       the frame to close before navigation
     * @param selectedEmployeeID the ID of the employee to display
     */
    public static void openViewEmployeeFrame(JFrame currentFrame, String selectedEmployeeID) {
        currentFrame.dispose();
        ViewEmployeeFrame view = new ViewEmployeeFrame(selectedEmployeeID);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }

    /**
     * Opens the {@link ViewSalaryFrame} for a payroll period and disposes the current frame.
     *
     * @param currentFrame      the frame to close before navigation
     * @param employeeId        the employee's unique identifier
     * @param selectedStartDate the inclusive start of the payroll period
     * @param selectedEndDate   the inclusive end of the payroll period
     */
    public static void openViewSalaryFrame(JFrame currentFrame, String employeeId,
                                           LocalDate selectedStartDate, LocalDate selectedEndDate) {
        currentFrame.dispose();
        ViewSalaryFrame salary = new ViewSalaryFrame(employeeId, selectedStartDate, selectedEndDate);
        salary.setLocationRelativeTo(null);
        salary.setVisible(true);
    }

    /**
     * Opens the {@link NewEmployeeFrame} and disposes the current frame.
     *
     * @param currentFrame the frame to close before navigation
     */
    public static void openNewEmployeeFrame(JFrame currentFrame) {
        currentFrame.dispose();
        NewEmployeeFrame frame = new NewEmployeeFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
