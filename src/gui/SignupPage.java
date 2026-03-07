package gui;

import dao.EmployeeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Sign Up Page – Interface #5
 * Allows creation of Admin or Employee user accounts.
 */
public class SignUpPage extends JFrame {

    private JTextField     txtUsername, txtFullName, txtEmail;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cbRole;
    private JButton        btnSignUp, btnClear, btnBackToLogin;
    private JLabel         lblStatus;
    private JCheckBox      chkShowPassword;

    private final EmployeeDAO dao = new EmployeeDAO();

    public SignUpPage() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Employee Management System – Sign Up");
        setSize(440, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel ────────────────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 60, 114));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 60, 114));
        headerPanel.setBorder(new EmptyBorder(25, 20, 15, 20));

        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Fill in the details below to register", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(180, 210, 255));

        headerPanel.add(lblTitle,    BorderLayout.CENTER);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        // ── Form panel ────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(24, 36, 16, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(5, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Full Name
        formPanel.add(makeLabel("Full Name"), gbc);
        txtFullName = makeField();
        formPanel.add(txtFullName, gbc);

        // Username
        formPanel.add(makeLabel("Username"), gbc);
        txtUsername = makeField();
        formPanel.add(txtUsername, gbc);

        // Email
        formPanel.add(makeLabel("Email"), gbc);
        txtEmail = makeField();
        formPanel.add(txtEmail, gbc);

        // Role
        formPanel.add(makeLabel("Role"), gbc);
        cbRole = new JComboBox<>(new String[]{"Employee", "Admin"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRole.setPreferredSize(new Dimension(300, 35));
        formPanel.add(cbRole, gbc);

        // Password
        formPanel.add(makeLabel("Password"), gbc);
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtPassword, gbc);

        // Confirm Password
        formPanel.add(makeLabel("Confirm Password"), gbc);
        txtConfirmPassword = new JPasswordField(20);
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtConfirmPassword, gbc);

        // Show password checkbox
        chkShowPassword = new JCheckBox("Show Passwords");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkShowPassword.setBackground(Color.WHITE);
        formPanel.add(chkShowPassword, gbc);

        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblStatus, gbc);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        btnSignUp = new JButton("Create Account");
        styleBtn(btnSignUp, new Color(39, 174, 96));

        btnClear = new JButton("Clear");
        styleBtn(btnClear, new Color(150, 150, 150));

        btnPanel.add(btnSignUp);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, gbc);

        // Back to login link
        btnBackToLogin = new JButton("← Already have an account? Login");
        btnBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnBackToLogin.setForeground(new Color(30, 60, 114));
        btnBackToLogin.setBackground(Color.WHITE);
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setOpaque(true);
        btnBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnBackToLogin, gbc);

        // ── Assemble ──────────────────────────────────────────────────────────
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel,   BorderLayout.CENTER);
        add(mainPanel);

        // ── Events ────────────────────────────────────────────────────────────
        btnSignUp.addActionListener(e -> performSignUp());
        btnClear.addActionListener(e -> clearFields());
        btnBackToLogin.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        chkShowPassword.addActionListener(e -> {
            char echo = chkShowPassword.isSelected() ? (char) 0 : '•';
            txtPassword.setEchoChar(echo);
            txtConfirmPassword.setEchoChar(echo);
        });
    }

    private void performSignUp() {
        String fullName  = txtFullName.getText().trim();
        String username  = txtUsername.getText().trim();
        String email     = txtEmail.getText().trim();
        String role      = (String) cbRole.getSelectedItem();
        String password  = new String(txtPassword.getPassword()).trim();
        String confirm   = new String(txtConfirmPassword.getPassword()).trim();

        // ── Validation ────────────────────────────────────────────────────────
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Full name, username and password are required.");
            return;
        }

        if (username.length() < 3) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 6) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirm)) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Passwords do not match. Try again.");
            txtConfirmPassword.setText("");
            return;
        }

        // ── Save to database ──────────────────────────────────────────────────
        boolean success = dao.registerUser(username, password, fullName, email, role);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\n" +
                "Username: " + username + "\nRole: " + role + "\n\nYou can now login.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            new LoginPage().setVisible(true);
            dispose();
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Username already exists. Choose another.");
        }
    }

    private void clearFields() {
        txtFullName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        cbRole.setSelectedIndex(0);
        lblStatus.setText(" ");
        txtFullName.requestFocus();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTextField makeField() {
        JTextField f = new JTextField(20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(300, 35));
        return f;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));
    }
}