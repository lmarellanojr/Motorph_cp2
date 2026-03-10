package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Allowance;
import com.group33.cp2.motorph.model.CompensationDetails;
import com.group33.cp2.motorph.model.Deductions;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.Finance;
import com.group33.cp2.motorph.model.GovernmentDetails;
import com.group33.cp2.motorph.model.Payroll;
import com.group33.cp2.motorph.model.PayrollLog;
import com.group33.cp2.motorph.model.SalaryDetails;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.PayrollCalculator;
import com.group33.cp2.motorph.service.PayrollCalculatorService;
import com.group33.cp2.motorph.service.PayrollLogService;
import com.group33.cp2.motorph.util.DialogUtil;

import java.time.YearMonth;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// Finance Dashboard — payroll overview, detailed report, and payroll-by-period tabs.
public class FinanceDashboard extends JFrame {

    private final Finance              financeUser;
    private final EmployeeService      employeeService;
    private final PayrollLogService    payrollLogService;

    // Tab 1 — Payroll Overview
    private JTable            payrollTable;
    private DefaultTableModel payrollModel;

    // Tab 2 — Detailed Report
    private JTable            detailTable;
    private DefaultTableModel detailModel;
    private JTextArea         reportTextArea;

    // Tab 2 — per-employee payslip detail labels
    private JLabel lblDetailEmpName;
    private JLabel lblDetailBasic;
    private JLabel lblDetailGross;
    private JLabel lblDetailSSS;
    private JLabel lblDetailPhilHealth;
    private JLabel lblDetailPagibig;
    private JLabel lblDetailTax;
    private JLabel lblDetailTotalDed;
    private JLabel lblDetailNet;

    // Tab 3 — Payroll by Period controls
    private JComboBox<String>   cmbPeriodMonth;
    private JComboBox<String>   cmbPeriodYear;
    private JTable              periodEmpTable;
    private DefaultTableModel   periodEmpModel;
    private TableRowSorter<DefaultTableModel> periodSorter;
    private JTextField          txtPeriodSearch;

    // Tab 3 — right-panel display labels
    private JLabel lblPeriodEmpNum;
    private JLabel lblPeriodName;
    private JLabel lblPeriodSssNum;
    private JLabel lblPeriodPhilHealthNum;
    private JLabel lblPeriodTin;
    private JLabel lblPeriodPagibigNum;
    private JLabel lblPeriodRice;
    private JLabel lblPeriodPhone;
    private JLabel lblPeriodClothing;
    private JLabel lblPeriodTotalAllow;
    private JLabel lblPeriodSss;
    private JLabel lblPeriodPhilHealth;
    private JLabel lblPeriodPagibig;
    private JLabel lblPeriodTax;
    private JLabel lblPeriodTotalDed;
    private JLabel lblPeriodGross;
    private JLabel lblPeriodNet;
    private JLabel lblPeriodStatus;

    // Tab 3 — payroll log table (bottom panel)
    private DefaultTableModel periodLogModel;
    private JTable            periodLogTable;
    private JPanel            periodLogPanel;

    // Tab 3 — active SwingWorker; cancelled before a new one starts to prevent race conditions
    private SwingWorker<?, ?> currentPeriodWorker;

