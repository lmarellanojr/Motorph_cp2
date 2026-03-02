package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.CompensationDetails;
import com.group33.cp2.motorph.Constants;
import com.group33.cp2.motorph.Employee;
import com.group33.cp2.motorph.EmployeeService;
import com.group33.cp2.motorph.NavigationManager;
import com.group33.cp2.motorph.Payroll;
import com.group33.cp2.motorph.PayrollStatus;
import com.group33.cp2.motorph.Utility;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.border.TitledBorder;

/**
 * ViewSalaryFrame displays an employee's salary computation details for
 * a selected payroll period. This GUI includes personal information, salary
 * breakdown, deductions, and net pay based on calculated data.
 *
 * <p>It is part of the MotorPH Employee Payroll System.</p>
 *
 */
public class ViewSalaryFrame extends javax.swing.JFrame {

    /** Handles employee data retrieval. */
    EmployeeService employeeService = new EmployeeService();
    /** Currently selected employee. */
    Employee selectedEmployee;

    // GUI fields for employee info (left panel)
    private JTextField txtEmployeeNumber;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtBirthday;
    private JTextField txtAddress;
    private JTextField txtPhoneNumber;
    private JTextField txtSSSNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtTIN;
    private JTextField txtPagIbigNumber;
    private JTextField txtStatus;
    private JTextField txtPosition;
    private JTextField txtImmediateSupervisor;
    private JTextField txtBasicSalary;
    private JTextField txtRiceAllowance;
    private JTextField txtPhoneAllowance;
    private JTextField txtClothingAllowance;
    private JTextField txtGrossSemiMonthly;
    private JTextField txtHourlyRate;

    // Computed result labels (right panel)
    private JLabel lblRegularHours;
    private JLabel lblOvertimeHours;
    private JLabel lblRegularPay;
    private JLabel lblNetSalary;
    private JLabel lblOvertimePay;
    private JLabel lblGrossSalary;
    private JLabel lblSSSDeductions;
    private JLabel lblPhilHealthDeductions;
    private JLabel lblPagIBIGDeductions;
    private JLabel lblWithholdingTax;
    private JLabel lblRiceAllowance2;
    private JLabel lblClothingAllowance2;
    private JLabel lblPayrollStatus;
    private JLabel lblPhoneAllowance2;
    /** Payroll period start date display label. */
    private JLabel dateFrom;

