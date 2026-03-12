package gui;

import dao.EmployeeDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import model.Employee;

/**
 * Update Employee – standalone page with its own Employee ID search.
 * No longer requires ViewEmployees to be open first.
 */
public class UpdateEmployeePage extends JFrame {

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final EmployeeDAO   dao    = new EmployeeDAO();
    private final DashboardPage parent;

    // ── Search panel components ───────────────────────────────────────────────
    private JTextField  txtSearchId;
    private JButton     btnSearch;

    // ── Form panel ───────────────────────────────────────────────────────────
    private JPanel      formPanel;
    private JTextField  txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtPosition,  txtSalary,   txtHireDate;
    private JComboBox<String> cbDepartment, cbStatus;
    private JLabel      lblEmpId;
    private JButton     btnUpdate, btnReset, btnClear;

    // ── State ────────────────────────────────────────────────────────────────
    private Employee loadedEmployee = null;

    private static final String[] DEPARTMENTS = {
        "HR", "IT", "Finance", "Marketing",
        "Operations", "Sales", "Legal", "Engineering"
    };
    private static final String[] STATUSES = {"Active", "Inactive", "On Leave"};

    // ── Colours ──────────────────────────────────────────────────────────────
    private static final Color PRIMARY    = new Color(52, 152, 219);
    private static final Color BG         = new Color(0xF4F6F9);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xDDE3EC);
    private static final Color TEXT_MUTED = new Color(0x7A8A9C);
    private static final Color SUCCESS    = new Color(0x2D9B6F);
    private static final Color DANGER     = new Color(0xE63946);

    public UpdateEmployeePage(DashboardPage parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setTitle("Employee Management System  ·  Update Employee");
        setSize(560, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildBody(),      BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        // Form starts hidden until an employee is loaded
        showForm(false);
        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("✏️  Update Employee");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Search by Employee ID, edit details, then save.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0xAABDD6));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(sub);

        header.add(text, BorderLayout.CENTER);
        return header;
    }

    // ── Body ─────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 28, 16, 28));

        body.add(buildSearchCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildFormCard());

        return body;
    }

    // ── Search card ──────────────────────────────────────────────────────────
    private JPanel buildSearchCard() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lbl = new JLabel("Employee ID");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);

        txtSearchId = new JTextField();
        txtSearchId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearchId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(7, 10, 7, 10)));
        txtSearchId.addActionListener(e -> handleSearch());

        btnSearch = styledBtn("Search", PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> handleSearch());

        JPanel left = new JPanel(new BorderLayout(0, 5));
        left.setOpaque(false);
        left.add(lbl,         BorderLayout.NORTH);
        left.add(txtSearchId, BorderLayout.CENTER);

        card.add(left,      BorderLayout.CENTER);
        card.add(btnSearch, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        wrap.add(card);
        return wrap;
    }

    // ── Form card ─────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        // ── Fields grid ──
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        lblEmpId     = new JLabel();
        lblEmpId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmpId.setForeground(PRIMARY);

        txtFirstName = field();
        txtLastName  = field();
        txtEmail     = field();
        txtPhone     = field();
        txtPosition  = field();
        txtSalary    = field();
        txtHireDate  = field();
        txtHireDate.setToolTipText("Format: YYYY-MM-DD");

        cbDepartment = new JComboBox<>(DEPARTMENTS);
        cbStatus     = new JComboBox<>(STATUSES);

        int row = 0;
        addRow(grid, gbc, row++, "Employee ID",  lblEmpId);
        addRow(grid, gbc, row++, "First Name *", txtFirstName);
        addRow(grid, gbc, row++, "Last Name *",  txtLastName);
        addRow(grid, gbc, row++, "Email *",      txtEmail);
        addRow(grid, gbc, row++, "Phone",        txtPhone);
        addRow(grid, gbc, row++, "Department *", cbDepartment);
        addRow(grid, gbc, row++, "Position *",   txtPosition);
        addRow(grid, gbc, row++, "Salary",       txtSalary);
        addRow(grid, gbc, row++, "Hire Date",    txtHireDate);
        addRow(grid, gbc, row,   "Status",       cbStatus);

        // ── Buttons ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnRow.setBackground(CARD_BG);

        btnUpdate = styledBtn("💾 Save Changes", PRIMARY,               Color.WHITE);
        btnReset  = styledBtn("↩ Reset",         new Color(230,126,34), Color.WHITE);
        btnClear  = styledBtn("✖ Close",         new Color(150,150,150), Color.WHITE);

        btnRow.add(btnUpdate);
        btnRow.add(btnReset);
        btnRow.add(btnClear);

        btnUpdate.addActionListener(e -> handleUpdate());
        btnReset.addActionListener(e  -> { if (loadedEmployee != null) populateForm(loadedEmployee); });
        btnClear.addActionListener(e  -> dispose());

        formPanel.add(grid,   BorderLayout.CENTER);
        formPanel.add(btnRow, BorderLayout.SOUTH);

        return formPanel;
    }

    // ── Show / hide form ─────────────────────────────────────────────────────
    private void showForm(boolean visible) {
        formPanel.setVisible(visible);
    }

    // ── Status bar ───────────────────────────────────────────────────────────
    JLabel statusBar;
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0xEAEFF5));
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(6, 16, 6, 16)));
        statusBar = new JLabel("Enter an Employee ID above and click Search.");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(TEXT_MUTED);
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }

    // ── Handlers ─────────────────────────────────────────────────────────────
    private void handleSearch() {
        String text = txtSearchId.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID must be numeric.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Real DB lookup
        loadedEmployee = dao.getEmployeeById(id);

        if (loadedEmployee != null) {
            populateForm(loadedEmployee);
            showForm(true);
            revalidate();
            repaint();
            setStatus("Employee found: " + loadedEmployee.getFirstName()
                    + " " + loadedEmployee.getLastName() + ". Edit and save below.", TEXT_MUTED);
        } else {
            showForm(false);
            revalidate();
            repaint();
            setStatus("No employee found with ID " + id + ".", DANGER);
        }
    }

    private void populateForm(Employee emp) {
        lblEmpId.setText("EMP-" + emp.getEmployeeId());
        txtFirstName.setText(emp.getFirstName());
        txtLastName.setText(emp.getLastName());
        txtEmail.setText(emp.getEmail());
        txtPhone.setText(emp.getPhone());
        txtPosition.setText(emp.getPosition());
        txtSalary.setText(String.valueOf(emp.getSalary()));
        txtHireDate.setText(emp.getHireDate());

        for (int i = 0; i < cbDepartment.getItemCount(); i++) {
            if (cbDepartment.getItemAt(i).equals(emp.getDepartment())) {
                cbDepartment.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < cbStatus.getItemCount(); i++) {
            if (cbStatus.getItemAt(i).equals(emp.getStatus())) {
                cbStatus.setSelectedIndex(i); break;
            }
        }
    }

    private void handleUpdate() {
        if (loadedEmployee == null) return;

        if (txtFirstName.getText().trim().isEmpty() ||
            txtLastName.getText().trim().isEmpty()  ||
            txtEmail.getText().trim().isEmpty()     ||
            txtPosition.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (*).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double salary = 0;
        if (!txtSalary.getText().trim().isEmpty()) {
            try {
                salary = Double.parseDouble(txtSalary.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a number.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to save these changes?",
                "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Employee emp = new Employee();
        emp.setEmployeeId(loadedEmployee.getEmployeeId());
        emp.setFirstName(txtFirstName.getText().trim());
        emp.setLastName(txtLastName.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setPhone(txtPhone.getText().trim());
        emp.setDepartment((String) cbDepartment.getSelectedItem());
        emp.setPosition(txtPosition.getText().trim());
        emp.setSalary(salary);
        emp.setHireDate(txtHireDate.getText().trim());
        emp.setStatus((String) cbStatus.getSelectedItem());

        if (dao.updateEmployee(emp)) {
            JOptionPane.showMessageDialog(this,
                    "Employee updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            parent.refresh();
            setStatus("Employee EMP-" + emp.getEmployeeId() + " updated successfully.", SUCCESS);
            // Reset search so user can update another employee
            loadedEmployee = null;
            txtSearchId.setText("");
            showForm(false);
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update employee. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Update failed — please try again.", DANGER);
        }
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JTextField field() {
        JTextField f = new JTextField(18);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return f;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String labelText, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private JButton styledBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }
}