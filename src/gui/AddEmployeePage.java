package gui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Add Employee Page – Interface #3
 */
public class AddEmployeePage extends JDialog {

    private JTextField   txtFirstName, txtLastName, txtEmail, txtPhone,
                         txtPosition, txtSalary, txtHireDate;
    private JComboBox<String> cbDepartment, cbStatus;
    private JButton      btnSave, btnClear, btnClose;

    private final EmployeeDAO     dao;
    private final DashboardPage   parent;

    private static final String[] DEPARTMENTS = {
        "Select Department", "HR", "IT", "Finance", "Marketing",
        "Operations", "Sales", "Legal", "Engineering"
    };
    private static final String[] STATUSES = {"Active", "Inactive", "On Leave"};

    public AddEmployeePage(DashboardPage parent) {
        super(parent, "Add New Employee", true);
        this.parent = parent;
        this.dao    = new EmployeeDAO();
        initComponents();
    }

    private void initComponents() {
        setSize(520, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(new Color(39, 174, 96));
        JLabel lblHeader = new JLabel("➕ Add New Employee");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader);

        // ── Form ─────────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 30, 10, 30));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtFirstName = new JTextField(18);
        txtLastName  = new JTextField(18);
        txtEmail     = new JTextField(18);
        txtPhone     = new JTextField(18);
        txtPosition  = new JTextField(18);
        txtSalary    = new JTextField(18);
        txtHireDate  = new JTextField(18);
        txtHireDate.setToolTipText("Format: YYYY-MM-DD");

        cbDepartment = new JComboBox<>(DEPARTMENTS);
        cbStatus     = new JComboBox<>(STATUSES);

        // Row helper
        int row = 0;
        addFormRow(form, gbc, row++, "First Name *",  txtFirstName);
        addFormRow(form, gbc, row++, "Last Name *",   txtLastName);
        addFormRow(form, gbc, row++, "Email *",       txtEmail);
        addFormRow(form, gbc, row++, "Phone",         txtPhone);
        addFormRow(form, gbc, row++, "Department *",  cbDepartment);
        addFormRow(form, gbc, row++, "Position *",    txtPosition);
        addFormRow(form, gbc, row++, "Salary",        txtSalary);
        addFormRow(form, gbc, row++, "Hire Date",     txtHireDate);
        addFormRow(form, gbc, row,   "Status",        cbStatus);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        btnSave  = new JButton("💾 Save Employee");
        btnClear = new JButton("🗑 Clear");
        btnClose = new JButton("✖ Close");

        styleBtn(btnSave,  new Color(39, 174, 96));
        styleBtn(btnClear, new Color(230, 126, 34));
        styleBtn(btnClose, new Color(150, 150, 150));

        btnPanel.add(btnSave);
        btnPanel.add(btnClear);
        btnPanel.add(btnClose);

        mainPanel.add(header,   BorderLayout.NORTH);
        mainPanel.add(form,     BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ── Events ────────────────────────────────────────────────────────────
        btnSave.addActionListener(e -> saveEmployee());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());
    }

    private void saveEmployee() {
        // Validation
        if (txtFirstName.getText().trim().isEmpty() ||
            txtLastName.getText().trim().isEmpty()  ||
            txtEmail.getText().trim().isEmpty()     ||
            txtPosition.getText().trim().isEmpty()  ||
            cbDepartment.getSelectedIndex() == 0) {

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

        Employee emp = new Employee();
        emp.setFirstName((String) txtFirstName.getText().trim());
        emp.setLastName(txtLastName.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setPhone(txtPhone.getText().trim());
        emp.setDepartment((String) cbDepartment.getSelectedItem());
        emp.setPosition(txtPosition.getText().trim());
        emp.setSalary(salary);
        emp.setHireDate(txtHireDate.getText().trim().isEmpty()
                ? java.time.LocalDate.now().toString()
                : txtHireDate.getText().trim());
        emp.setStatus((String) cbStatus.getSelectedItem());

        if (dao.addEmployee(emp)) {
            JOptionPane.showMessageDialog(this,
                    "Employee added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            parent.refresh();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to add employee. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtPosition.setText("");
        txtSalary.setText("");
        txtHireDate.setText("");
        cbDepartment.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtFirstName.requestFocus();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        if (comp instanceof JTextField)
            ((JTextField) comp).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comp, gbc);
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(150, 35));
    }
}