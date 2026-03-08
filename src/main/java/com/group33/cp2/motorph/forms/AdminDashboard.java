package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Admin;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.Report;
import com.group33.cp2.motorph.service.EmployeeService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Admin Dashboard — the primary screen for Admin department employees.
 *
 * <p>Provides two tabs:</p>
 * <ol>
 *   <li><strong>Employee Management</strong> — view all employees in a table;
 *       add, edit, or deactivate records by delegating to
 *       {@link Admin#manageUsers(int, String)}.</li>
 *   <li><strong>System Reports</strong> — choose a report type (Payroll or Attendance),
 *       click Generate, and view the formatted output via
 *       {@link Admin#generateSystemReport(String)}.</li>
 * </ol>
 *
 * <p><strong>OOP Pillar — Polymorphism:</strong> Employee management and report
 * generation are invoked through the {@link com.group33.cp2.motorph.AdminOperations}
 * interface methods on the {@code adminUser} object. The dashboard does not
 * duplicate any business logic — it purely delegates to the domain layer.</p>
 *
 * @author Group13
 * @version 1.0
 */
public class AdminDashboard extends JFrame {

    private final Admin           adminUser;
    private final EmployeeService employeeService;

    // Tab 1 — Employee Management
    private JTable          employeeTable;
    private DefaultTableModel employeeModel;

    // Tab 2 — System Reports
    private JComboBox<String> cmbReportType;
    private JTextArea         reportArea;

    // Column index constants for the employee table
    private static final int COL_EMP_ID   = 0;
    private static final int COL_NAME     = 1;
    private static final int COL_POSITION = 2;
    private static final int COL_STATUS   = 3;

    private static final String[] REPORT_TYPES = {"payroll", "attendance"};

    /**
     * Constructs the AdminDashboard.
     *
     * @param adminUser       the logged-in Admin employee; must not be null
     * @param employeeService the employee data service; must not be null
     */
    public AdminDashboard(Admin adminUser, EmployeeService employeeService) {
        this.adminUser       = adminUser;
        this.employeeService = employeeService;

        setTitle("Admin Dashboard — " + adminUser.getFullName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        AdminDashboard.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        buildUI();
        loadEmployeeTable();
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel header = buildHeaderPanel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employee Management", buildEmployeeTab());
        tabs.addTab("System Reports",      buildReportsTab());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(tabs,   BorderLayout.CENTER);
        getContentPane().add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel welcome = new JLabel("Welcome, " + adminUser.getFullName(), SwingConstants.RIGHT);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(title, BorderLayout.WEST);
        panel.add(welcome, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this, "Log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                NavigationManager.openLoginFrame(this);
            }
        });
        panel.add(btnLogout);
        return panel;
    }

    // =========================================================================
    //  Tab 1: Employee Management
    // =========================================================================

    private JPanel buildEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        employeeModel = new DefaultTableModel(
                new String[]{"Employee ID", "Full Name", "Position", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        employeeTable = new JTable(employeeModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JButton btnAdd        = new JButton("Add Employee");
        JButton btnEdit       = new JButton("Edit Employee");
        JButton btnDeactivate = new JButton("Deactivate Employee");
        JButton btnRefresh    = new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            // Delegates to Admin.manageUsers("create") — opens NewEmployeeFrame
            adminUser.manageUsers(0, "create");
        });

        btnEdit.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empId = employeeModel.getValueAt(row, COL_EMP_ID).toString();
            int userId;
            try {
                userId = Integer.parseInt(empId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid employee ID: " + empId,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Delegates to Admin.manageUsers("update") — opens UpdateEmployeeFrame
            adminUser.manageUsers(userId, "update");
        });

        btnDeactivate.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to deactivate.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empId  = employeeModel.getValueAt(row, COL_EMP_ID).toString();
            String status = employeeModel.getValueAt(row, COL_STATUS).toString();
            if ("Inactive".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Employee is already inactive.",
                        "No Change", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Deactivate employee " + empId + "? This sets their status to Inactive.",
                    "Confirm Deactivation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            int userId;
            try {
                userId = Integer.parseInt(empId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid employee ID: " + empId,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Delegates to Admin.manageUsers("deactivate")
            boolean ok = adminUser.manageUsers(userId, "deactivate");
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Employee " + empId + " has been set to Inactive.",
                        "Deactivated", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not deactivate employee " + empId + ".",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> {
            employeeService.reloadEmployees();
            loadEmployeeTable();
        });

        // Change A — Delete Employee button declaration
        JButton btnDelete = new JButton("Delete Employee");

        // Change B — Delete Employee action listener
        btnDelete.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empId = employeeModel.getValueAt(row, COL_EMP_ID).toString();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Permanently delete employee " + empId + "? This cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = employeeService.deleteEmployee(empId);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Employee " + empId + " has been permanently deleted.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete employee " + empId + ".",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonBar.add(btnAdd);
        buttonBar.add(btnEdit);
        buttonBar.add(btnDeactivate);
        buttonBar.add(btnRefresh);
        // Change C — add Delete button to the button bar
        buttonBar.add(btnDelete);

        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);
        return panel;
    }

    private void loadEmployeeTable() {
        employeeModel.setRowCount(0);
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            employeeModel.addRow(new Object[]{
                emp.getEmployeeID(),
                emp.getFullName(),
                emp.getPosition(),
                emp.getStatus()
            });
        }
    }

    // =========================================================================
    //  Tab 2: System Reports
    // =========================================================================

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls bar: report type selector + Generate button
        JLabel lblType = new JLabel("Report Type:");
        cmbReportType = new JComboBox<>(new String[]{"Payroll Report", "Attendance Report"});
        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.addActionListener(e -> handleGenerateReport());

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlBar.add(lblType);
        controlBar.add(cmbReportType);
        controlBar.add(btnGenerate);

        // Output area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setText("Select a report type and click Generate Report.");

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Report Output"));

        panel.add(controlBar, BorderLayout.NORTH);
        panel.add(scrollPane,  BorderLayout.CENTER);
        return panel;
    }

    /**
     * Generates the selected report type by calling
     * {@link Admin#generateSystemReport(String)} and displays the result.
     */
    private void handleGenerateReport() {
        int selectedIndex = cmbReportType.getSelectedIndex();
        // Map combo index to report type key
        String reportType = REPORT_TYPES[selectedIndex]; // "payroll" or "attendance"

        Report report = adminUser.generateSystemReport(reportType);
        reportArea.setText(report.getContent());
        reportArea.setCaretPosition(0);
    }
}
