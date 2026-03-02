package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.Constants;
import com.group33.cp2.motorph.Employee;
import com.group33.cp2.motorph.EmployeeService;
import com.group33.cp2.motorph.NavigationManager;
import com.group33.cp2.motorph.Payroll;
import com.group33.cp2.motorph.Utility;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.Year;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * ViewEmployeeFrame displays detailed, read-only information about a selected
 * employee, including personal, government, and compensation data, with
 * payroll period selection.
 *
 * <p>NOTE: The original source declared {@code private JDateChooser dateFrom}
 * and {@code private JDateChooser dateTo} fields, and the {@code clearAllFields()}
 * method referenced those fields plus several non-existent fields
 * ({@code txtRegularHours}, {@code txtOvertimeHours}, {@code txtRegularPay},
 * {@code txtOvertimePay}, {@code txtGrossSalary}, {@code txtNetSalary},
 * {@code txtPayrollStatus}). All JDateChooser references have been removed and
 * {@code clearAllFields()} has been corrected to only reference fields that are
 * actually declared in this class.</p>
 *
 */
public class ViewEmployeeFrame extends javax.swing.JFrame {

    /** Handles employee data retrieval. */
    EmployeeService employeeService = new EmployeeService();
    /** Currently selected employee. */
    Employee selectedEmployee;

    // GUI fields for employee and payroll info
    private JTextField txtEmployeeNumber;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtBirthday;
    private JTextField txtAddress;
    private JTextField txtPosition;
    private JTextField txtHourlyRate;
    private JTextField txtSSSNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtPagibigNumber;
    private JTextField txtTINNumber;
    private JTextField txtRiceSubsidy;
    private JTextField txtClothingAllowance;
    private JTextField txtPhoneAllowance;
    private JTextField txtPhoneNumber;
    private JTextField txtStatus;
    private JTextField txtImmediateSupervisor;
    private JTextField txtGrossSemiMonthly;
    private JTextField txtBasicSalary;

    /**
     * Constructs the ViewEmployeeFrame for the given employee ID.
     *
     * @param employeeId the ID of the employee to display
     */
    public ViewEmployeeFrame(String employeeId) {
        setTitle("MotorPH Employee Payroll System");
        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        // Fetch employee once
        selectedEmployee = employeeService.getEmployeeById(employeeId);

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee ID " + employeeId + " was not found.",
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            NavigationManager.openEmployeeListFrame(this); // Go back if employee not found
            return;
        }

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left panel: Employee details
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(createEmployeeDetailsPanel());

        // Right panel: Government details, compensation, and payroll period selection
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(createGovernmentDetailsPanel());
        rightPanel.add(createCompensationDetailsPanel());
        rightPanel.add(createPayrollPanel(employeeId)); // employeeId still needed for payroll panel's compute action

