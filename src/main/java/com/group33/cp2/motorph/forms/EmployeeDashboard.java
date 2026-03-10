package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Allowance;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.GovernmentDetails;
import com.group33.cp2.motorph.model.LeaveRequest;
import com.group33.cp2.motorph.model.Payroll;
import com.group33.cp2.motorph.model.Payslip;
import com.group33.cp2.motorph.model.SalaryDetails;
import com.group33.cp2.motorph.service.LeaveService;
import com.group33.cp2.motorph.service.PayrollCalculatorService;
import com.group33.cp2.motorph.service.TimeTrackingService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Employee self-service dashboard.
 *
 * <p>Provides four tabs:</p>
 * <ol>
 *   <li><strong>My Info</strong> — read-only personal and employment details.</li>
 *   <li><strong>My Payslip</strong> — gross salary, deductions, and net pay summary.</li>
 *   <li><strong>Time Tracking</strong> — clock in/out buttons and a time-log history table.</li>
 *   <li><strong>Leave</strong> — current balances, request submission form, and request history.</li>
 * </ol>
 *
 * <p><strong>OOP Pillar — Polymorphism:</strong> Constructor accepts any {@link Employee}
 * subtype; all tabs operate via the abstract {@code Employee} interface, dispatching to
 * the correct concrete implementation ({@link com.group33.cp2.motorph.model.RegularEmployee}
 * vs. {@link com.group33.cp2.motorph.model.ProbationaryEmployee}) at runtime.</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The payslip tab delegates all computation
 * to {@link com.group33.cp2.motorph.service.PayrollCalculatorService} via a
 * {@link javax.swing.SwingWorker}. The dashboard has no knowledge of CSV layouts or
 * deduction formulas — it only observes the resulting {@link com.group33.cp2.motorph.model.SalaryDetails}
 * record.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class EmployeeDashboard extends JFrame {

    private final Employee          employee;
    private final LeaveService       leaveService;
    private final TimeTrackingService timeTrackingService;

    // Tab 3 — Time tracking state
    private JTable timeLogTable;
    private DefaultTableModel timeLogModel;

    // Tab 4 — Leave state
    private JLabel lblSickBalance;
    private JLabel lblVacationBalance;
    private JLabel lblBirthdayBalance;

    // Tab 2 — Payslip labels (populated by SwingWorker)
    private JLabel lblPayBasic;
    private JLabel lblPayAllowances;
    private JLabel lblPayGross;
    private JLabel lblPaySSS;
    private JLabel lblPayPhilHealth;
    private JLabel lblPayPagibig;
    private JLabel lblPayTax;
    private JLabel lblPayTotalDed;
    private JLabel lblPayNet;

    private JComboBox<String> cmbLeaveType;
    private JTextField txtLeaveStartDate;
    private JTextField txtLeaveEndDate;
    private JTextField txtLeaveReason;
    private JTable leaveHistoryTable;
    private DefaultTableModel leaveHistoryModel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] LEAVE_TYPES = {"Sick Leave", "Vacation Leave", "Birthday Leave"};

    /**
     * Bundles leave-balance integers and leave-request list loaded from CSV so
     * both can be returned from a single {@link SwingWorker#doInBackground()} call.
     */
    private record LeaveData(int sick, int vacation, int birthday, List<LeaveRequest> requests) {}

    /**
     * Constructs the EmployeeDashboard for the given employee.
     *
     * @param employee the logged-in employee; must not be null
     */
    public EmployeeDashboard(Employee employee) {
        this.employee           = employee;
        this.leaveService       = new LeaveService();
        this.timeTrackingService = new TimeTrackingService();

        setTitle("Employee Dashboard — " + employee.getFullName());
        setSize(900, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        EmployeeDashboard.this,
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
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel header = buildHeaderPanel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Info",       buildInfoTab());
        tabs.addTab("My Payslip",    buildPayslipTab());
        tabs.addTab("Time Tracking", buildTimeTrackingTab());
        tabs.addTab("Leave",         buildLeaveTab());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(tabs,   BorderLayout.CENTER);
        getContentPane().add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel title = new JLabel("Employee Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel welcome = new JLabel("Welcome, " + employee.getFullName(), SwingConstants.RIGHT);
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
    //  Tab 1: My Info
    // =========================================================================

    private JScrollPane buildInfoTab() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        GovernmentDetails gov = employee.getGovernmentDetails();
        Allowance allowance   = employee.getAllowanceDetails();

        panel.add(infoSection("Personal Information", new String[][]{
            {"Employee ID",  employee.getEmployeeID()},
            {"Full Name",    employee.getFullName()},
            {"Birthday",     employee.getBirthday()},
            {"Address",      employee.getAddress()},
            {"Phone",        employee.getPhoneNumber()}
        }));
        panel.add(infoSection("Employment Details", new String[][]{
            {"Position",    employee.getPosition()},
            {"Status",      employee.getStatus()},
            {"Supervisor",  employee.getImmediateSupervisor()}
        }));
        panel.add(infoSection("Government IDs", new String[][]{
            {"SSS Number",         gov != null ? gov.getSssNumber()        : "N/A"},
            {"PhilHealth Number",  gov != null ? gov.getPhilHealthNumber() : "N/A"},
            {"TIN",                gov != null ? gov.getTinNumber()        : "N/A"},
            {"Pag-IBIG Number",    gov != null ? gov.getPagibigNumber()    : "N/A"}
        }));
        panel.add(infoSection("Allowances", new String[][]{
            {"Rice Allowance",     allowance != null ? String.format("%.2f", allowance.getRiceAllowance())     : "0.00"},
            {"Phone Allowance",    allowance != null ? String.format("%.2f", allowance.getPhoneAllowance())    : "0.00"},
            {"Clothing Allowance", allowance != null ? String.format("%.2f", allowance.getClothingAllowance()) : "0.00"}
        }));

        return new JScrollPane(panel);
    }

    /**
     * Builds a titled section of label-value pairs.
     */
    private JPanel infoSection(String title, String[][] fields) {
        JPanel section = new JPanel(new GridLayout(0, 2, 10, 4));
        section.setBorder(BorderFactory.createTitledBorder(title));
        for (String[] field : fields) {
            JLabel lbl = new JLabel(field[0] + ":");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            JLabel val = new JLabel(field[1]);
            section.add(lbl);
            section.add(val);
        }
        return section;
    }

    // =========================================================================
    //  Tab 2: My Payslip
    // =========================================================================

    private JScrollPane buildPayslipTab() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Build grid of label-value pairs; mutable labels assigned to instance fields
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 4));
        grid.setBorder(BorderFactory.createTitledBorder("Payslip Summary"));

        lblPayBasic      = addPayslipRow(grid, "Basic Salary");
        lblPayAllowances = addPayslipRow(grid, "Total Allowances");
        lblPayGross      = addPayslipRow(grid, "Gross Salary");
        lblPaySSS        = addPayslipRow(grid, "SSS Deduction");
        lblPayPhilHealth = addPayslipRow(grid, "PhilHealth Deduction");
        lblPayPagibig    = addPayslipRow(grid, "Pag-IBIG Deduction");
        lblPayTax        = addPayslipRow(grid, "Withholding Tax");
        lblPayTotalDed   = addPayslipRow(grid, "Total Deductions");
        lblPayNet        = addPayslipRow(grid, "Net Salary");

        JButton btnCalculate = new JButton("Calculate Payslip");
        btnCalculate.addActionListener(e -> calculatePayslip());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.add(btnCalculate);

        panel.add(grid);
        panel.add(btnRow);

        return new JScrollPane(panel);
    }

    /**
     * Adds a bold label and a mutable value label to the given grid panel.
     *
     * @param grid  the 2-column grid to add into
     * @param name  the field name displayed on the left
     * @return the mutable value {@link JLabel} assigned to an instance field
     */
    private JLabel addPayslipRow(JPanel grid, String name) {
        JLabel fieldLabel = new JLabel(name + ":");
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel valueLabel = new JLabel("\u2014");   // em dash placeholder
        grid.add(fieldLabel);
        grid.add(valueLabel);
        return valueLabel;
    }

    /**
     * Fires a background worker to load payroll data from CSV and populate the
     * payslip labels. Keeps the EDT free while CSV reads are in progress.
     *
     * <p><strong>OOP Pillar — Abstraction:</strong> This method delegates all
     * computation to {@link PayrollCalculatorService}. The dashboard has no
     * knowledge of deduction formulas or CSV layouts.</p>
     */
    private void calculatePayslip() {
        new javax.swing.SwingWorker<SalaryDetails, Void>() {
            @Override
            protected SalaryDetails doInBackground() throws Exception {
                return new PayrollCalculatorService().getSalaryDetails(employee.getEmployeeID());
            }
            @Override
            protected void done() {
                try {
                    SalaryDetails d = get();
                    if (d == null) {
                        JOptionPane.showMessageDialog(EmployeeDashboard.this,
                                "Payroll data not found for employee #" + employee.getEmployeeID(),
                                "Not Found", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    lblPayBasic.setText(String.format("%.2f", d.grossSalary() - d.totalAllowances()));
                    lblPayAllowances.setText(String.format("%.2f", d.totalAllowances()));
                    lblPayGross.setText(String.format("%.2f", d.grossSalary()));
                    lblPaySSS.setText(String.format("%.2f", d.sssDeduction()));
                    lblPayPhilHealth.setText(String.format("%.2f", d.philHealthDeduction()));
                    lblPayPagibig.setText(String.format("%.2f", d.pagibigDeduction()));
                    lblPayTax.setText(String.format("%.2f", d.withholdingTax()));
                    lblPayTotalDed.setText(String.format("%.2f", d.totalDeductions()));
                    lblPayNet.setText(String.format("%.2f", d.netSalary()));

                    // Wire the Payroll/Payslip domain pipeline so employee.getPayslips()
                    // is populated during the session. The SalaryDetails labels above are
                    // the authoritative display source; this block exercises the domain model.
                    try {
                        LocalDate now         = LocalDate.now();
                        LocalDate periodStart = now.withDayOfMonth(1);
                        LocalDate periodEnd   = now.withDayOfMonth(now.lengthOfMonth());
                        Payroll payroll = new Payroll(
                                employee.getEmployeeID(), employee, periodStart, periodEnd);
                        payroll.calculateNetSalary();
                        Payslip payslip = payroll.generatePayslip();
                        employee.addPayslip(payslip);
                    } catch (NullPointerException npe) {
                        // Allowance data missing for this employee — payslip domain object
                        // not added, but the label display above is unaffected.
                        System.err.println("EmployeeDashboard.calculatePayslip: "
                                + "could not create Payslip domain object — " + npe.getMessage());
                    }
                } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                    JOptionPane.showMessageDialog(EmployeeDashboard.this,
                            "Failed to calculate payslip: " + ex.getMessage(),
                            "Payroll Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // =========================================================================
    //  Tab 3: Time Tracking
    // =========================================================================

    private JPanel buildTimeTrackingTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        timeLogModel = new DefaultTableModel(
                new String[]{"Employee #", "Date", "Time In", "Time Out", "Hours Worked"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        timeLogTable = new JTable(timeLogModel);
        timeLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons
        JButton btnClockIn  = new JButton("Clock In");
        JButton btnClockOut = new JButton("Clock Out");
        JButton btnRefresh  = new JButton("Refresh Logs");

        btnClockIn.addActionListener(e -> handleClockIn());
        btnClockOut.addActionListener(e -> handleClockOut());
        btnRefresh.addActionListener(e -> loadTimeLogs());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.add(btnClockIn);
        buttonBar.add(btnClockOut);
        buttonBar.add(btnRefresh);

        panel.add(buttonBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(timeLogTable), BorderLayout.CENTER);

        loadTimeLogs();
        return panel;
    }

    private void handleClockIn() {
        try {
            timeTrackingService.clockIn(employee.getEmployeeID());
            JOptionPane.showMessageDialog(this, "Clocked in successfully.", "Clock In", JOptionPane.INFORMATION_MESSAGE);
            loadTimeLogs();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Clock in failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClockOut() {
        try {
            timeTrackingService.clockOut(employee.getEmployeeID());
            JOptionPane.showMessageDialog(this, "Clocked out successfully.", "Clock Out", JOptionPane.INFORMATION_MESSAGE);
            loadTimeLogs();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Clock out failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads time-log records off the EDT via a SwingWorker, then populates the
     * table model on the EDT inside {@code done()}.
     *
     * <p><strong>OOP Pillar — Abstraction:</strong> The method delegates file I/O
     * entirely to {@link TimeTrackingService}; the dashboard has no knowledge of the
     * underlying CSV format.</p>
     */
    private void loadTimeLogs() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                return timeTrackingService.getTimeLogs(employee.getEmployeeID());
            }

            @Override
            protected void done() {
                try {
                    List<String[]> logs = get();
                    timeLogModel.setRowCount(0);
                    for (String[] row : logs) {
                        timeLogModel.addRow(row);
                    }
                } catch (Exception ex) {
                    System.err.println("EmployeeDashboard.loadTimeLogs: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // =========================================================================
    //  Tab 4: Leave
    // =========================================================================

    private JPanel buildLeaveTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Balance summary panel
        JPanel balancePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        balancePanel.setBorder(BorderFactory.createTitledBorder("Leave Balances"));
        lblSickBalance     = new JLabel("Sick Leave: --", SwingConstants.CENTER);
        lblVacationBalance = new JLabel("Vacation Leave: --", SwingConstants.CENTER);
        lblBirthdayBalance = new JLabel("Birthday Leave: --", SwingConstants.CENTER);
        balancePanel.add(lblSickBalance);
        balancePanel.add(lblVacationBalance);
        balancePanel.add(lblBirthdayBalance);

        // Request form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 4));
        formPanel.setBorder(BorderFactory.createTitledBorder("Submit Leave Request"));

        cmbLeaveType     = new JComboBox<>(LEAVE_TYPES);
        txtLeaveStartDate = new JTextField(10);
        txtLeaveEndDate   = new JTextField(10);
        txtLeaveReason    = new JTextField(30);

        formPanel.add(new JLabel("Leave Type:"));
        formPanel.add(cmbLeaveType);
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        formPanel.add(txtLeaveStartDate);
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"));
        formPanel.add(txtLeaveEndDate);
        formPanel.add(new JLabel("Reason:"));
        formPanel.add(txtLeaveReason);

        JButton btnSubmit = new JButton("Submit Request");
        btnSubmit.addActionListener(e -> handleLeaveSubmit());

        JPanel submitRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitRow.add(btnSubmit);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(balancePanel);
        topPanel.add(formPanel);
        topPanel.add(submitRow);

        // History table
        leaveHistoryModel = new DefaultTableModel(
                new String[]{"Leave ID", "Type", "Start", "End", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        leaveHistoryTable = new JTable(leaveHistoryModel);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadLeaveData());

        JPanel histHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        histHeader.add(new JLabel("Leave History"));
        histHeader.add(btnRefresh);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(histHeader, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(leaveHistoryTable), BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);

        loadLeaveData();
        return panel;
    }

    /**
     * Loads leave balances and leave-request history off the EDT via a SwingWorker.
     * Both CSV reads are performed in {@code doInBackground()}; all label and table
     * updates are performed in {@code done()} on the EDT.
     */
    private void loadLeaveData() {
        new SwingWorker<LeaveData, Void>() {
            @Override
            protected LeaveData doInBackground() throws Exception {
                String empId = employee.getEmployeeID();
                int sick     = leaveService.getLeaveBalance(empId, "Sick Leave");
                int vacation = leaveService.getLeaveBalance(empId, "Vacation Leave");
                int birthday = leaveService.getLeaveBalance(empId, "Birthday Leave");
                List<LeaveRequest> requests = leaveService.getLeaveRequestsByEmployee(empId);
                return new LeaveData(sick, vacation, birthday, requests);
            }

            @Override
            protected void done() {
                try {
                    LeaveData data = get();
                    // Update balance labels on the EDT
                    lblSickBalance.setText("Sick Leave: " + data.sick() + " days");
                    lblVacationBalance.setText("Vacation Leave: " + data.vacation() + " days");
                    lblBirthdayBalance.setText("Birthday Leave: " + data.birthday() + " days");
                    // Reload history table on the EDT
                    leaveHistoryModel.setRowCount(0);
                    for (LeaveRequest req : data.requests()) {
                        leaveHistoryModel.addRow(new Object[]{
                            req.getLeaveID(),
                            req.getLeaveType(),
                            req.getStartDate() != null ? req.getStartDate().toString() : "",
                            req.getEndDate()   != null ? req.getEndDate().toString()   : "",
                            req.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("EmployeeDashboard.loadLeaveData: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleLeaveSubmit() {
        String leaveType = (String) cmbLeaveType.getSelectedItem();
        String startStr  = txtLeaveStartDate.getText().trim();
        String endStr    = txtLeaveEndDate.getText().trim();
        String reason    = txtLeaveReason.getText().trim();

        if (startStr.isEmpty() || endStr.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                    "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr, DATE_FMT);
            endDate   = LocalDate.parse(endStr, DATE_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date format must be yyyy-MM-dd (e.g. 2025-06-15).",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            leaveService.submitLeaveRequest(
                    employee.getEmployeeID(), leaveType, startDate, endDate, reason);
            JOptionPane.showMessageDialog(this,
                    "Leave request submitted successfully.",
                    "Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
            txtLeaveStartDate.setText("");
            txtLeaveEndDate.setText("");
            txtLeaveReason.setText("");
            loadLeaveData();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit request: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
