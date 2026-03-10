package com.group33.cp2.motorph.forms;

import com.group33.cp2.motorph.model.Admin;
import com.group33.cp2.motorph.model.Employee;
import com.group33.cp2.motorph.model.Finance;
import com.group33.cp2.motorph.model.HR;
import com.group33.cp2.motorph.model.IT;
import com.group33.cp2.motorph.service.AuthService;
import com.group33.cp2.motorph.service.EmployeeService;
import com.group33.cp2.motorph.util.Constants;
import com.group33.cp2.motorph.util.DialogUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

// Login screen. BCrypt auth against Login.csv; dispatches to role dashboard on success.
// If changePassword=YES, user must set a new password before proceeding.
public class LoginFrame extends javax.swing.JFrame {

    private final AuthService     authService     = new AuthService();
    private final EmployeeService employeeService = new EmployeeService();

    public LoginFrame() {
        initComponents();
        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (DialogUtil.confirmExit(LoginFrame.this)) {
                    dispose();
                }
            }
        });

        // Allow pressing Enter in either field to trigger login
        txtPassword.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "login");
        txtPassword.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        });

        txtUsername.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "login");
        txtUsername.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        });

        // Wire the "Forgot Password?" hyperlink mouse listener.
        // The label itself is declared and added to the GroupLayout inside initComponents()
        // so that the GroupLayout positions it correctly without breaking existing layout math.
        lblForgotPassword.setForeground(new Color(0, 0, 200));
        lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openForgotPassword();
            }
        });

        setLocationRelativeTo(null);
    }

    // Initializes form components. Maven-only project; hand edits here are safe.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtUsername = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        lblForgotPassword = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Username");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Password");

        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("sansserif", 0, 48)); // NOI18N
        jLabel3.setText("MotorPH\u2122");

        jLabel4.setFont(new java.awt.Font("sansserif", 0, 48)); // NOI18N
        jLabel4.setText("Payroll System");

        // "Forgot Password?" hyperlink — styled and wired in constructor after initComponents()
        lblForgotPassword.setText("<html><u>Forgot Password?</u></html>");
        lblForgotPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addGap(38, 38, 38))
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtUsername)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(lblForgotPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(370, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(70, 70, 70)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(btnLogin)
                .addGap(12, 12, 12)
                .addComponent(lblForgotPassword)
                .addContainerGap(225, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Reads inputs, authenticates via BCrypt against Login.csv, dispatches to role dashboard.
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username should not be empty",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password should not be empty",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // Look up Login.csv row: [0]=empNum, [1]=username, [2]=roleName, [3]=password, [4]=changePassword
        String[] loginRow = authService.getLoginDataByUsername(username);

        if (loginRow == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String storedHash = loginRow.length > 3 ? loginRow[3].trim() : "";
        if (!BCrypt.checkpw(password, storedHash)) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if password change is required
        String changePw = loginRow.length > 4 ? loginRow[4].trim() : "NO";
        if ("YES".equalsIgnoreCase(changePw)) {
            handleForcePasswordChange(loginRow[0].trim());
            return;
        }

        // Successful login — dispatch by role
        String empNum = loginRow[0].trim();
        String role   = loginRow.length > 2 ? loginRow[2].trim().toUpperCase() : "EMPLOYEE";

        JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username + ".");
        openDashboardForRole(role, empNum);
    }

    // Prompts the user to set a new password and writes the BCrypt hash to Login.csv.
    // Requires a successful change before proceeding to the dashboard.
    private void handleForcePasswordChange(String empNum) {
        JPasswordField newPwField     = new JPasswordField();
        JPasswordField confirmPwField = new JPasswordField();
        Object[] message = {
            "Your password must be changed before you can continue.",
            "New Password:", newPwField,
            "Confirm Password:", confirmPwField
        };

        int option = JOptionPane.showConfirmDialog(
                this, message, "Change Password Required", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            return; // User cancelled — stay on login screen
        }

        String newPw      = new String(newPwField.getPassword()).trim();
        String confirmPw  = new String(confirmPwField.getPassword()).trim();

        if (newPw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashed = BCrypt.hashpw(newPw, BCrypt.gensalt());
        try {
            boolean updated = authService.changeUserPassword(empNum, hashed);
            if (!updated) {
                JOptionPane.showMessageDialog(this, "Password change failed: employee not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Password change failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Password changed successfully. Please log in again.",
                "Password Changed", JOptionPane.INFORMATION_MESSAGE);
        // Don't dispatch yet — require the user to log in again with the new password
    }

    // Opens the appropriate dashboard for the given role and employee number.
    private void openDashboardForRole(String role, String empNum) {
        Employee emp = employeeService.getEmployeeById(empNum);

        switch (role) {
            case "HR" -> {
                if (emp instanceof HR hrEmp) {
                    dispose();
                    HRDashboard dash = new HRDashboard(hrEmp, employeeService);
                    dash.setVisible(true);
                } else {
                    // Employee record exists but type is not HR — open fallback
                    NavigationManager.openMenuFrame(this);
                }
            }
            case "IT" -> {
                if (emp instanceof IT itEmp) {
                    dispose();
                    ITDashboard dash = new ITDashboard(itEmp);
                    dash.setVisible(true);
                } else {
                    NavigationManager.openMenuFrame(this);
                }
            }
            case "FINANCE" -> {
                if (emp instanceof Finance finEmp) {
                    dispose();
                    FinanceDashboard dash = new FinanceDashboard(finEmp, employeeService);
                    dash.setVisible(true);
                } else {
                    NavigationManager.openMenuFrame(this);
                }
            }
            case "ADMIN" -> {
                if (emp instanceof Admin adminEmp) {
                    dispose();
                    AdminDashboard dash = new AdminDashboard(adminEmp, employeeService);
                    dash.setVisible(true);
                } else {
                    NavigationManager.openMenuFrame(this);
                }
            }
            default -> {
                // EMPLOYEE and any unrecognised roles
                if (emp != null) {
                    dispose();
                    EmployeeDashboard dash = new EmployeeDashboard(emp);
                    dash.setVisible(true);
                } else {
                    // emp not found in Employee.csv — fall back to legacy menu
                    NavigationManager.openMenuFrame(this);
                }
            }
        }
    }

    // Disposes the login frame and opens the ForgotPasswordForm.
    private void openForgotPassword() {
        dispose();
        new ForgotPasswordForm().setVisible(true);
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        login();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // handled via key binding
    }//GEN-LAST:event_txtUsernameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblForgotPassword;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
