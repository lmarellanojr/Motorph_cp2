package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.Finance;
import com.group33.cp2.motorph.model.SalaryDetails;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.PayrollCalculator;
import com.group33.cp2.motorph.service.PayrollCalculatorService;
import com.group33.cp2.motorph.util.DialogUtil;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Finance Dashboard — the primary screen for Finance department employees.
 *
 * <p>Provides two tabs:</p>
 * <ol>
 *   <li><strong>Payroll Overview</strong> — summarised payroll table for all employees
 *       (basic salary, gross, deductions, net).</li>
 *   <li><strong>Detailed Report</strong> — expanded breakdown with individual deduction
 *       columns (SSS, PhilHealth, Pag-IBIG, Withholding Tax) and a "Generate Report"
 *       button that renders a formatted text summary in a non-editable text area.</li>
 * </ol>
 *
 * <p><strong>OOP Pillar — Polymorphism:</strong> All payroll figures are obtained by
 * calling {@code employee.calculateGrossSalary()}, {@code calculateDeductions()}, and
 * {@code calculateNetSalary()} through the abstract {@link Employee} reference.
 * At runtime, the JVM dispatches to whichever concrete implementation is stored
 * (e.g., {@link com.group33.cp2.motorph.model.RegularEmployee} includes withholding tax;
 * {@link com.group33.cp2.motorph.model.ProbationaryEmployee} does not).</p>
 *
 * @author Group 33
 * @version 2.0
 */
public class FinanceDashboard extends JFrame {

    private final Finance         financeUser;
    private final EmployeeService employeeService;

    // Tab 1 — Payroll Overview
    private JTable payrollTable;
    private DefaultTableModel payrollModel;

    // Tab 2 — Detailed Report
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JTextArea reportTextArea;

    // Tab 2 — per-employee payslip detail (populated by row-selection SwingWorker)
    private JLabel lblDetailEmpName;
    private JLabel lblDetailBasic;
    private JLabel lblDetailGross;
    private JLabel lblDetailSSS;
    private JLabel lblDetailPhilHealth;
    private JLabel lblDetailPagibig;
    private JLabel lblDetailTax;
    private JLabel lblDetailTotalDed;
    private JLabel lblDetailNet;

