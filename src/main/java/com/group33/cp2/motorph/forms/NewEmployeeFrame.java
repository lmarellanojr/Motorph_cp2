package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.Allowance;
import com.group33.cp2.motorph.Constants;
import com.group33.cp2.motorph.Employee;
import com.group33.cp2.motorph.EmployeeService;
import com.group33.cp2.motorph.GovernmentDetails;
import com.group33.cp2.motorph.NavigationManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Frame for adding a new employee to the MotorPH system.
 * Collects personal, government, and compensation details with validation,
 * then saves the record via {@link EmployeeService}.
 *
 * <p>NOTE: The original source used {@code com.toedter.calendar.JDateChooser} for the
 * birthday field. That dependency is unavailable in Maven Central, so the birthday
 * selector has been replaced with a plain {@link JTextField} accepting MM/dd/yyyy input.</p>
 *
 */
public class NewEmployeeFrame extends javax.swing.JFrame {

    private EmployeeService employeeService;

    // Personal Information Fields (Left Column)
    private JTextField txtEmployeeNumber;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    /** Birthday field (replaced JDateChooser — accepts MM/dd/yyyy text input). */
    private JTextField txtBirthday;
    private JTextField txtAddress;
    private JTextField txtPhoneNumber;
    private JComboBox<String> cmbStatus;
    private JTextField txtPosition;
    private JTextField txtImmediateSupervisor;

    // Government Details Fields (Right Column, Top)
    private JTextField txtSSSNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtTINNumber;
    private JTextField txtPagIBIGNumber;

    // Compensation Details Fields (Right Column, Bottom)
    private JTextField txtRiceSubsidy;
    private JTextField txtPhoneAllowance;
    private JTextField txtClothingAllowance;
    private JTextField txtGrossSemiMonthly;
    private JTextField txtHourlyRate;
    private JTextField txtBasicSalary;

    // Action Buttons
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnClear;

    /**
     * Creates and displays the New Employee data-entry frame.
     */
    public NewEmployeeFrame() {
        employeeService = new EmployeeService();
        initializeComponents();
        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setupLayout();
        setupEventHandlers();
        setDefaultValues();

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
    }

    /**
     * Initialises all GUI components.
     */
    private void initializeComponents() {
        setTitle("Add Employee");

        txtEmployeeNumber = new JTextField(15);
        txtEmployeeNumber.setEditable(false);
        txtEmployeeNumber.setBackground(getBackground().darker());

        txtLastName = new JTextField(20);
        txtFirstName = new JTextField(20);
        // Plain text field replaces JDateChooser (jcalendar not available)
        txtBirthday = new JTextField(15);
        txtBirthday.setToolTipText("Enter birthday as MM/dd/yyyy");

        txtAddress = new JTextField(25);
        txtPhoneNumber = new JTextField(15);

        String[] statusOptions = {"Regular", "Probationary", "Contractual", "Resigned", "Terminated"};
        cmbStatus = new JComboBox<>(statusOptions);
        cmbStatus.setSelectedIndex(0);

        txtPosition = new JTextField(20);
        txtImmediateSupervisor = new JTextField(20);

        txtSSSNumber = new JTextField(15);
        txtPhilHealthNumber = new JTextField(15);
        txtTINNumber = new JTextField(15);
        txtPagIBIGNumber = new JTextField(15);

        txtRiceSubsidy = new JTextField(15);
        txtPhoneAllowance = new JTextField(15);
        txtClothingAllowance = new JTextField(15);
        txtGrossSemiMonthly = new JTextField(15);
        txtHourlyRate = new JTextField(15);
        txtBasicSalary = new JTextField(15);

        btnSave = new JButton("Save Employee");
        btnCancel = new JButton("Cancel");
        btnClear = new JButton("Clear All Fields");

        Dimension buttonSize = new Dimension(130, 30);
        btnSave.setPreferredSize(buttonSize);
        btnCancel.setPreferredSize(buttonSize);
        btnClear.setPreferredSize(buttonSize);
    }

