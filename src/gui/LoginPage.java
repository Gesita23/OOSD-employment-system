package gui;

import dao.EmployeeDAO;
import database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin, btnClear, btnSignUp;
    private JLabel         lblStatus;
    private JCheckBox      chkShowPassword;

    private final EmployeeDAO dao = new EmployeeDAO();

    public LoginPage() {
        initComponents();
        warmUpConnection();   // connect in background — UI stays responsive
    }

    // ── Warm up DB connection in background so UI never freezes ──────────────
    private void warmUpConnection() {
        new Thread(() -> {
            DBConnection.getConnection();  // fires once silently in background
        }, "db-warmup").start();
    }

    private void initComponents() {
        setTitle("Employee Management System – Login");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 60, 114));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 60, 114));
        headerPanel.setBorder(new EmptyBorder(25, 0, 15, 0));

        JLabel lblTitle = new JLabel("Employee Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Please sign in to continue");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(180, 210, 255));

        JPanel subPanel = new JPanel();
        subPanel.setBackground(new Color(30, 60, 114));
        subPanel.add(lblSubtitle);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 60, 114));
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(subPanel,    BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(6, 0, 6, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 0;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(300, 35));
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 2;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(300, 35));
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkShowPassword.setBackground(Color.WHITE);
        gbc.gridy = 4;
        formPanel.add(chkShowPassword, gbc);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        formPanel.add(lblStatus, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(30, 60, 114));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 38));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(150, 150, 150));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setOpaque(true);
        btnClear.setBorderPainted(false);

        btnPanel.add(btnLogin);
        btnPanel.add(btnClear);
        gbc.gridy = 6;
        formPanel.add(btnPanel, gbc);

        btnSignUp = new JButton("Don't have an account? Sign Up");
        btnSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSignUp.setForeground(new Color(30, 60, 114));
        btnSignUp.setBackground(Color.WHITE);
        btnSignUp.setBorderPainted(false);
        btnSignUp.setFocusPainted(false);
        btnSignUp.setOpaque(true);
        btnSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        formPanel.add(btnSignUp, gbc);

        mainPanel.add(topPanel,   BorderLayout.NORTH);
        mainPanel.add(formPanel,  BorderLayout.CENTER);
        add(mainPanel);

        btnLogin.addActionListener(e -> performLogin());
        btnClear.addActionListener(e -> clearFields());
        btnSignUp.addActionListener(e -> {
            new SignUpPage().setVisible(true);
            dispose();
        });

        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•');
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both username and password.");
            return;
        }

        // Disable button + show checking message so user knows it's working
        btnLogin.setEnabled(false);
        lblStatus.setForeground(new Color(30, 60, 114));
        lblStatus.setText("Checking credentials...");

        // Run DB check in background thread — never blocks the UI
        new Thread(() -> {
            boolean valid = dao.validateLogin(username, password);
            SwingUtilities.invokeLater(() -> {
                btnLogin.setEnabled(true);
                if (valid) {
                    lblStatus.setText(" ");
                    new DashboardPage().setVisible(true);
                    dispose();
                } else {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Invalid username or password. Try again.");
                    txtPassword.setText("");
                }
            });
        }, "login-thread").start();
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblStatus.setText(" ");
        txtUsername.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new LoginPage().setVisible(true);
        });
    }
}