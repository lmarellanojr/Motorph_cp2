package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.HR;
import com.group33.cp2.motorph.model.LeaveRequest;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.service.LeaveService;
import com.group33.cp2.motorph.util.DialogUtil;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * HR Dashboard — the primary screen for HR department employees.
 *
 * <p>Provides two tabs:</p>
 * <ol>
 *   <li><strong>Employee Management</strong> — view all employees; add, edit, or delete records.</li>
 *   <li><strong>Leave Management</strong> — view pending/all leave requests; approve or reject
 *       with a remark.</li>
 * </ol>
 *
 * <p><strong>OOP Pillar — Polymorphism:</strong> Constructor accepts the logged-in HR employee
 * as an {@link HR} object. Leave approval/rejection delegates to {@code hrUser.approveLeave()}
 * and {@code hrUser.rejectLeave()}, which dispatch polymorphically through the
 * {@link com.group33.cp2.motorph.HROperations} interface.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class HRDashboard extends JFrame {

    // Domain objects
    private final HR              hrUser;
    private final EmployeeService employeeService;
    private final LeaveService    leaveService;

    // Tab 1 — Employee Management
    private JTable employeeTable;
    private DefaultTableModel employeeTableModel;

    // Tab 2 — Leave Management
    private JTable leaveTable;
    private DefaultTableModel leaveTableModel;
    private JTextField txtRemark;

    // Column indices for leave table
    private static final int LEAVE_COL_ID       = 0;
    private static final int LEAVE_COL_EMP_NUM  = 1;
    private static final int LEAVE_COL_TYPE     = 2;
    private static final int LEAVE_COL_START    = 3;
    private static final int LEAVE_COL_END      = 4;
    private static final int LEAVE_COL_STATUS   = 5;

    // Column indices for employee table
    private static final int EMP_COL_ID         = 0;
    private static final int EMP_COL_NAME       = 1;
    private static final int EMP_COL_POSITION   = 2;
    private static final int EMP_COL_STATUS     = 3;

    /**
     * Constructs the HRDashboard for the given HR user.
     *
     * @param hrUser          the logged-in HR employee; must not be null
     * @param employeeService the employee data service; must not be null
     */
    public HRDashboard(HR hrUser, EmployeeService employeeService) {
        this.hrUser          = hrUser;
        this.employeeService = employeeService;
        this.leaveService    = new LeaveService();

        setTitle("HR Dashboard — " + hrUser.getFullName());
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (DialogUtil.confirmExit(HRDashboard.this)) {
                    dispose();
                }
            }
        });

        buildUI();
        loadEmployeeTable();
        loadLeaveTable();
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel headerPanel = buildHeaderPanel();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employee Management", buildEmployeeTab());
        tabs.addTab("Leave Management", buildLeaveTab());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel title = new JLabel("HR Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel welcome = new JLabel("Welcome, " + hrUser.getFullName(), SwingConstants.RIGHT);
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
    //  Tab 1: Employee Management
    // =========================================================================

    private JPanel buildEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        employeeTableModel = new DefaultTableModel(
                new String[]{"Employee ID", "Full Name", "Position", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Button bar
        JButton btnAdd    = new JButton("Add Employee");
        JButton btnEdit   = new JButton("Edit Employee");
        JButton btnDelete = new JButton("Delete Employee");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> {
            NavigationManager.openNewEmployeeFrame(this);
        });

        btnEdit.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empId = employeeTableModel.getValueAt(row, EMP_COL_ID).toString();
            UpdateEmployeeFrame updateFrame = new UpdateEmployeeFrame(empId);
            updateFrame.setLocationRelativeTo(this);
            updateFrame.setVisible(true);
        });

        btnDelete.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empId = employeeTableModel.getValueAt(row, EMP_COL_ID).toString();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete employee " + empId + "? This cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                hrUser.deleteEmployee(empId);
                loadEmployeeTable();
            }
        });

        btnRefresh.addActionListener(e -> {
            employeeService.reloadEmployees();
            loadEmployeeTable();
        });

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonBar.add(btnAdd);
        buttonBar.add(btnEdit);
        buttonBar.add(btnDelete);
        buttonBar.add(btnRefresh);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    //  Tab 2: Leave Management
    // =========================================================================

    private JPanel buildLeaveTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        leaveTableModel = new DefaultTableModel(
                new String[]{"Leave ID", "Employee #", "Type", "Start Date", "End Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        leaveTable = new JTable(leaveTableModel);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(leaveTable);

        // Remark field
        JPanel remarkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        remarkPanel.add(new JLabel("Remark:"));
        txtRemark = new JTextField(30);
        remarkPanel.add(txtRemark);

        // Action buttons
        JButton btnApprove = new JButton("Approve");
        JButton btnReject  = new JButton("Reject");
        JButton btnRefresh = new JButton("Refresh");

        btnApprove.addActionListener(e -> handleLeaveAction(true));
        btnReject.addActionListener(e  -> handleLeaveAction(false));
        btnRefresh.addActionListener(e -> loadLeaveTable());

        remarkPanel.add(btnApprove);
        remarkPanel.add(btnReject);
        remarkPanel.add(btnRefresh);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(remarkPanel, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    //  Data loaders
    // =========================================================================

    /** Reloads the employee table from EmployeeService. */
    private void loadEmployeeTable() {
        employeeTableModel.setRowCount(0);
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            employeeTableModel.addRow(new Object[]{
                emp.getEmployeeID(),
                emp.getFullName(),
                emp.getPosition(),
                emp.getStatus()
            });
        }
    }

    /**
     * Reloads the leave requests table from LeaveRequests.csv off the EDT via a
     * SwingWorker. The CSV read runs in {@code doInBackground()}; the table model
     * update runs in {@code done()} on the EDT.
     */
    private void loadLeaveTable() {
        new SwingWorker<List<LeaveRequest>, Void>() {
            @Override
            protected List<LeaveRequest> doInBackground() throws Exception {
                return leaveService.getAllLeaveRequests();
            }

            @Override
            protected void done() {
                try {
                    List<LeaveRequest> requests = get();
                    leaveTableModel.setRowCount(0);
                    for (LeaveRequest req : requests) {
                        leaveTableModel.addRow(new Object[]{
                            req.getLeaveID(),
                            req.getEmployeeID(),
                            req.getLeaveType(),
                            req.getStartDate() != null ? req.getStartDate().toString() : "",
                            req.getEndDate()   != null ? req.getEndDate().toString()   : "",
                            req.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HRDashboard.this,
                            "Failed to load leave requests: " + ex.getMessage(),
                            "Load Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // =========================================================================
    //  Action helpers
    // =========================================================================

    /**
     * Approves or rejects the selected leave request.
     *
     * @param approve {@code true} to approve; {@code false} to reject
     */
    private void handleLeaveAction(boolean approve) {
        int row = leaveTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String leaveId = leaveTableModel.getValueAt(row, LEAVE_COL_ID).toString();
        String remark  = txtRemark.getText().trim();

        if (!approve && remark.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a remark before rejecting.",
                    "Remark Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success;
        if (approve) {
            success = hrUser.approveLeave(leaveId, remark);
        } else {
            success = hrUser.rejectLeave(leaveId, remark);
        }

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Leave request " + leaveId + " has been " + (approve ? "approved." : "rejected."),
                    approve ? "Approved" : "Rejected",
                    JOptionPane.INFORMATION_MESSAGE);
            txtRemark.setText("");
            loadLeaveTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Action failed. The request may not be in Pending status or was not found.",
                    "Action Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