    // Month name labels for the combo box — index 0 is the placeholder
    private static final String[] MONTH_NAMES = {
        "Select Month",
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public FinanceDashboard(Finance financeUser, EmployeeService employeeService) {
        this.financeUser      = financeUser;
        this.employeeService  = employeeService;
        this.payrollLogService = new PayrollLogService();

        setTitle("Finance Dashboard — " + financeUser.getFullName());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (DialogUtil.confirmExit(FinanceDashboard.this)) {
                    dispose();
                }
            }
        });

        buildUI();
        loadPayrollTable();
        loadDetailTable();
        loadPeriodEmployeeTable();
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel header = buildHeaderPanel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Payroll Overview",    buildOverviewTab());
        tabs.addTab("Detailed Report",     buildDetailedReportTab());
        tabs.addTab("Payroll by Period",   buildPayrollByPeriodTab());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header,              BorderLayout.NORTH);
        getContentPane().add(tabs,                BorderLayout.CENTER);
        getContentPane().add(buildFooterPanel(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel title = new JLabel("Finance Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel welcome = new JLabel("Welcome, " + financeUser.getFullName(), SwingConstants.RIGHT);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(title,   BorderLayout.WEST);
        panel.add(welcome, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            if (DialogUtil.confirmLogout(this)) {
                NavigationManager.openLoginFrame(this);
            }
        });
        panel.add(btnLogout);
        return panel;
    }

    // =========================================================================
    //  Tab 1: Payroll Overview
    // =========================================================================

    private JPanel buildOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        payrollModel = new DefaultTableModel(
                new String[]{
                    "Employee ID", "Full Name", "Position",
                    "Basic Salary", "Gross Salary", "Total Deductions", "Net Salary"
                }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        payrollTable = new JTable(payrollModel);
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadPayrollTable());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.add(btnRefresh);

        panel.add(new JScrollPane(payrollTable), BorderLayout.CENTER);
        panel.add(buttonBar,                     BorderLayout.SOUTH);
        return panel;
    }

    private void loadPayrollTable() {
        payrollModel.setRowCount(0);
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            payrollModel.addRow(new Object[]{
                emp.getEmployeeID(),
                emp.getFullName(),
                emp.getPosition(),
                String.format("%.2f", emp.getBasicSalary()),
                String.format("%.2f", emp.calculateGrossSalary()),
                String.format("%.2f", emp.calculateDeductions()),
                String.format("%.2f", emp.calculateNetSalary())
            });
        }
    }

    // =========================================================================
    //  Tab 2: Detailed Report
    // =========================================================================

    private JPanel buildDetailedReportTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailModel = new DefaultTableModel(
                new String[]{
                    "Emp ID", "Full Name",
                    "Basic Salary", "Allowances",
                    "SSS", "PhilHealth", "Pag-IBIG", "Withholding Tax",
                    "Total Deductions", "Net Salary"
                }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane tableScroll = new JScrollPane(detailTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        reportTextArea = new JTextArea(8, 80);
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setText("Click 'Generate Report' to produce a payroll summary.");
        JScrollPane textScroll = new JScrollPane(reportTextArea);
        textScroll.setBorder(BorderFactory.createTitledBorder("Payroll Summary Report"));

        JButton btnRefresh  = new JButton("Refresh Table");
        JButton btnGenerate = new JButton("Generate Report");
        btnRefresh.addActionListener(e  -> loadDetailTable());
        btnGenerate.addActionListener(e -> generateDetailedReport());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.add(btnRefresh);
        buttonBar.add(btnGenerate);

        JPanel topSection = new JPanel(new BorderLayout(0, 5));
        topSection.add(tableScroll, BorderLayout.CENTER);
        topSection.add(buttonBar,   BorderLayout.SOUTH);

        lblDetailEmpName  = new JLabel("—", SwingConstants.CENTER);
        lblDetailEmpName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailBasic      = new JLabel("—");
        lblDetailGross      = new JLabel("—");
        lblDetailSSS        = new JLabel("—");
        lblDetailPhilHealth = new JLabel("—");
        lblDetailPagibig    = new JLabel("—");
        lblDetailTax        = new JLabel("—");
        lblDetailTotalDed   = new JLabel("—");
        lblDetailNet        = new JLabel("—");

        JPanel detailGrid = new JPanel(new GridLayout(0, 4, 15, 6));
        addLabelValuePair(detailGrid, "Basic Salary:",    lblDetailBasic,    "Gross Salary:",    lblDetailGross);
        addLabelValuePair(detailGrid, "SSS:",             lblDetailSSS,      "PhilHealth:",      lblDetailPhilHealth);
        addLabelValuePair(detailGrid, "Pag-IBIG:",        lblDetailPagibig,  "Withholding Tax:", lblDetailTax);
        addLabelValuePair(detailGrid, "Total Deductions:", lblDetailTotalDed, "Net Salary:",     lblDetailNet);

        JPanel detailPanel = new JPanel(new BorderLayout(0, 5));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Selected Employee Payslip"));
        detailPanel.add(lblDetailEmpName, BorderLayout.NORTH);
        detailPanel.add(detailGrid,       BorderLayout.CENTER);

        // Wire row-selection — cancels any in-flight worker before starting a new one
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = detailTable.getSelectedRow();
                if (row >= 0) {
                    String empId = (String) detailModel.getValueAt(row, 0);
                    loadEmployeePayslipDetail(empId);
                }
            }
        });

        JSplitPane southSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textScroll, detailPanel);
        southSplit.setDividerLocation(160);
        southSplit.setResizeWeight(0.5);

        panel.add(topSection, BorderLayout.CENTER);
        panel.add(southSplit, BorderLayout.SOUTH);
        return panel;
    }

    // Adds a bold-name / value pair (×2) to a 4-column grid panel.
    private void addLabelValuePair(JPanel grid,
                                   String leftName,  JLabel leftValue,
                                   String rightName, JLabel rightValue) {
        JLabel lName = new JLabel(leftName);
        lName.setFont(lName.getFont().deriveFont(Font.BOLD));
        JLabel rName = new JLabel(rightName);
        rName.setFont(rName.getFont().deriveFont(Font.BOLD));
        grid.add(lName);
        grid.add(leftValue);
        grid.add(rName);
        grid.add(rightValue);
    }

    // Loads payslip detail for one employee in background; updates Tab 2 labels in done().
    private void loadEmployeePayslipDetail(String empId) {
        new SwingWorker<SalaryDetails, Void>() {
            @Override
            protected SalaryDetails doInBackground() throws Exception {
                return new PayrollCalculatorService().getSalaryDetails(empId);
            }

            @Override
            protected void done() {
                try {
                    SalaryDetails d = get();
                    if (d == null) {
                        clearPayslipDetail();
                        return;
                    }
                    double basicSalary = d.grossSalary() - d.totalAllowances();
                    lblDetailBasic.setText(String.format("%.2f", basicSalary));
                    lblDetailGross.setText(String.format("%.2f", d.grossSalary()));
                    lblDetailSSS.setText(String.format("%.2f", d.sssDeduction()));
                    lblDetailPhilHealth.setText(String.format("%.2f", d.philHealthDeduction()));
                    lblDetailPagibig.setText(String.format("%.2f", d.pagibigDeduction()));
                    lblDetailTax.setText(String.format("%.2f", d.withholdingTax()));
                    lblDetailTotalDed.setText(String.format("%.2f", d.totalDeductions()));
                    lblDetailNet.setText(String.format("%.2f", d.netSalary()));
                    lblDetailEmpName.setText("Employee #" + empId);
                } catch (InterruptedException | ExecutionException ex) {
                    clearPayslipDetail();
                }
            }
        }.execute();
    }

    private void clearPayslipDetail() {
        lblDetailEmpName.setText("—");
        lblDetailBasic.setText("—");
        lblDetailGross.setText("—");
        lblDetailSSS.setText("—");
        lblDetailPhilHealth.setText("—");
        lblDetailPagibig.setText("—");
        lblDetailTax.setText("—");
        lblDetailTotalDed.setText("—");
        lblDetailNet.setText("—");
    }

    private void loadDetailTable() {
        detailModel.setRowCount(0);
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            double basic      = emp.getBasicSalary();
            double allowances = emp.getAllowance();
            double sss        = PayrollCalculator.computeSSSDeduction(basic);
            double philHealth = PayrollCalculator.computePhilhealthDeduction(basic);
            double pagIbig    = PayrollCalculator.computePagibigDeduction(basic);
            double tax        = PayrollCalculator.computeWithholdingTax(basic);
            double totalDeduct = emp.calculateDeductions();
            double net        = emp.calculateNetSalary();

            detailModel.addRow(new Object[]{
                emp.getEmployeeID(),
                emp.getFullName(),
                String.format("%.2f", basic),
                String.format("%.2f", allowances),
                String.format("%.2f", sss),
                String.format("%.2f", philHealth),
                String.format("%.2f", pagIbig),
                String.format("%.2f", tax),
                String.format("%.2f", totalDeduct),
                String.format("%.2f", net)
            });
        }
    }

    // Column indices for detailModel — must match the schema defined in buildDetailedReportTab().
    private static final int DETAIL_COL_EMP_ID      = 0;
    private static final int DETAIL_COL_FULL_NAME   = 1;
    private static final int DETAIL_COL_BASIC       = 2;
    private static final int DETAIL_COL_ALLOWANCES  = 3;
    private static final int DETAIL_COL_SSS         = 4;
    private static final int DETAIL_COL_PHILHEALTH  = 5;
    private static final int DETAIL_COL_PAGIBIG     = 6;
    private static final int DETAIL_COL_TAX         = 7;
    private static final int DETAIL_COL_TOTAL_DED   = 8;
    private static final int DETAIL_COL_NET         = 9;

    // Expected column count for detailModel — used as a structural guard.
    private static final int DETAIL_EXPECTED_COLUMNS = 10;

    // Generates a formatted payroll summary from the already-populated detailModel.
    private void generateDetailedReport() {
        if (detailModel.getRowCount() == 0) {
            reportTextArea.setText("No payroll data to report. Please refresh the table first.");
            return;
        }

        if (detailModel.getColumnCount() != DETAIL_EXPECTED_COLUMNS) {
            throw new IllegalStateException(
                    "detailModel schema mismatch: expected " + DETAIL_EXPECTED_COLUMNS
                    + " columns but found " + detailModel.getColumnCount()
                    + ". Update DETAIL_COL_* constants to match the new schema.");
        }

        double totalBasic  = 0, totalAllow  = 0, totalSss  = 0;
        double totalPh     = 0, totalPib    = 0, totalTax  = 0;
        double totalDeduct = 0, totalNet    = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("MotorPH Payroll Detailed Report\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append(String.format("%-10s %-22s %10s %10s %8s %10s %9s %12s %12s %12s%n",
                "Emp ID", "Full Name", "Basic", "Allowance",
                "SSS", "PhilHealth", "PagIbig", "Tax", "Deductions", "Net Pay"));
        sb.append("-".repeat(120)).append("\n");

        int rowCount = detailModel.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            String empId  = (String) detailModel.getValueAt(row, DETAIL_COL_EMP_ID);
            String name   = (String) detailModel.getValueAt(row, DETAIL_COL_FULL_NAME);
            double basic  = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_BASIC));
            double allow  = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_ALLOWANCES));
            double sss    = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_SSS));
            double ph     = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_PHILHEALTH));
            double pib    = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_PAGIBIG));
            double tax    = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_TAX));
            double deduct = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_TOTAL_DED));
            double net    = Double.parseDouble((String) detailModel.getValueAt(row, DETAIL_COL_NET));

            totalBasic  += basic;  totalAllow  += allow;
            totalSss    += sss;    totalPh     += ph;
            totalPib    += pib;    totalTax    += tax;
            totalDeduct += deduct; totalNet    += net;

            String displayName = name.length() > 22 ? name.substring(0, 19) + "..." : name;
            sb.append(String.format("%-10s %-22s %10.2f %10.2f %8.2f %10.2f %9.2f %12.2f %12.2f %12.2f%n",
                    empId, displayName, basic, allow, sss, ph, pib, tax, deduct, net));
        }

        sb.append("=".repeat(120)).append("\n");
        sb.append(String.format("%-10s %-22s %10.2f %10.2f %8.2f %10.2f %9.2f %12.2f %12.2f %12.2f%n",
                "", "TOTALS",
                totalBasic, totalAllow, totalSss, totalPh, totalPib, totalTax, totalDeduct, totalNet));
        sb.append(String.format("%nTotal Employees: %d%n", rowCount));

        reportTextArea.setText(sb.toString());
        reportTextArea.setCaretPosition(0);
    }

    // =========================================================================
    //  Tab 3: Payroll by Period
    // =========================================================================

    private JPanel buildPayrollByPeriodTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top control bar: month/year selectors + Compute + Refresh Log buttons ---
        cmbPeriodMonth = new JComboBox<>(MONTH_NAMES);
        cmbPeriodYear  = new JComboBox<>(buildYearItems());

        JButton btnComputePeriod = new JButton("Compute Period");
        btnComputePeriod.addActionListener(e -> runBatchPayrollForPeriod());

        JButton btnRefreshLog = new JButton("Refresh Log");
        btnRefreshLog.addActionListener(e -> loadPeriodLogTable());

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controlBar.add(new JLabel("Month:"));
        controlBar.add(cmbPeriodMonth);
        controlBar.add(new JLabel("Year:"));
        controlBar.add(cmbPeriodYear);
        controlBar.add(btnComputePeriod);
        controlBar.add(btnRefreshLog);

        // Wire combo changes to refresh the log table for the newly selected period
        cmbPeriodMonth.addActionListener(e -> loadPeriodLogTable());
        cmbPeriodYear.addActionListener(e -> loadPeriodLogTable());

        // --- Left panel: searchable employee list ---
        periodEmpModel = new DefaultTableModel(new String[]{"Emp #", "Name"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        periodEmpTable = new JTable(periodEmpModel);
        periodEmpTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        periodEmpTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        periodSorter = new TableRowSorter<>(periodEmpModel);
        periodEmpTable.setRowSorter(periodSorter);

        txtPeriodSearch = new JTextField(15);
        txtPeriodSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterPeriodTable(); }
            @Override public void removeUpdate(DocumentEvent e) { filterPeriodTable(); }
            @Override public void changedUpdate(DocumentEvent e) { filterPeriodTable(); }
        });

        JPanel searchBar = new JPanel(new BorderLayout(4, 0));
        searchBar.add(new JLabel("Search: "), BorderLayout.WEST);
        searchBar.add(txtPeriodSearch, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 4));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Employees"));
        leftPanel.add(searchBar,                        BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(periodEmpTable),  BorderLayout.CENTER);

        // Wire row-selection — cancels prior worker before starting a new one
        periodEmpTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = periodEmpTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = periodEmpTable.convertRowIndexToModel(viewRow);
                    String empId = (String) periodEmpModel.getValueAt(modelRow, 0);
                    showPeriodPayslip(empId);
                }
            }
        });

        // --- Right panel: payslip detail labels ---
        JPanel rightPanel = buildPeriodDetailPanel();

        // --- Split: left list | right detail ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(230);
        splitPane.setResizeWeight(0.25);

        // --- Bottom panel: payroll log table ---
        periodLogModel = new DefaultTableModel(
                new String[]{"Emp #", "Employee Name", "Gross Salary", "Total Allowance", "Total Deductions", "Net Salary"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        periodLogTable = new JTable(periodLogModel);
        periodLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        periodLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane logScroll = new JScrollPane(periodLogTable);
        logScroll.setPreferredSize(new java.awt.Dimension(0, 150));

        periodLogPanel = new JPanel(new BorderLayout());
        periodLogPanel.setBorder(BorderFactory.createTitledBorder("Payroll Log — Select a period above"));
        periodLogPanel.add(logScroll, BorderLayout.CENTER);

        panel.add(controlBar,    BorderLayout.NORTH);
        panel.add(splitPane,     BorderLayout.CENTER);
        panel.add(periodLogPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Builds the right-panel label grid for the "Payroll by Period" tab.
    private JPanel buildPeriodDetailPanel() {
        // Initialise all display labels
        lblPeriodStatus       = new JLabel("Select a period and employee to view payslip");
        lblPeriodEmpNum       = new JLabel("—");
        lblPeriodName         = new JLabel("—");
        lblPeriodSssNum       = new JLabel("—");
        lblPeriodPhilHealthNum = new JLabel("—");
        lblPeriodTin          = new JLabel("—");
        lblPeriodPagibigNum   = new JLabel("—");
        lblPeriodRice         = new JLabel("—");
        lblPeriodPhone        = new JLabel("—");
        lblPeriodClothing     = new JLabel("—");
        lblPeriodTotalAllow   = new JLabel("—");
        lblPeriodSss          = new JLabel("—");
        lblPeriodPhilHealth   = new JLabel("—");
        lblPeriodPagibig      = new JLabel("—");
        lblPeriodTax          = new JLabel("—");
        lblPeriodTotalDed     = new JLabel("—");
        lblPeriodGross        = new JLabel("—");
        lblPeriodNet          = new JLabel("—");

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 8, 3, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Status hint row — spans all 4 columns
        gc.gridx = 0; gc.gridy = row++; gc.gridwidth = 4;
        grid.add(lblPeriodStatus, gc);
        gc.gridwidth = 1;

        // Identity section
        addPeriodRow(grid, gc, row++, "Employee #:",    lblPeriodEmpNum,       "Full Name:",         lblPeriodName);
        addPeriodRow(grid, gc, row++, "SSS #:",         lblPeriodSssNum,       "PhilHealth #:",      lblPeriodPhilHealthNum);
        addPeriodRow(grid, gc, row++, "TIN:",           lblPeriodTin,          "Pag-IBIG #:",        lblPeriodPagibigNum);

        // Separator label
        gc.gridx = 0; gc.gridy = row++; gc.gridwidth = 4;
        JLabel sepEarnings = new JLabel("— Earnings —");
        sepEarnings.setFont(sepEarnings.getFont().deriveFont(Font.BOLD));
        grid.add(sepEarnings, gc);
        gc.gridwidth = 1;

        addPeriodRow(grid, gc, row++, "Rice Allowance:",    lblPeriodRice,       "Phone Allowance:",   lblPeriodPhone);
        addPeriodRow(grid, gc, row++, "Clothing Allowance:", lblPeriodClothing,  "Total Allowances:",  lblPeriodTotalAllow);

        // Separator label
        gc.gridx = 0; gc.gridy = row++; gc.gridwidth = 4;
        JLabel sepDeductions = new JLabel("— Deductions —");
        sepDeductions.setFont(sepDeductions.getFont().deriveFont(Font.BOLD));
        grid.add(sepDeductions, gc);
        gc.gridwidth = 1;

        addPeriodRow(grid, gc, row++, "SSS:",           lblPeriodSss,          "PhilHealth:",        lblPeriodPhilHealth);
        addPeriodRow(grid, gc, row++, "Pag-IBIG:",      lblPeriodPagibig,      "Withholding Tax:",   lblPeriodTax);
        addPeriodRow(grid, gc, row++, "Total Deductions:", lblPeriodTotalDed,   "",                   new JLabel(""));

        // Separator label
        gc.gridx = 0; gc.gridy = row++; gc.gridwidth = 4;
        JLabel sepSummary = new JLabel("— Summary —");
        sepSummary.setFont(sepSummary.getFont().deriveFont(Font.BOLD));
        grid.add(sepSummary, gc);
        gc.gridwidth = 1;

        addPeriodRow(grid, gc, row++, "Gross Salary:",  lblPeriodGross,        "Net Salary:",        lblPeriodNet);

        // Push everything to the top
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 4; gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        grid.add(new JPanel(), gc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Payslip Detail"));
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    // Adds one row (2 label+value pairs) to a GridBagLayout panel.
    private void addPeriodRow(JPanel panel, GridBagConstraints gc, int row,
                               String leftLabel, JLabel leftValue,
                               String rightLabel, JLabel rightValue) {
        gc.weightx = 0;
        gc.gridx = 0; gc.gridy = row;
        JLabel lbl1 = new JLabel(leftLabel);
        lbl1.setFont(lbl1.getFont().deriveFont(Font.BOLD));
        panel.add(lbl1, gc);

        gc.weightx = 0.4;
        gc.gridx = 1;
        panel.add(leftValue, gc);

        gc.weightx = 0;
        gc.gridx = 2;
        JLabel lbl2 = new JLabel(rightLabel);
        lbl2.setFont(lbl2.getFont().deriveFont(Font.BOLD));
        panel.add(lbl2, gc);

        gc.weightx = 0.4;
        gc.gridx = 3;
        panel.add(rightValue, gc);
    }

    // Populates the left-panel employee table with all employees from EmployeeService.
    private void loadPeriodEmployeeTable() {
        periodEmpModel.setRowCount(0);
        for (Employee emp : employeeService.getAllEmployees()) {
            periodEmpModel.addRow(new Object[]{emp.getEmployeeID(), emp.getFullName()});
        }
    }

    // Applies a case-insensitive filter to both columns of the period employee table.
    private void filterPeriodTable() {
        String text = txtPeriodSearch.getText().trim();
        if (text.isEmpty()) {
            periodSorter.setRowFilter(null);
        } else {
            periodSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // Populates the bottom log table with stored payroll runs for the selected period.
    // Shows a placeholder row when no period is selected or no logs exist for the period.
    private void loadPeriodLogTable() {
        int monthIndex = cmbPeriodMonth.getSelectedIndex();
        int yearIndex  = cmbPeriodYear.getSelectedIndex();
        boolean periodSelected = (monthIndex > 0 && yearIndex > 0);

        periodLogModel.setRowCount(0);

        if (!periodSelected) {
            periodLogPanel.setBorder(BorderFactory.createTitledBorder("Payroll Log — Select a period above"));
            periodLogModel.addRow(new Object[]{"—", "Select a month and year above", "", "", "", ""});
            return;
        }

        int month = monthIndex;
        int year  = parsePeriodYear();
        String periodLabel = MONTH_NAMES[month] + " " + year;
        periodLogPanel.setBorder(BorderFactory.createTitledBorder("Payroll Log — " + periodLabel));

        List<PayrollLog> logs = payrollLogService.getLogsByPeriod(month, year);

        if (logs.isEmpty()) {
            periodLogModel.addRow(new Object[]{"—", "No payroll run for this period", "", "", "", ""});
            return;
        }

        double totalGross = 0, totalAllow = 0, totalDed = 0, totalNet = 0;

        for (PayrollLog log : logs) {
            Employee emp = employeeService.getEmployeeById(log.empNum());
            String name  = emp != null ? emp.getFullName() : log.empNum();
            periodLogModel.addRow(new Object[]{
                log.empNum(),
                name,
                String.format("%.2f", log.grossSalary()),
                String.format("%.2f", log.totalAllowance()),
                String.format("%.2f", log.totalDeductions()),
                String.format("%.2f", log.netSalary())
            });
            totalGross += log.grossSalary();
            totalAllow += log.totalAllowance();
            totalDed   += log.totalDeductions();
            totalNet   += log.netSalary();
        }

        // Totals row
        periodLogModel.addRow(new Object[]{
            "TOTAL",
            "",
            String.format("%.2f", totalGross),
            String.format("%.2f", totalAllow),
            String.format("%.2f", totalDed),
            String.format("%.2f", totalNet)
        });
    }

    // Loads payslip detail for the selected employee and period into the right panel.
    // If no period is selected, clears the panel and shows a hint message.
    // Cancels any in-flight worker before starting a new one.
    private void showPeriodPayslip(String empId) {
        if (currentPeriodWorker != null && !currentPeriodWorker.isDone()) {
            currentPeriodWorker.cancel(true);
        }

        int monthIndex = cmbPeriodMonth.getSelectedIndex();  // 0 = placeholder
        int yearIndex  = cmbPeriodYear.getSelectedIndex();   // 0 = placeholder
        boolean periodSelected = (monthIndex > 0 && yearIndex > 0);

        if (!periodSelected) {
            // No period chosen — show hint and clear detail labels
            clearPeriodDetail();
            lblPeriodStatus.setText("Select a month and year above");
            return;
        }

        // Capture selected values before entering background thread
        int month = monthIndex;  // MONTH_NAMES[1] = January = month 1
        int year  = parsePeriodYear();

        SwingWorker<Payroll, Void> worker = new SwingWorker<>() {
            @Override
            protected Payroll doInBackground() {
                Employee emp = employeeService.getEmployeeById(empId);
                if (emp == null) return null;
                LocalDate start = LocalDate.of(year, month, 1);
                LocalDate end   = YearMonth.of(year, month).atEndOfMonth();
                Payroll payroll = new Payroll(empId, emp, start, end);
                payroll.calculateNetSalary();
                return payroll;
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    Payroll payroll = get();
                    Employee emp = employeeService.getEmployeeById(empId);
                    GovernmentDetails gov = emp != null ? emp.getGovernmentDetails() : null;
                    if (payroll == null) {
                        clearPeriodDetail();
                        lblPeriodStatus.setText("Employee data not found");
                        return;
                    }
                    populatePeriodDetailFromPayroll(empId, payroll, gov);
                } catch (InterruptedException | ExecutionException ex) {
                    clearPeriodDetail();
                    lblPeriodStatus.setText("Error loading payslip: " + ex.getMessage());
                }
            }
        };

        currentPeriodWorker = worker;
        worker.execute();
    }

    // Populates all right-panel labels with the given salary details and government IDs.
    private void populatePeriodDetail(String empId, SalaryDetails d, GovernmentDetails gov) {
        if (d == null) {
            clearPeriodDetail();
            return;
        }
        Employee emp = employeeService.getEmployeeById(empId);
        lblPeriodEmpNum.setText(empId);
        lblPeriodName.setText(emp != null ? emp.getFullName() : "—");

        if (gov != null) {
            lblPeriodSssNum.setText(gov.getSssNumber());
            lblPeriodPhilHealthNum.setText(gov.getPhilHealthNumber());
            lblPeriodTin.setText(gov.getTinNumber());
            lblPeriodPagibigNum.setText(gov.getPagibigNumber());
        } else {
            lblPeriodSssNum.setText("—");
            lblPeriodPhilHealthNum.setText("—");
            lblPeriodTin.setText("—");
            lblPeriodPagibigNum.setText("—");
        }

        lblPeriodRice.setText(String.format("%.2f", d.riceSubsidy()));
        lblPeriodPhone.setText(String.format("%.2f", d.phoneAllowance()));
        lblPeriodClothing.setText(String.format("%.2f", d.clothingAllowance()));
        lblPeriodTotalAllow.setText(String.format("%.2f", d.totalAllowances()));
        lblPeriodSss.setText(String.format("%.2f", d.sssDeduction()));
        lblPeriodPhilHealth.setText(String.format("%.2f", d.philHealthDeduction()));
        lblPeriodPagibig.setText(String.format("%.2f", d.pagibigDeduction()));
        lblPeriodTax.setText(String.format("%.2f", d.withholdingTax()));
        lblPeriodTotalDed.setText(String.format("%.2f", d.totalDeductions()));
        lblPeriodGross.setText(String.format("%.2f", d.grossSalary()));
        lblPeriodNet.setText(String.format("%.2f", d.netSalary()));
    }

    // Populates right-panel labels from an attendance-based Payroll object.
    private void populatePeriodDetailFromPayroll(String empId, Payroll payroll, GovernmentDetails gov) {
        Employee emp = employeeService.getEmployeeById(empId);
        CompensationDetails cd  = payroll.getCompensationDetails();
        Deductions ded          = cd.getDeductions();
        Allowance allow         = cd.getAllowance();

        lblPeriodEmpNum.setText(empId);
        lblPeriodName.setText(emp != null ? emp.getFullName() : "—");

        if (gov != null) {
            lblPeriodSssNum.setText(gov.getSssNumber());
            lblPeriodPhilHealthNum.setText(gov.getPhilHealthNumber());
            lblPeriodTin.setText(gov.getTinNumber());
            lblPeriodPagibigNum.setText(gov.getPagibigNumber());
        } else {
            lblPeriodSssNum.setText("—");
            lblPeriodPhilHealthNum.setText("—");
            lblPeriodTin.setText("—");
            lblPeriodPagibigNum.setText("—");
        }

        lblPeriodRice.setText(String.format("%.2f", allow.getRiceAllowance()));
        lblPeriodPhone.setText(String.format("%.2f", allow.getPhoneAllowance()));
        lblPeriodClothing.setText(String.format("%.2f", allow.getClothingAllowance()));
        lblPeriodTotalAllow.setText(String.format("%.2f", allow.getTotal()));
        lblPeriodSss.setText(String.format("%.2f", ded.getSss()));
        lblPeriodPhilHealth.setText(String.format("%.2f", ded.getPhilHealth()));
        lblPeriodPagibig.setText(String.format("%.2f", ded.getPagIbig()));
        lblPeriodTax.setText(String.format("%.2f", ded.getTax()));
        lblPeriodTotalDed.setText(String.format("%.2f", ded.getTotal()));
        lblPeriodGross.setText(String.format("%.2f", cd.getGrossSalary()));
        lblPeriodNet.setText(String.format("%.2f", cd.getNetSalary()));

        // Update status line with attendance hours summary
        lblPeriodStatus.setText(String.format("Payroll loaded — %.2f regular hrs, %.2f OT hrs",
                payroll.getTotalRegularHours(), payroll.getTotalOvertimeHours()));
    }

    // Resets all right-panel labels to the placeholder dash.
    private void clearPeriodDetail() {
        lblPeriodStatus.setText("Select a period and employee to view payslip");
        lblPeriodEmpNum.setText("—");
        lblPeriodName.setText("—");
        lblPeriodSssNum.setText("—");
        lblPeriodPhilHealthNum.setText("—");
        lblPeriodTin.setText("—");
        lblPeriodPagibigNum.setText("—");
        lblPeriodRice.setText("—");
        lblPeriodPhone.setText("—");
        lblPeriodClothing.setText("—");
        lblPeriodTotalAllow.setText("—");
        lblPeriodSss.setText("—");
        lblPeriodPhilHealth.setText("—");
        lblPeriodPagibig.setText("—");
        lblPeriodTax.setText("—");
        lblPeriodTotalDed.setText("—");
        lblPeriodGross.setText("—");
        lblPeriodNet.setText("—");
    }

    // Validates the period selection, then computes and logs payroll for all employees.
    // Employees already logged for the period are skipped; a summary dialog is shown.
    private void runBatchPayrollForPeriod() {
        if (cmbPeriodMonth.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a month before computing the period.",
                    "No Month Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbPeriodYear.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a year before computing the period.",
                    "No Year Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int month = cmbPeriodMonth.getSelectedIndex();  // 1–12
        int year  = parsePeriodYear();

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Compute payroll for %s %d for all employees?",
                        MONTH_NAMES[month], year),
                "Confirm Payroll Run",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        List<Employee> employees = employeeService.getAllEmployees();

        // Batch run in background to keep the UI responsive
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int computed = 0, skipped = 0;
                LocalDate start = LocalDate.of(year, month, 1);
                LocalDate end   = YearMonth.of(year, month).atEndOfMonth();
                for (Employee emp : employees) {
                    String empId = emp.getEmployeeID();
                    try {
                        if (payrollLogService.isAlreadyLogged(empId, month, year)) {
                            skipped++;
                            continue;
                        }
                        Payroll payroll = new Payroll(empId, emp, start, end);
                        payroll.calculateNetSalary();
                        CompensationDetails cd = payroll.getCompensationDetails();
                        payrollLogService.savePayrollRun(empId, month, year, cd);
                        computed++;
                    } catch (Exception ex) {
                        System.err.println("Batch payroll: error for " + empId + ": " + ex.getMessage());
                        skipped++;
                    }
                }
                return new int[]{computed, skipped};
            }

            @Override
            protected void done() {
                try {
                    int[] result = get();
                    JOptionPane.showMessageDialog(FinanceDashboard.this,
                            String.format("Payroll run complete for %s %d.%n"
                                    + "Computed: %d employees%n"
                                    + "Skipped (already logged or no data): %d employees",
                                    MONTH_NAMES[month], year, result[0], result[1]),
                            "Payroll Run Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadPeriodLogTable();
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(FinanceDashboard.this,
                            "Payroll run failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // Parses the year integer from the selected year combo item (e.g. "2025" → 2025).
    private int parsePeriodYear() {
        String yearStr = (String) cmbPeriodYear.getSelectedItem();
        if (yearStr == null || yearStr.equals("Select Year")) return 0;
        try {
            return Integer.parseInt(yearStr.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Builds the year combo items dynamically: placeholder + current year + 2 prior years.
    private static String[] buildYearItems() {
        int currentYear = LocalDate.now().getYear();
        return new String[]{
            "Select Year",
            String.valueOf(currentYear - 2),
            String.valueOf(currentYear - 1),
            String.valueOf(currentYear)
        };
    }
}
