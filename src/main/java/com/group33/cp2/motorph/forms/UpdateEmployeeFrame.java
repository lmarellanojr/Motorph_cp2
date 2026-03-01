package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.Allowance;
import com.group33.cp2.motorph.Employee;
import com.group33.cp2.motorph.EmployeeService;
import com.group33.cp2.motorph.GovernmentDetails;
import com.group33.cp2.motorph.NavigationManager;
import com.group33.cp2.motorph.Utility;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * GUI form for updating an existing employee's personal, government, and
 * compensation details.
 *
 * <p>Loads existing data using {@link EmployeeService}, allows editing with
 * validation (date format, government ID patterns, numeric fields), and persists
 * changes to the CSV via {@link EmployeeService#updateEmployee(Employee)}.</p>
 *
 */
public class UpdateEmployeeFrame extends javax.swing.JFrame {

    private final EmployeeService employeeService = new EmployeeService();
    private com.group33.cp2.motorph.Employee selectedEmployee;

    // Personal fields
    private JTextField txtEmployeeNumber;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtBirthday;
    private JTextField txtAddress;
    private JTextField txtPhoneNumber;
    private JTextField txtStatus;
    private JTextField txtPosition;
    private JTextField txtImmediateSupervisor;

    // Government details
    private JTextField txtSSSNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtTINNumber;
    private JTextField txtPagibigNumber;

    // Compensation details
    private JTextField txtBasicSalary;
    private JTextField txtRiceSubsidy;
    private JTextField txtPhoneAllowance;
    private JTextField txtClothingAllowance;
    private JTextField txtGrossSemiMonthly;
    private JTextField txtHourlyRate;

    /**
     * Constructs the UpdateEmployeeFrame and loads the specified employee's data.
     *
     * @param employeeId the ID of the employee to update
     */
    public UpdateEmployeeFrame(String employeeId) {
        setTitle("Update Employee \u2013 MotorPH Employee Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(createEmployeeDetailsPanel());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(createGovernmentDetailsPanel());
        rightPanel.add(createCompensationDetailsPanel());
        rightPanel.add(createUpdatePanel(employeeId));

        setEmployeeDetails(employeeId);
        setGovernmentDetails();
        setCompensationDetails();

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        disableFields();
    }

    private void setEmployeeDetails(String employeeId) {
        txtEmployeeNumber.setText(employeeId);
        selectedEmployee = employeeService.getEmployeeById(employeeId);

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee ID " + employeeId + " was not found.",
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        if (selectedEmployee != null) {
            txtSSSNumber.setText(selectedEmployee.getGovernmentDetails().getSssNumber());
            txtPhilHealthNumber.setText(selectedEmployee.getGovernmentDetails().getPhilHealthNumber());
            txtTINNumber.setText(selectedEmployee.getGovernmentDetails().getTinNumber());
            txtPagibigNumber.setText(selectedEmployee.getGovernmentDetails().getPagibigNumber());
        }
    }

    private void setCompensationDetails() {
        if (selectedEmployee != null) {
            txtBasicSalary.setText(Utility.formatTwoDecimal(selectedEmployee.getBasicSalary()).replace(",", ""));
            txtRiceSubsidy.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowance().getRiceAllowance()).replace(",", ""));
            txtPhoneAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowance().getPhoneAllowance()).replace(",", ""));
            txtClothingAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowance().getClothingAllowance()).replace(",", ""));
            txtGrossSemiMonthly.setText(Utility.formatTwoDecimal(selectedEmployee.getGrossSemiMonthlyRate()).replace(",", ""));
            txtHourlyRate.setText(Utility.formatTwoDecimal(selectedEmployee.getHourlyRate()).replace(",", ""));
        }
    }

    private void disableFields() {
        txtEmployeeNumber.setEditable(false);
    }

    private JPanel createEmployeeDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        panel.add(new JLabel("Employee ID:"));
        txtEmployeeNumber = new JTextField();
        panel.add(txtEmployeeNumber);

        panel.add(new JLabel("Last Name: *"));
        txtLastName = new JTextField();
        panel.add(txtLastName);

        panel.add(new JLabel("First Name: *"));
        txtFirstName = new JTextField();
        panel.add(txtFirstName);

        panel.add(new JLabel("Birthday: *"));
        txtBirthday = new JTextField();
        panel.add(txtBirthday);

        panel.add(new JLabel("Address: *"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        panel.add(txtPhoneNumber);

        panel.add(new JLabel("Status:"));
        txtStatus = new JTextField();
        panel.add(txtStatus);

        panel.add(new JLabel("Position: *"));
        txtPosition = new JTextField();
        panel.add(txtPosition);

        panel.add(new JLabel("Immediate Supervisor:"));
        txtImmediateSupervisor = new JTextField();
        panel.add(txtImmediateSupervisor);

        return panel;
    }

    private JPanel createGovernmentDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Government Details"));

        panel.add(new JLabel("SSS Number: *"));
        txtSSSNumber = new JTextField();
        panel.add(txtSSSNumber);

        panel.add(new JLabel("PhilHealth Number: *"));
        txtPhilHealthNumber = new JTextField();
        panel.add(txtPhilHealthNumber);

        panel.add(new JLabel("TIN Number: *"));
        txtTINNumber = new JTextField();
        panel.add(txtTINNumber);

        panel.add(new JLabel("Pag-IBIG Number: *"));
        txtPagibigNumber = new JTextField();
        panel.add(txtPagibigNumber);

        return panel;
    }

    private JPanel createCompensationDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Compensation Details"));

        panel.add(new JLabel("Basic Salary: *"));
        txtBasicSalary = new JTextField();
        panel.add(txtBasicSalary);

        panel.add(new JLabel("Rice Subsidy:"));
        txtRiceSubsidy = new JTextField();
        panel.add(txtRiceSubsidy);

        panel.add(new JLabel("Phone Allowance:"));
        txtPhoneAllowance = new JTextField();
        panel.add(txtPhoneAllowance);

        panel.add(new JLabel("Clothing Allowance:"));
        txtClothingAllowance = new JTextField();
        panel.add(txtClothingAllowance);

        panel.add(new JLabel("Gross Semi-Monthly: *"));
        txtGrossSemiMonthly = new JTextField();
        panel.add(txtGrossSemiMonthly);

        panel.add(new JLabel("Hourly Rate: *"));
        txtHourlyRate = new JTextField();
        panel.add(txtHourlyRate);

        return panel;
    }

    private JPanel createUpdatePanel(String employeeId) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        Dimension btnSize = new Dimension(120, 35);
        JButton btnCancel = new JButton("Cancel");
        JButton btnUpdate = new JButton("Update");

        btnCancel.setPreferredSize(btnSize);
        btnUpdate.setPreferredSize(btnSize);

        btnCancel.addActionListener((ActionEvent e) -> {
            NavigationManager.openEmployeeListFrame(this);
        });

        btnUpdate.addActionListener((ActionEvent e) -> {
            if (txtFirstName.getText().trim().isEmpty()
                    || txtLastName.getText().trim().isEmpty()
                    || txtBirthday.getText().trim().isEmpty()
                    || txtAddress.getText().trim().isEmpty()
                    || txtPosition.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields (marked *).",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String birthdayText = txtBirthday.getText().trim();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthdayDate;
            try {
                birthdayDate = LocalDate.parse(birthdayText, fmt);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Birthday must be a valid date (MM/dd/yyyy).",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtBirthday.requestFocus();
                return;
            }
            if (birthdayDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "Birthday cannot be in the future.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtBirthday.requestFocus();
                return;
            }

            if (!validateFormats()) {
                return;
            }

            String id = txtEmployeeNumber.getText().trim();
            String last = txtLastName.getText().trim();
            String first = txtFirstName.getText().trim();
            String birthday = txtBirthday.getText().trim();
            String address = txtAddress.getText().trim();
            String phone = txtPhoneNumber.getText().trim();
            String status = txtStatus.getText().trim();
            String position = txtPosition.getText().trim();
            String supervisor = txtImmediateSupervisor.getText().trim();
            if (supervisor.isEmpty()) {
                supervisor = "N/A";
            }

            String sss = txtSSSNumber.getText().trim();
            String phil = txtPhilHealthNumber.getText().trim();
            String tin = txtTINNumber.getText().trim();
            String pagibig = txtPagibigNumber.getText().trim();

            double basicSalary = 0.0;
            double riceSub = 0.0;
            double phoneAllowance = 0.0;
            double clothAllowance = 0.0;
            double grossSemi = 0.0;
            double hourlyRate = 0.0;

            try {
                basicSalary = Double.parseDouble(txtBasicSalary.getText().trim().replace(",", ""));
                if (!txtRiceSubsidy.getText().trim().isEmpty()) {
                    riceSub = Double.parseDouble(txtRiceSubsidy.getText().trim().replace(",", ""));
                }
                if (!txtPhoneAllowance.getText().trim().isEmpty()) {
                    phoneAllowance = Double.parseDouble(txtPhoneAllowance.getText().trim().replace(",", ""));
                }
                if (!txtClothingAllowance.getText().trim().isEmpty()) {
                    clothAllowance = Double.parseDouble(txtClothingAllowance.getText().trim().replace(",", ""));
                }
                grossSemi = Double.parseDouble(txtGrossSemiMonthly.getText().trim().replace(",", ""));
                hourlyRate = Double.parseDouble(txtHourlyRate.getText().trim().replace(",", ""));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numeric values in the compensation fields.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Employee updatedEmployee = new Employee(
                    employeeId, last, first, birthday, address, phone,
                    basicSalary, hourlyRate, grossSemi, status, position, supervisor,
                    new Allowance(employeeId, riceSub, phoneAllowance, clothAllowance),
                    new GovernmentDetails(employeeId, sss, phil, tin, pagibig)
            );

            employeeService.updateEmployee(updatedEmployee);

            JOptionPane.showMessageDialog(this,
                    "Employee \"" + first + " " + last + "\" has been updated successfully!",
                    "Update Success", JOptionPane.INFORMATION_MESSAGE);
            NavigationManager.openEmployeeListFrame(this);
        });

        panel.add(btnCancel);
        panel.add(btnUpdate);
        return panel;
    }

    private boolean validateFormats() {
        String sss = txtSSSNumber.getText().trim();
        if (!sss.matches("\\d{2}-\\d{7}-\\d{1}")) {
            showValidationError("SSS Number should follow format XX-XXXXXXX-X (e.g., 44-4506057-3)", txtSSSNumber);
            return false;
        }

        String tin = txtTINNumber.getText().trim();
        if (!tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}")) {
            showValidationError("TIN Number should follow format XXX-XXX-XXX-XXX (e.g., 442-605-657-000)", txtTINNumber);
            return false;
        }

        String phil = txtPhilHealthNumber.getText().trim();
        if (!phil.matches("\\d{12}")) {
            showValidationError("PhilHealth Number should be 12 digits (e.g., 820126853951)", txtPhilHealthNumber);
            return false;
        }

        String pag = txtPagibigNumber.getText().trim();
        if (!pag.matches("\\d{12}")) {
            showValidationError("Pag-IBIG Number should be 12 digits (e.g., 691295330870)", txtPagibigNumber);
            return false;
        }

        String phone = txtPhoneNumber.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{3}-\\d{3}-\\d{3}")) {
            showValidationError("Phone Number should follow format XXX-XXX-XXX (e.g., 966-860-270)", txtPhoneNumber);
            return false;
        }

        return true;
    }

    private void showValidationError(String message, java.awt.Component component) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
        component.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateEmployeeFrame("12345").setVisible(true));
    }
}