    /**
     * Constructs the FinanceDashboard.
     *
     * @param financeUser     the logged-in Finance employee; must not be null
     * @param employeeService the employee data service; must not be null
     */
    public FinanceDashboard(Finance financeUser, EmployeeService employeeService) {
        this.financeUser     = financeUser;
        this.employeeService = employeeService;

        setTitle("Finance Dashboard — " + financeUser.getFullName());
        setSize(1000, 650);
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
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel header = buildHeaderPanel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Payroll Overview", buildOverviewTab());
        tabs.addTab("Detailed Report",  buildDetailedReportTab());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(tabs,   BorderLayout.CENTER);
        getContentPane().add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel title = new JLabel("Finance Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel welcome = new JLabel("Welcome, " + financeUser.getFullName(), SwingConstants.RIGHT);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(title, BorderLayout.WEST);
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
        panel.add(buttonBar, BorderLayout.SOUTH);
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

        // Detailed breakdown table
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

        // Text summary area (shown below the table after clicking Generate)
        reportTextArea = new JTextArea(8, 80);
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setText("Click 'Generate Report' to produce a payroll summary.");
        JScrollPane textScroll = new JScrollPane(reportTextArea);
        textScroll.setBorder(BorderFactory.createTitledBorder("Payroll Summary Report"));

        // Button bar
        JButton btnRefresh  = new JButton("Refresh Table");
        JButton btnGenerate = new JButton("Generate Report");
        btnRefresh.addActionListener(e  -> loadDetailTable());
        btnGenerate.addActionListener(e -> generateDetailedReport());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.add(btnRefresh);
        buttonBar.add(btnGenerate);

        // Layout: table + button bar in the top area
        JPanel topSection = new JPanel(new BorderLayout(0, 5));
        topSection.add(tableScroll, BorderLayout.CENTER);
        topSection.add(buttonBar, BorderLayout.SOUTH);

        // Per-employee payslip detail panel (SOUTH of the split pane)
        lblDetailEmpName  = new JLabel("—", SwingConstants.CENTER);
        lblDetailEmpName.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblDetailBasic    = new JLabel("—");
        lblDetailGross    = new JLabel("—");
        lblDetailSSS      = new JLabel("—");
        lblDetailPhilHealth = new JLabel("—");
        lblDetailPagibig  = new JLabel("—");
        lblDetailTax      = new JLabel("—");
        lblDetailTotalDed = new JLabel("—");
        lblDetailNet      = new JLabel("—");

        JPanel detailGrid = new JPanel(new GridLayout(0, 4, 15, 6));
        addPayslipDetailRow(detailGrid, "Basic Salary:",   lblDetailBasic,    "Gross Salary:",     lblDetailGross);
        addPayslipDetailRow(detailGrid, "SSS:",            lblDetailSSS,      "PhilHealth:",       lblDetailPhilHealth);
        addPayslipDetailRow(detailGrid, "Pag-IBIG:",       lblDetailPagibig,  "Withholding Tax:",  lblDetailTax);
        addPayslipDetailRow(detailGrid, "Total Deductions:", lblDetailTotalDed, "Net Salary:",     lblDetailNet);

        JPanel detailPanel = new JPanel(new BorderLayout(0, 5));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Selected Employee Payslip"));
        detailPanel.add(lblDetailEmpName, BorderLayout.NORTH);
        detailPanel.add(detailGrid, BorderLayout.CENTER);

        // Wire row-selection listener BEFORE assembling the split pane
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = detailTable.getSelectedRow();
                if (row >= 0) {
                    String empId = (String) detailModel.getValueAt(row, 0);
                    loadEmployeePayslipDetail(empId);
                }
            }
        });

        // JSplitPane: Generate Report text area on top, payslip detail on bottom
        JSplitPane southSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textScroll, detailPanel);
        southSplit.setDividerLocation(160);
        southSplit.setResizeWeight(0.5);

        panel.add(topSection,  BorderLayout.CENTER);
        panel.add(southSplit,  BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Adds a pair of (field-name label, value label) to a 4-column grid row.
     * Two pairs per row: left field + left value + right field + right value.
     */
    private void addPayslipDetailRow(JPanel grid,
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

    /**
     * Fires a SwingWorker to load the payslip detail for the given employee ID
     * and update the detail labels on the EDT when done.
     *
     * <p><strong>OOP Pillar — Abstraction:</strong> this method has no knowledge of
     * deduction formulas or CSV file paths; it delegates entirely to
     * {@link PayrollCalculatorService}.</p>
     *
     * @param empId the employee ID whose payslip detail to display
     */
    private void loadEmployeePayslipDetail(String empId) {
        new javax.swing.SwingWorker<SalaryDetails, Void>() {
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
                } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                    clearPayslipDetail();
                }
            }
        }.execute();
    }

    /** Resets all payslip detail labels to the placeholder dash. */
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
            double basic       = emp.getBasicSalary();
            double allowances  = emp.getAllowance();
            double sss         = PayrollCalculator.computeSSSDeduction(basic);
            double philHealth  = PayrollCalculator.computePhilhealthDeduction(basic);
            double pagIbig     = PayrollCalculator.computePagibigDeduction(basic);
            double tax         = PayrollCalculator.computeWithholdingTax(basic);
            double totalDeduct = emp.calculateDeductions();
            double net         = emp.calculateNetSalary();

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

    /**
     * Generates a formatted payroll summary and displays it in the report text area.
     * Also shows the aggregate totals (employees count, total gross, deductions, net).
     */
    private void generateDetailedReport() {
        List<Employee> employees = employeeService.getAllEmployees();

        double totalBasic    = 0;
        double totalAllow    = 0;
        double totalSss      = 0;
        double totalPh       = 0;
        double totalPib      = 0;
        double totalTax      = 0;
        double totalDeduct   = 0;
        double totalNet      = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("MotorPH Payroll Detailed Report\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append(String.format("%-10s %-22s %10s %10s %8s %10s %9s %12s %12s %12s%n",
                "Emp ID", "Full Name", "Basic", "Allowance",
                "SSS", "PhilHealth", "PagIbig", "Tax", "Deductions", "Net Pay"));
        sb.append("-".repeat(120)).append("\n");

        for (Employee emp : employees) {
            double basic      = emp.getBasicSalary();
            double allowances = emp.getAllowance();
            double sss        = PayrollCalculator.computeSSSDeduction(basic);
            double ph         = PayrollCalculator.computePhilhealthDeduction(basic);
            double pib        = PayrollCalculator.computePagibigDeduction(basic);
            double tax        = PayrollCalculator.computeWithholdingTax(basic);
            double deduct     = emp.calculateDeductions();
            double net        = emp.calculateNetSalary();

            totalBasic  += basic;
            totalAllow  += allowances;
            totalSss    += sss;
            totalPh     += ph;
            totalPib    += pib;
            totalTax    += tax;
            totalDeduct += deduct;
            totalNet    += net;

            String name = emp.getFullName();
            if (name.length() > 22) name = name.substring(0, 19) + "...";

            sb.append(String.format("%-10s %-22s %10.2f %10.2f %8.2f %10.2f %9.2f %12.2f %12.2f %12.2f%n",
                    emp.getEmployeeID(), name,
                    basic, allowances, sss, ph, pib, tax, deduct, net));
        }

        sb.append("=".repeat(120)).append("\n");
        sb.append(String.format("%-10s %-22s %10.2f %10.2f %8.2f %10.2f %9.2f %12.2f %12.2f %12.2f%n",
                "", "TOTALS",
                totalBasic, totalAllow, totalSss, totalPh, totalPib, totalTax, totalDeduct, totalNet));
        sb.append(String.format("%nTotal Employees: %d%n", employees.size()));

        reportTextArea.setText(sb.toString());
        reportTextArea.setCaretPosition(0);
    }
}