    /**
     * Constructs the ViewSalaryFrame window, which displays an employee's
     * payroll details based on the selected date range.
     *
     * @param employeeId        the ID of the employee
     * @param selectedStartDate the first day of the pay period
     * @param selectEndDate     the last day of the pay period
     */
    public ViewSalaryFrame(String employeeId, LocalDate selectedStartDate, LocalDate selectEndDate) {
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

        // Main layout: 2 columns with spacing
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));

        // Left panel: employee info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(createEmployeeDetailsPanel());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Right panel: all payroll computation results
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, Color.WHITE));
        rightPanel.add(createComputationPanel());
        rightPanel.add(createHoursPanel());
        rightPanel.add(createPayBreakdownPanel());
        rightPanel.add(createDeductionsPanel());
        rightPanel.add(createAllowancesPanel());
        rightPanel.add(createStatusPanel());

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        disableFields();

        // Use selectedEmployee.getEmployeeID() to set the text field
        txtEmployeeNumber.setText(selectedEmployee.getEmployeeID());
        setEmployeeDetailsTextFields();

        // Format and display the selected payroll period
        if (selectedStartDate != null) {
            Date date = Date.from(selectedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
            String formattedDate = formatter.format(date);
            dateFrom.setText(formattedDate);

            compute(selectedStartDate, selectEndDate);
        }
    }

    /**
     * Disables all text fields to make them read-only.
     */
    private void disableFields() {
        txtEmployeeNumber.setEditable(false);
        txtFirstName.setEditable(false);
        txtLastName.setEditable(false);
        txtBirthday.setEditable(false);
        txtAddress.setEditable(false);
        txtPhoneNumber.setEditable(false);
        txtStatus.setEditable(false);
        txtPosition.setEditable(false);
        txtImmediateSupervisor.setEditable(false);
        txtSSSNumber.setEditable(false);
        txtPhilHealthNumber.setEditable(false);
        txtTIN.setEditable(false);
        txtPagIbigNumber.setEditable(false);
        txtBasicSalary.setEditable(false);
        txtRiceAllowance.setEditable(false);
        txtPhoneAllowance.setEditable(false);
        txtClothingAllowance.setEditable(false);
        txtGrossSemiMonthly.setEditable(false);
        txtHourlyRate.setEditable(false);
    }

    /**
     * Loads employee data into the form fields.
     *
     * @param employeeNumber the employee ID to look up
     */
    private void setEmployeeDetailsTextFields() {
        txtLastName.setText(selectedEmployee.getLastName());
        txtFirstName.setText(selectedEmployee.getFirstName());
        txtBirthday.setText(selectedEmployee.getBirthday());
        txtAddress.setText(selectedEmployee.getAddress());
        txtPhoneNumber.setText(selectedEmployee.getPhoneNumber());
        txtSSSNumber.setText(selectedEmployee.getGovernmentDetails().getSssNumber());
        txtPhilHealthNumber.setText(selectedEmployee.getGovernmentDetails().getPhilHealthNumber());
        txtTIN.setText(selectedEmployee.getGovernmentDetails().getTinNumber());
        txtPagIbigNumber.setText(selectedEmployee.getGovernmentDetails().getPagibigNumber());
        txtStatus.setText(selectedEmployee.getStatus());
        txtPosition.setText(selectedEmployee.getPosition());
        txtImmediateSupervisor.setText(selectedEmployee.getImmediateSupervisor());
        txtBasicSalary.setText(Utility.formatTwoDecimal(selectedEmployee.getBasicSalary()));
        txtRiceAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getRiceAllowance()));
        txtPhoneAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getPhoneAllowance()));
        txtClothingAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getClothingAllowance()));
        txtGrossSemiMonthly.setText(Utility.formatTwoDecimal(selectedEmployee.getGrossSemiMonthlyRate()));
    }

    /**
     * Creates the form panel for displaying employee details.
     *
     * @return configured JPanel
     */
    private JPanel createEmployeeDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(19, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        panel.add(new JLabel("Employee Number:"));
        txtEmployeeNumber = new JTextField();
        panel.add(txtEmployeeNumber);

        panel.add(new JLabel("Last Name:"));
        txtLastName = new JTextField();
        panel.add(txtLastName);

        panel.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        panel.add(txtFirstName);

        panel.add(new JLabel("Birthday:"));
        txtBirthday = new JTextField();
        panel.add(txtBirthday);

        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        panel.add(txtPhoneNumber);

        panel.add(new JLabel("SSS Number:"));
        txtSSSNumber = new JTextField();
        panel.add(txtSSSNumber);

        panel.add(new JLabel("PhilHealth Number:"));
        txtPhilHealthNumber = new JTextField();
        panel.add(txtPhilHealthNumber);

        panel.add(new JLabel("TIN Number:"));
        txtTIN = new JTextField();
        panel.add(txtTIN);

        panel.add(new JLabel("Pag-IBIG Number:"));
        txtPagIbigNumber = new JTextField();
        panel.add(txtPagIbigNumber);

        panel.add(new JLabel("Status:"));
        txtStatus = new JTextField();
        panel.add(txtStatus);

        panel.add(new JLabel("Position:"));
        txtPosition = new JTextField();
        panel.add(txtPosition);

        panel.add(new JLabel("Immediate Supervisor:"));
        txtImmediateSupervisor = new JTextField();
        panel.add(txtImmediateSupervisor);

        panel.add(new JLabel("Basic Salary:"));
        txtBasicSalary = new JTextField();
        panel.add(txtBasicSalary);

        panel.add(new JLabel("Rice Subsidy:"));
        txtRiceAllowance = new JTextField();
        panel.add(txtRiceAllowance);

        panel.add(new JLabel("Phone Allowance:"));
        txtPhoneAllowance = new JTextField();
        panel.add(txtPhoneAllowance);

        panel.add(new JLabel("Clothing Allowance:"));
        txtClothingAllowance = new JTextField();
        panel.add(txtClothingAllowance);

        panel.add(new JLabel("Gross Semi-Monthly:"));
        txtGrossSemiMonthly = new JTextField();
        panel.add(txtGrossSemiMonthly);

        panel.add(new JLabel("Hourly Rate:"));
        txtHourlyRate = new JTextField();
        panel.add(txtHourlyRate);

        return panel;
    }

    /**
     * Creates the computation summary header panel showing pay coverage period.
     *
     * @return configured JPanel
     */
    private JPanel createComputationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBackground(Color.white);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                "Computed Salary Summary"
        );
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.ABOVE_TOP);
        border.setTitleFont(new Font("Arial", Font.BOLD, 16));
        panel.setBorder(border);

        panel.add(new JLabel("Pay Coverage:"));
        dateFrom = new JLabel();
        panel.add(dateFrom);

        return panel;
    }

    /**
     * Creates the panel displaying all payroll deductions.
     *
     * @return configured JPanel
     */
    private JPanel createDeductionsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                "Deductions"
        ));

        panel.add(new JLabel("SSS:"));
        lblSSSDeductions = new JLabel();
        panel.add(lblSSSDeductions);

        panel.add(new JLabel("PhilHealth:"));
        lblPhilHealthDeductions = new JLabel();
        panel.add(lblPhilHealthDeductions);

        panel.add(new JLabel("Pag-IBIG:"));
        lblPagIBIGDeductions = new JLabel();
        panel.add(lblPagIBIGDeductions);

        panel.add(new JLabel("Withholding Tax:"));
        lblWithholdingTax = new JLabel();
        panel.add(lblWithholdingTax);

        return panel;
    }

    /**
     * Creates the panel showing total regular and overtime hours.
     *
     * @return configured JPanel
     */
    private JPanel createHoursPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                "Hours"
        ));

        panel.add(new JLabel("Regular Hours:"));
        lblRegularHours = new JLabel();
        panel.add(lblRegularHours);

        panel.add(new JLabel("Overtime Hours:"));
        lblOvertimeHours = new JLabel();
        panel.add(lblOvertimeHours);

        return panel;
    }

    /**
     * Creates the panel showing regular pay, overtime pay, and gross salary.
     *
     * @return configured JPanel
     */
    private JPanel createPayBreakdownPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                "Pay Breakdown"
        ));

        panel.add(new JLabel("Regular Pay:"));
        lblRegularPay = new JLabel();
        panel.add(lblRegularPay);

        panel.add(new JLabel("Overtime Pay:"));
        lblOvertimePay = new JLabel();
        panel.add(lblOvertimePay);

        panel.add(new JLabel("Gross Salary:"));
        lblGrossSalary = new JLabel();
        panel.add(lblGrossSalary);

        return panel;
    }

    /**
     * Creates the panel showing allowances added to salary.
     *
     * @return configured JPanel
     */
    private JPanel createAllowancesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                "Allowances"
        ));

        panel.add(new JLabel("Rice:"));
        lblRiceAllowance2 = new JLabel();
        panel.add(lblRiceAllowance2);

        panel.add(new JLabel("Phone:"));
        lblPhoneAllowance2 = new JLabel();
        panel.add(lblPhoneAllowance2);

        panel.add(new JLabel("Clothing:"));
        lblClothingAllowance2 = new JLabel();
        panel.add(lblClothingAllowance2);

        return panel;
    }

    /**
     * Creates the panel displaying net salary, payroll status, and the Back button.
     *
     * @return configured JPanel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 100, 10, 10),
                ""
        ));

        JLabel lblNetSalaryTitle = new JLabel("Net Salary:");
        lblNetSalaryTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblNetSalaryTitle);

        lblNetSalary = new JLabel();
        lblNetSalary.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblNetSalary);

        panel.add(new JLabel("Payroll Status:"));
        lblPayrollStatus = new JLabel(PayrollStatus.PENDING.toString());
        panel.add(lblPayrollStatus);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> NavigationManager.openEmployeeListFrame(this));

        panel.add(new JLabel(""));
        panel.add(btnBack);

        return panel;
    }

    /**
     * Computes and displays the employee's payroll for the given date range.
     *
     * @param selectedStartDate first day of the payroll period
     * @param selectedEndDate   last day of the payroll period
     */
    private void compute(LocalDate selectedStartDate, LocalDate selectedEndDate) {
        Payroll payroll = new Payroll(selectedEmployee.getEmployeeID(), selectedEmployee, selectedStartDate, selectedEndDate);

        payroll.calculateNetSalary();

        if (payroll.getTotalRegularHours() <= 0) {
            NavigationManager.openEmployeeListFrame(this);
            JOptionPane.showMessageDialog(
                this,
                "No salary record found for the selected month.",
                "Notice",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Format work hours
        String regularHours = Utility.formatTwoDecimal(payroll.getTotalRegularHours());
        String overtimeHours = Utility.formatTwoDecimal(payroll.getTotalOvertimeHours());

        CompensationDetails compensationDetails = payroll.getCompensationDetails();

        // Format pay components
        String regularPay = Utility.formatTwoDecimal(compensationDetails.getRegularPay());
        String overtimePay = Utility.formatTwoDecimal(compensationDetails.getOvertimePay());
        String grossSalary = Utility.formatTwoDecimal(compensationDetails.getGrossSalary());

        // Format deductions
        String sss = Utility.formatTwoDecimal(compensationDetails.getDeductions().getSss());
        String pagibig = Utility.formatTwoDecimal(compensationDetails.getDeductions().getPagIbig());
        String philhealth = Utility.formatTwoDecimal(compensationDetails.getDeductions().getPhilHealth());
        String tax = Utility.formatTwoDecimal(compensationDetails.getDeductions().getTax());

        // Format allowances
        String rice = Utility.formatTwoDecimal(compensationDetails.getAllowance().getRiceAllowance());
        String phone = Utility.formatTwoDecimal(compensationDetails.getAllowance().getPhoneAllowance());
        String clothing = Utility.formatTwoDecimal(compensationDetails.getAllowance().getClothingAllowance());

        String netPay = Utility.formatTwoDecimal(compensationDetails.getNetSalary());

        // Update UI labels with calculated values
        lblRegularHours.setText(regularHours);
        lblOvertimeHours.setText(overtimeHours);
        lblRegularPay.setText(regularPay);
        lblOvertimePay.setText(overtimePay);
        lblGrossSalary.setText(grossSalary);

        lblSSSDeductions.setText(sss);
        lblPagIBIGDeductions.setText(pagibig);
        lblPhilHealthDeductions.setText(philhealth);
        lblWithholdingTax.setText(tax);

        lblRiceAllowance2.setText(rice);
        lblClothingAllowance2.setText(clothing);
        lblPhoneAllowance2.setText(phone);

        lblNetSalary.setText(netPay);
        lblPayrollStatus.setText(payroll.getStatus().toString());
    }

    /**
     * Entry point for standalone testing of this frame.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewSalaryFrame("", null, null).setVisible(true));
    }
}
