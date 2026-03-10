package com.group33.cp2.motorph.forms;

import java.time.LocalDate;
import javax.swing.JFrame;

// Static helper for switching between screens. Each method closes the current frame and opens the next.
public class NavigationManager {

    public static void openMenuFrame(JFrame currentFrame) {
        currentFrame.dispose();
        MenuFrame home = new MenuFrame();
        home.setLocationRelativeTo(null);
        home.setVisible(true);
    }

    public static void openLoginFrame(JFrame currentFrame) {
        currentFrame.dispose();
        LoginFrame login = new LoginFrame();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    public static void openEmployeeListFrame(JFrame currentFrame) {
        currentFrame.dispose();
        EmployeeListFrame list = new EmployeeListFrame();
        list.setLocationRelativeTo(null);
        list.setVisible(true);
    }

    public static void openViewEmployeeFrame(JFrame currentFrame, String selectedEmployeeID) {
        currentFrame.dispose();
        ViewEmployeeFrame view = new ViewEmployeeFrame(selectedEmployeeID);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }

    public static void openViewSalaryFrame(JFrame currentFrame, String employeeId,
                                           LocalDate selectedStartDate, LocalDate selectedEndDate) {
        currentFrame.dispose();
        ViewSalaryFrame salary = new ViewSalaryFrame(employeeId, selectedStartDate, selectedEndDate);
        salary.setLocationRelativeTo(null);
        salary.setVisible(true);
    }

    public static void openNewEmployeeFrame(JFrame currentFrame) {
        currentFrame.dispose();
        NewEmployeeFrame frame = new NewEmployeeFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