    /**
     * Builds the two-column layout and adds it to the frame.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());

        JPanel leftColumn = createPersonalInformationPanel();
        JPanel rightTopColumn = createGovernmentDetailsPanel();
        JPanel rightBottomColumn = createCompensationDetailsPanel();

        JPanel rightColumn = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(0, 0, 10, 0);
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.weightx = 1.0;
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weighty = 0.5; // Distribute vertical space
        rightColumn.add(rightTopColumn, rightGbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.5; // Distribute vertical space
        rightColumn.add(rightBottomColumn, rightGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 15);
        formPanel.add(leftColumn, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 15, 0, 0);
        formPanel.add(rightColumn, gbc);

        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createPersonalInformationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Employee Number:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmployeeNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Last Name: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtLastName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("First Name: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Birthday (MM/dd/yyyy):"), gbc);
        gbc.gridx = 1;
        panel.add(txtBirthday, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPhoneNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPosition, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Immediate Supervisor:"), gbc);
        gbc.gridx = 1;
        panel.add(txtImmediateSupervisor, gbc);

        return panel;
    }

    private JPanel createGovernmentDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Government Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("SSS Number: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtSSSNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("PhilHealth Number: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtPhilHealthNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("TIN Number: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtTINNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Pag-IBIG Number: *"), gbc);
        gbc.gridx = 1;
        panel.add(txtPagIBIGNumber, gbc);

        return panel;
    }

    private JPanel createCompensationDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Compensation Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Basic Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(txtBasicSalary, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Gross Semi-Monthly:"), gbc);
        gbc.gridx = 1;
        panel.add(txtGrossSemiMonthly, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Hourly Rate:"), gbc);
        gbc.gridx = 1;
        panel.add(txtHourlyRate, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Rice Subsidy:"), gbc);
        gbc.gridx = 1;
        panel.add(txtRiceSubsidy, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Phone Allowance:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPhoneAllowance, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Clothing Allowance:"), gbc);
        gbc.gridx = 1;
        panel.add(txtClothingAllowance, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(btnSave);
        panel.add(btnClear);
        panel.add(btnCancel);
        return panel;
    }

    private void setupEventHandlers() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllFields();
            }
        });

        JFrame currentFrame = this;
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        NewEmployeeFrame.this,
                        "Are you sure you want to cancel? All unsaved data will be lost.",
                        "Confirm Cancel",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    NavigationManager.openEmployeeListFrame(currentFrame);
                }
            }
        });
    }

    private void setDefaultValues() {
        String newEmployeeID = employeeService.generateEmployeeID();
        txtEmployeeNumber.setText(newEmployeeID);

        txtRiceSubsidy.setText("1500.00");
        txtPhoneAllowance.setText("1000.00");
        txtClothingAllowance.setText("1000.00");
        txtBasicSalary.setText("0.00");
        txtHourlyRate.setText("0.00");
        txtGrossSemiMonthly.setText("0.00");

        txtLastName.setText("");
        txtFirstName.setText("");
        txtBirthday.setText("");
        txtAddress.setText("");
        txtPhoneNumber.setText("");
        txtPosition.setText("");
        txtImmediateSupervisor.setText("");
        cmbStatus.setSelectedIndex(0);

        txtSSSNumber.setText("");
        txtPhilHealthNumber.setText("");
        txtTINNumber.setText("");
        txtPagIBIGNumber.setText("");

        txtLastName.requestFocus();
    }

    private void saveEmployee() {
        try {
            if (!validateRequiredFields()) {
                return;
            }
            if (!validateFormats()) {
                return;
            }
            if (!validateNumericFields()) {
                return;
            }

            Employee newEmployee = createEmployeeFromForm();
            employeeService.addEmployee(newEmployee);

            JOptionPane.showMessageDialog(
                    this,
                    "Employee " + newEmployee.getFirstName() + " has been successfully added!\n"
                    + "Employee ID: " + newEmployee.getEmployeeID() + "\n"
                    + "The employee data has been saved to the CSV file.",
                    "Employee Added Successfully",
                    JOptionPane.INFORMATION_MESSAGE
            );

            NavigationManager.openEmployeeListFrame(this);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please check that all numeric fields contain valid numbers.\n"
                    + "Error details: " + e.getMessage(),
                    "Invalid Number Format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred while saving the employee:\n" + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validateRequiredFields() {
        if (txtLastName.getText().trim().isEmpty()) {
            showValidationError("Last Name is required.", txtLastName);
            return false;
        }
        if (txtFirstName.getText().trim().isEmpty()) {
            showValidationError("First Name is required.", txtFirstName);
            return false;
        }
        if (txtSSSNumber.getText().trim().isEmpty()) {
            showValidationError("SSS Number is required.", txtSSSNumber);
            return false;
        }
        if (txtPhilHealthNumber.getText().trim().isEmpty()) {
            showValidationError("PhilHealth Number is required.", txtPhilHealthNumber);
            return false;
        }
        if (txtTINNumber.getText().trim().isEmpty()) {
            showValidationError("TIN Number is required.", txtTINNumber);
            return false;
        }
        if (txtPagIBIGNumber.getText().trim().isEmpty()) {
            showValidationError("Pag-IBIG Number is required.", txtPagIBIGNumber);
            return false;
        }
        return true;
    }

    private boolean validateNumericFields() {
        try {
            Double.parseDouble(txtBasicSalary.getText().trim().replace(",", ""));
            Double.parseDouble(txtHourlyRate.getText().trim().replace(",", ""));
            Double.parseDouble(txtGrossSemiMonthly.getText().trim().replace(",", ""));
            Double.parseDouble(txtRiceSubsidy.getText().trim().replace(",", ""));
            Double.parseDouble(txtPhoneAllowance.getText().trim().replace(",", ""));
            Double.parseDouble(txtClothingAllowance.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for all salary and allowance fields. Do not include currency symbols.",
                    "Invalid Numeric Input", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateFormats() {
        String sss = txtSSSNumber.getText().trim();
        if (!sss.matches("\\d{2}-\\d{7}-\\d{1}")) {
            showValidationError("SSS Number should follow format: XX-XXXXXXX-X (e.g., 44-4506057-3)", txtSSSNumber);
            return false;
        }

        String tin = txtTINNumber.getText().trim();
        if (!tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}")) {
            showValidationError("TIN Number should follow format: XXX-XXX-XXX-XXX (e.g., 442-605-657-000)", txtTINNumber);
            return false;
        }

        String philhealth = txtPhilHealthNumber.getText().trim();
        if (!philhealth.matches("\\d{12}")) {
            showValidationError("PhilHealth Number should be 12 digits (e.g., 820126853951)", txtPhilHealthNumber);
            return false;
        }

        String pagibig = txtPagIBIGNumber.getText().trim();
        if (!pagibig.matches("\\d{12}")) {
            showValidationError("Pag-IBIG Number should be 12 digits (e.g., 691295330870)", txtPagIBIGNumber);
            return false;
        }

        return true;
    }

    private Employee createEmployeeFromForm() {
        String employeeID = txtEmployeeNumber.getText().trim();
        String lastName = txtLastName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String birthday = txtBirthday.getText().trim();
        String address = txtAddress.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        String position = txtPosition.getText().trim();
        String immediateSupervisor = txtImmediateSupervisor.getText().trim();

        double basicSalary = Double.parseDouble(txtBasicSalary.getText().trim().replace(",", ""));
        double hourlyRate = Double.parseDouble(txtHourlyRate.getText().trim().replace(",", ""));
        double grossSemiMonthly = Double.parseDouble(txtGrossSemiMonthly.getText().trim().replace(",", ""));
        double riceSubsidy = Double.parseDouble(txtRiceSubsidy.getText().trim().replace(",", ""));
        double phoneAllowance = Double.parseDouble(txtPhoneAllowance.getText().trim().replace(",", ""));
        double clothingAllowance = Double.parseDouble(txtClothingAllowance.getText().trim().replace(",", ""));

        GovernmentDetails governmentDetails = new GovernmentDetails(
                employeeID,
                txtSSSNumber.getText().trim(),
                txtPhilHealthNumber.getText().trim(),
                txtTINNumber.getText().trim(),
                txtPagIBIGNumber.getText().trim()
        );

        Allowance allowance = new Allowance(employeeID, riceSubsidy, phoneAllowance, clothingAllowance);

        return new Employee(
                employeeID,
                lastName,
                firstName,
                birthday,
                address,
                phoneNumber,
                basicSalary,
                hourlyRate,
                grossSemiMonthly,
                status,
                position,
                immediateSupervisor,
                allowance,
                governmentDetails
        );
    }

    /**
     * Clears all editable form fields and resets defaults.
     */
    private void clearAllFields() {
        txtLastName.setText("");
        txtFirstName.setText("");
        txtBirthday.setText("");    // Replaced dateChooserBirthday.setDate(null)
        txtAddress.setText("");
        txtPhoneNumber.setText("");
        cmbStatus.setSelectedIndex(0);
        txtPosition.setText("");
        txtImmediateSupervisor.setText("");

        txtSSSNumber.setText("");
        txtPhilHealthNumber.setText("");
        txtTINNumber.setText("");
        txtPagIBIGNumber.setText("");

        txtBasicSalary.setText("");
        txtGrossSemiMonthly.setText("");
        txtHourlyRate.setText("");
        txtRiceSubsidy.setText("");
        txtPhoneAllowance.setText("");
        txtClothingAllowance.setText("");

        setDefaultValues();
    }

    private void showValidationError(String message, java.awt.Component component) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
        component.requestFocus();
    }
}
