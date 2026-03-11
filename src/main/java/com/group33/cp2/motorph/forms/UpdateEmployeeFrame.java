package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Allowance;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.GovernmentDetails;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.util.Utility;
import com.group33.cp2.motorph.util.ValidationUtil;

import java.awt.Color;
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

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

// Update Employee form — loads existing data via EmployeeService, validates edits, and persists via updateEmployee().
// The canEditCompensation flag controls whether salary and allowance fields are editable.
// HR callers pass false; Admin and legacy callers pass true.
public class UpdateEmployeeFrame extends javax.swing.JFrame {

    private final EmployeeService employeeService = new EmployeeService();
    private Employee selectedEmployee;

    // Controls whether the six compensation fields are editable.
    // false = HR mode (read-only compensation); true = Admin/full-access mode.
    private final boolean canEditCompensation;

    // Visual indicator colour applied to read-only compensation fields.
    private static final Color READ_ONLY_BG = new Color(220, 220, 220);
    private static final Color READ_ONLY_FG = Color.DARK_GRAY;

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
     * Opens the Update Employee form.
     *
     * @param employeeId        the ID of the employee to edit
     * @param canEditCompensation true if the caller role is permitted to modify
     *                            salary and allowance data; false for HR users
     */
    public UpdateEmployeeFrame(String employeeId, boolean canEditCompensation) {
        this.canEditCompensation = canEditCompensation;

        setTitle("Update Employee \u2013 MotorPH Employee Management System");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        // Always lock the employee ID field — it is never editable.
        disableFields();

        // If the caller role cannot edit compensation, lock all six compensation
        // fields and apply a visual indicator so the user understands they are
        // read-only in this session.
        if (!canEditCompensation) {
            disableCompensationFields();
        }
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
            txtRiceSubsidy.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getRiceAllowance()).replace(",", ""));
            txtPhoneAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getPhoneAllowance()).replace(",", ""));
            txtClothingAllowance.setText(Utility.formatTwoDecimal(selectedEmployee.getAllowanceDetails().getClothingAllowance()).replace(",", ""));
            txtGrossSemiMonthly.setText(Utility.formatTwoDecimal(selectedEmployee.getGrossSemiMonthlyRate()).replace(",", ""));
            txtHourlyRate.setText(Utility.formatTwoDecimal(selectedEmployee.getHourlyRate()).replace(",", ""));
        }
    }

    // Locks the employee ID field — always non-editable regardless of caller role.
    private void disableFields() {
        txtEmployeeNumber.setEditable(false);
    }

    /**
     * Locks all six compensation fields and applies a visual indicator.
     * Called only when canEditCompensation is false (HR mode).
     * The fields still display the current values so HR can see them;
     * they simply cannot be changed.
     */
    private void disableCompensationFields() {
        JTextField[] compensationFields = {
            txtBasicSalary,
            txtRiceSubsidy,
            txtPhoneAllowance,
            txtClothingAllowance,
            txtGrossSemiMonthly,
            txtHourlyRate
        };
        for (JTextField field : compensationFields) {
            field.setEditable(false);
            field.setBackground(READ_ONLY_BG);
            field.setForeground(READ_ONLY_FG);
        }
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
            // Simply close this frame. The parent frame (EmployeeListFrame or a
            // dashboard) remains visible behind us and will refresh itself via
            // the WindowListener it registered on this frame.
            dispose();
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

            double basicSalary;
            double riceSub;
            double phoneAllowance;
            double clothAllowance;
            double grossSemi;
            double hourlyRate;

            if (canEditCompensation) {
                // Admin / full-access path: read compensation values from the form fields.
                try {
                    basicSalary = Double.parseDouble(txtBasicSalary.getText().trim().replace(",", ""));
                    riceSub = txtRiceSubsidy.getText().trim().isEmpty() ? 0.0
                            : Double.parseDouble(txtRiceSubsidy.getText().trim().replace(",", ""));
                    phoneAllowance = txtPhoneAllowance.getText().trim().isEmpty() ? 0.0
                            : Double.parseDouble(txtPhoneAllowance.getText().trim().replace(",", ""));
                    clothAllowance = txtClothingAllowance.getText().trim().isEmpty() ? 0.0
                            : Double.parseDouble(txtClothingAllowance.getText().trim().replace(",", ""));
                    grossSemi = Double.parseDouble(txtGrossSemiMonthly.getText().trim().replace(",", ""));
                    hourlyRate = Double.parseDouble(txtHourlyRate.getText().trim().replace(",", ""));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter valid numeric values in the compensation fields.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (basicSalary <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Basic Salary must be greater than zero.",
                            "Invalid Salary", JOptionPane.WARNING_MESSAGE);
                    txtBasicSalary.requestFocus();
                    return;
                }
                if (hourlyRate <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Hourly Rate must be greater than zero.",
                            "Invalid Rate", JOptionPane.WARNING_MESSAGE);
                    txtHourlyRate.requestFocus();
                    return;
                }
            } else {
                // HR path: compensation fields are locked. Re-use the values that
                // were loaded from the employee record so the save does not alter
                // Salary.csv or Allowance.csv.
                basicSalary    = selectedEmployee.getBasicSalary();
                riceSub        = selectedEmployee.getAllowanceDetails().getRiceAllowance();
                phoneAllowance = selectedEmployee.getAllowanceDetails().getPhoneAllowance();
                clothAllowance = selectedEmployee.getAllowanceDetails().getClothingAllowance();
                grossSemi      = selectedEmployee.getGrossSemiMonthlyRate();
                hourlyRate     = selectedEmployee.getHourlyRate();
            }

            // Delegate subtype selection to EmployeeService.createEmployee():
            // the form no longer decides between RegularEmployee and ProbationaryEmployee.
            Employee updatedEmployee = employeeService.createEmployee(
                    employeeId, last, first, birthday, address, phone,
                    basicSalary, hourlyRate, grossSemi, status, position, supervisor,
                    new Allowance(employeeId, riceSub, phoneAllowance, clothAllowance),
                    new GovernmentDetails(employeeId, sss, phil, tin, pagibig)
            );

            // Pass the caller role so EmployeeService can enforce the compensation
            // guard (Option C). canEditCompensation=false means the caller is HR.
            String callerRole = canEditCompensation ? "ADMIN" : "HR";
            employeeService.updateEmployee(updatedEmployee, callerRole);

            JOptionPane.showMessageDialog(this,
                    "Employee \"" + first + " " + last + "\" has been updated successfully!",
                    "Update Success", JOptionPane.INFORMATION_MESSAGE);
            // Close this frame only. The parent frame refreshes via its
            // WindowListener.windowClosed() handler registered on this frame.
            dispose();
        });

        panel.add(btnCancel);
        panel.add(btnUpdate);
        return panel;
    }

    private boolean validateFormats() {
        String sss = txtSSSNumber.getText().trim();
        if (!ValidationUtil.isValidSSS(sss)) {
            showValidationError("SSS Number should follow format XX-XXXXXXX-X (e.g., 44-4506057-3)", txtSSSNumber);
            return false;
        }

        String tin = txtTINNumber.getText().trim();
        if (!ValidationUtil.isValidTIN(tin)) {
            showValidationError("TIN Number should follow format XXX-XXX-XXX-XXX (e.g., 442-605-657-000)", txtTINNumber);
            return false;
        }

        String phil = txtPhilHealthNumber.getText().trim();
        if (!ValidationUtil.isValidPhilHealth(phil)) {
            showValidationError("PhilHealth Number should be 12 digits (e.g., 820126853951)", txtPhilHealthNumber);
            return false;
        }

        String pag = txtPagibigNumber.getText().trim();
        if (!ValidationUtil.isValidPagIBIG(pag)) {
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

}
