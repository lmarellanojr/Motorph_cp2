package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.IT;
import com.group33.cp2.motorph.model.PasswordResetRequest;
import com.group33.cp2.motorph.service.PasswordResetService;
import com.group33.cp2.motorph.service.ResetPasswordProcessor;
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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * IT Dashboard — the primary screen for IT department employees.
 *
 * <p>Displays pending password reset requests and allows the IT employee to
 * approve them (which generates a temporary password and updates Login.csv).</p>
 *
 * <p><strong>OOP Pillar — Abstraction:</strong> The approval action delegates to
 * {@link ResetPasswordProcessor}, which depends on {@link com.group33.cp2.motorph.service.PasswordResetCallback}
 * rather than importing this class. The callback is passed as a lambda
 * ({@code this::loadPasswordResetRequests}), demonstrating the decoupling seam.</p>
 *
 * @author Group 33
 * @version 1.0
 */
public class ITDashboard extends JFrame {

    private final IT itUser;
    private final PasswordResetService   passwordResetService;
    private final ResetPasswordProcessor processor;

    private JTable requestTable;
    private DefaultTableModel requestModel;

    private static final int COL_EMP_NUM  = 0;
    private static final int COL_EMP_NAME = 1;
    private static final int COL_DATE_REQ = 2;
    private static final int COL_STATUS   = 3;

    /**
     * Constructs the ITDashboard for the given IT employee.
     *
     * @param itUser the logged-in IT employee; must not be null
     */
    public ITDashboard(IT itUser) {
        this.itUser               = itUser;
        this.passwordResetService = new PasswordResetService();
        this.processor            = new ResetPasswordProcessor(passwordResetService);

        setTitle("IT Dashboard — " + itUser.getFullName());
        setSize(800, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (DialogUtil.confirmExit(ITDashboard.this)) {
                    dispose();
                }
            }
        });

        buildUI();
        loadPasswordResetRequests();
    }

    // =========================================================================
    //  UI construction
    // =========================================================================

    private void buildUI() {
        JPanel header = buildHeaderPanel();

        // Table
        requestModel = new DefaultTableModel(
                new String[]{"Employee #", "Employee Name", "Date Requested", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        requestTable = new JTable(requestModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons
        JButton btnReset   = new JButton("Reset Password");
        JButton btnRefresh = new JButton("Refresh");

        btnReset.addActionListener(e -> handleResetPassword());
        btnRefresh.addActionListener(e -> loadPasswordResetRequests());

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.add(btnReset);
        buttonBar.add(btnRefresh);

        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        content.add(buttonBar, BorderLayout.SOUTH);

        JPanel footer = buildFooterPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(content, BorderLayout.CENTER);
        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel title = new JLabel("IT Dashboard — Password Reset Requests", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel welcome = new JLabel("Welcome, " + itUser.getFullName(), SwingConstants.RIGHT);
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
    //  Data and actions
    // =========================================================================

    /**
     * Reloads the password reset requests table from the CSV off the EDT via a
     * SwingWorker. The CSV read runs in {@code doInBackground()}; the table model
     * update runs in {@code done()} on the EDT.
     *
     * <p>This method is passed as {@code this::loadPasswordResetRequests} to
     * {@link com.group33.cp2.motorph.service.ResetPasswordProcessor} as a
     * {@link com.group33.cp2.motorph.service.PasswordResetCallback} lambda. The
     * SwingWorker pattern is transparent to that contract because this method still
     * returns {@code void} immediately and schedules work asynchronously.</p>
     */
    public void loadPasswordResetRequests() {
        new SwingWorker<List<PasswordResetRequest>, Void>() {
            @Override
            protected List<PasswordResetRequest> doInBackground() throws Exception {
                return passwordResetService.getAllRequests();
            }

            @Override
            protected void done() {
                try {
                    List<PasswordResetRequest> requests = get();
                    requestModel.setRowCount(0);
                    for (PasswordResetRequest req : requests) {
                        requestModel.addRow(new Object[]{
                            req.getEmployeeNumber(),
                            req.getEmployeeName(),
                            req.getDateOfRequest(),
                            req.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ITDashboard.this,
                            "Failed to load password reset requests: " + ex.getMessage(),
                            "Load Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /** Handles the "Reset Password" button action on the selected row. */
    private void handleResetPassword() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = requestModel.getValueAt(row, COL_STATUS).toString();
        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This request has already been processed.",
                    "Already Processed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String empNum = requestModel.getValueAt(row, COL_EMP_NUM).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reset password for employee " + empNum + "?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Use PasswordResetCallback lambda — decouples processor from GUI
            processor.resetPassword(empNum, itUser.getFullName(), itUser.getEmployeeID(),
                    this::loadPasswordResetRequests);
        }
    }
}