        // Populate fields using the fetched selectedEmployee
        setEmployeeDetails();
        setGovernmentDetails();
        setCompensationDetails();

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);

        disableFields();
    }

    private void setEmployeeDetails() {
        txtEmployeeNumber.setText(selectedEmployee.getEmployeeID());
        txtLastName.setText(selectedEmployee.getLastName());
        txtFirstName.setText(selectedEmployee.getFirstName());
        txtBirthday.setText(selectedEmployee.getBirthday());
        txtAddress.setText(selectedEmployee.getAddress());
        txtPhoneNumber.setText(selectedEmployee.getPhoneNumber());
        txtStatus.setText(selectedEmployee.getStatus());
        txtPosition.setText(selectedEmployee.getPosition());
        txtImmediateSupervisor.setText(selectedEmployee.getImmediateSupervisor());
    }

    private void setGovernmentDetails() {
        txtSSSNumber.setText(selectedEmployee.getGovernmentDetails().getSssNumber());
        txtPhilHealthNumber.setText(selectedEmployee.getGovernmentDetails().getPhilHealthNumber());
        txtTINNumber.setText(selectedEmployee.getGovernmentDetails().getTinNumber());
        txtPagibigNumber.setText(selectedEmployee.getGovernmentDetails().getPagibigNumber());
    }

    private void setCompensationDetails() {
        txtRiceSubsidy.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getRiceAllowance()));
        txtPhoneAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getPhoneAllowance()));
        txtClothingAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getClothingAllowance()));
        txtGrossSemiMonthly.setText(Utility.formatTwoDecimal(selectedEmployee.getGrossSemiMonthlyRate()));
        txtHourlyRate.setText(Utility.formatTwoDecimal(selectedEmployee.getHourlyRate()));
        txtBasicSalary.setText(Utility.formatTwoDecimal(selectedEmployee.getBasicSalary()));
    }
    /**
     * Disables all text fields to prevent editing (read-only view).
     */
    private void disableFields() {
        txtEmployeeNumber.setEditable(false);
        txtFirstName.setEditable(false);
        txtLastName.setEditable(false);
        txtBirthday.setEditable(false);
        txtAddress.setEditable(false);
        txtPosition.setEditable(false);
        txtPhoneNumber.setEditable(false);
        txtStatus.setEditable(false);
        txtImmediateSupervisor.setEditable(false);
        txtSSSNumber.setEditable(false);
        txtPhilHealthNumber.setEditable(false);
        txtTINNumber.setEditable(false);
        txtPagibigNumber.setEditable(false);
        txtPhoneAllowance.setEditable(false);
        txtClothingAllowance.setEditable(false);
        txtGrossSemiMonthly.setEditable(false);
        txtHourlyRate.setEditable(false);
        txtBasicSalary.setEditable(false);
        txtRiceSubsidy.setEditable(false);
    }

    /**
     * Creates the panel displaying employee personal information.
     *
     * @return configured JPanel
     */
    private JPanel createEmployeeDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        panel.add(new JLabel("Employee Number:"));
        txtEmployeeNumber = new JTextField();
        panel.add(txtEmployeeNumber);

        txtLastName = new JTextField();
        panel.add(new JLabel("Last Name:"));
        panel.add(txtLastName);

        txtFirstName = new JTextField();
        panel.add(new JLabel("First Name:"));
        panel.add(txtFirstName);

        txtBirthday = new JTextField();
        panel.add(new JLabel("Birthday:"));
        panel.add(txtBirthday);

        txtAddress = new JTextField();
        panel.add(new JLabel("Address:"));
        panel.add(txtAddress);

        txtPhoneNumber = new JTextField();
        panel.add(new JLabel("Phone Number:"));
        panel.add(txtPhoneNumber);

        txtStatus = new JTextField();
        panel.add(new JLabel("Status:"));
        panel.add(txtStatus);

        txtPosition = new JTextField();
        panel.add(new JLabel("Position:"));
        panel.add(txtPosition);

        txtImmediateSupervisor = new JTextField();
        panel.add(new JLabel("Immediate Supervisor:"));
        panel.add(txtImmediateSupervisor);

        return panel;
    }

    /**
     * Creates the panel displaying government ID details.
     *
     * @return configured JPanel
     */
    private JPanel createGovernmentDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Government Details"));

        txtSSSNumber = new JTextField();
        panel.add(new JLabel("SSS Number:"));
        panel.add(txtSSSNumber);

        txtPhilHealthNumber = new JTextField();
        panel.add(new JLabel("PhilHealth Number:"));
        panel.add(txtPhilHealthNumber);

        txtTINNumber = new JTextField();
        panel.add(new JLabel("TIN Number:"));
        panel.add(txtTINNumber);

        txtPagibigNumber = new JTextField();
        panel.add(new JLabel("Pag-IBIG Number:"));
        panel.add(txtPagibigNumber);

        return panel;
    }

    /**
     * Creates the panel displaying compensation and allowance details.
     *
     * @return configured JPanel
     */
    private JPanel createCompensationDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Compensation Details"));

        txtRiceSubsidy = new JTextField();
        panel.add(new JLabel("Rice Subsidy:"));
        panel.add(txtRiceSubsidy);

        txtPhoneAllowance = new JTextField();
        panel.add(new JLabel("Phone Allowance:"));
        panel.add(txtPhoneAllowance);

        txtClothingAllowance = new JTextField();
        panel.add(new JLabel("Clothing Allowance:"));
        panel.add(txtClothingAllowance);

        txtGrossSemiMonthly = new JTextField();
        panel.add(new JLabel("Gross Semi Monthly Rate:"));
        panel.add(txtGrossSemiMonthly);

        txtHourlyRate = new JTextField();
        panel.add(new JLabel("Hourly Rate:"));
        panel.add(txtHourlyRate);

        txtBasicSalary = new JTextField();
        panel.add(new JLabel("Basic Salary:"));
        panel.add(txtBasicSalary);

        return panel;
    }

    /**
     * Creates the payroll period selection panel with month/year combo boxes
     * and Compute/Cancel buttons.
     *
     * @param employeeId the employee ID for payroll computation
     * @return configured JPanel
     */
    private JPanel createPayrollPanel(String employeeId) {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Payroll"));

        // Month ComboBox
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        JComboBox<String> monthComboBox = new JComboBox<>(months);

        // Year ComboBox (range: current year +/- 5 years)
        int currentYear = Year.now().getValue();
        Integer[] years = IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .boxed().toArray(Integer[]::new);
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);

        Dimension comboSize = new Dimension(150, 25);
        monthComboBox.setPreferredSize(comboSize);
        yearComboBox.setPreferredSize(comboSize);
        yearComboBox.setSelectedItem(currentYear);

        JButton btnCancel = new JButton("Cancel");
        JButton btnCompute = new JButton("Compute");

        btnCancel.setPreferredSize(comboSize);
        btnCompute.setPreferredSize(comboSize);

        btnCancel.addActionListener(e -> NavigationManager.openEmployeeListFrame(this));

        btnCompute.addActionListener(e -> {
            int selectedMonth = monthComboBox.getSelectedIndex() + 1;
            int selectedYear = (Integer) yearComboBox.getSelectedItem();

            LocalDate selectedStartDate = LocalDate.of(selectedYear, selectedMonth, 1);
            LocalDate selectedEndDate = selectedStartDate.withDayOfMonth(selectedStartDate.lengthOfMonth());

            Payroll payroll = new Payroll(selectedEmployee.getEmployeeID(), selectedEmployee, selectedStartDate, selectedEndDate);

            if (payroll.getTotalRegularHours() <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "No salary record found for the selected month.",
                    "Notice",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                NavigationManager.openViewSalaryFrame(this, employeeId, selectedStartDate, selectedEndDate);
            }
        });

        panel.add(new JLabel("Pay Coverage:"));
        panel.add(monthComboBox);
        panel.add(yearComboBox);
        panel.add(new JLabel(""));
        panel.add(btnCancel);
        panel.add(btnCompute);

        return panel;
    }

    /**
     * Clears only employee detail fields.
     */
    private void clearEmployeeFields() {
        txtLastName.setText("");
        txtFirstName.setText("");
        txtBirthday.setText("");
        txtAddress.setText("");
        txtPosition.setText("");
        txtHourlyRate.setText("");
    }

    /**
     * Clears all fields in the form that are declared in this class.
     *
     * <p>The original implementation referenced non-existent fields from
     * {@code PayrollFormMs1} and called {@code JDateChooser.setDate(null)}.
     * Those invalid references have been removed; only actual declared fields
     * are cleared here.</p>
     */
    private void clearAllFields() {
        txtEmployeeNumber.setText("");
        clearEmployeeFields();
        txtSSSNumber.setText("");
        txtPhilHealthNumber.setText("");
        txtPagibigNumber.setText("");
        txtTINNumber.setText("");
        txtRiceSubsidy.setText("");
        txtPhoneAllowance.setText("");
        txtClothingAllowance.setText("");
        txtGrossSemiMonthly.setText("");
        txtBasicSalary.setText("");
        txtPhoneNumber.setText("");
        txtStatus.setText("");
        txtImmediateSupervisor.setText("");
    }

    /**
     * Entry point for standalone testing of this frame.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewEmployeeFrame("").setVisible(true));
    }
}
