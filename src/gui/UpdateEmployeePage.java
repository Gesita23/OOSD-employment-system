package gui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Update Employee Page – Interface #4
 */
public class UpdateEmployeePage extends JDialog {

    private JTextField    txtFirstName, txtLastName, txtEmail, txtPhone,
                          txtPosition, txtSalary, txtHireDate;
    private JComboBox<String> cbDepartment, cbStatus;
    private JButton       btnUpdate, btnReset, btnClose;
    private JLabel        lblEmpId;

    private final Employee      originalEmployee;
    private final EmployeeDAO   dao;
    private final DashboardPage parent;

    private static final String[] DEPARTMENTS = {
        "HR", "IT", "Finance", "Marketing",
        "Operations", "Sales", "Legal", "Engineering"
    };
    private static final String[] STATUSES = {"Active", "Inactive", "On Leave"};

    public UpdateEmployeePage(DashboardPage parent, Employee emp) {
        super(parent, "Update Employee", true);
        this.parent           = parent;
        this.originalEmployee = emp;
        this.dao              = new EmployeeDAO();
        initComponents();
        populateForm(emp);
    }

    private void initComponents() {
        setSize(520, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(new Color(52, 152, 219));
        JLabel lblHeader = new JLabel("✏️ Update Employee");
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

        // Employee ID (read-only)
        lblEmpId     = new JLabel();
        lblEmpId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmpId.setForeground(new Color(52, 152, 219));

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

        int row = 0;
        addFormRow(form, gbc, row++, "Employee ID",  lblEmpId);
        addFormRow(form, gbc, row++, "First Name *", txtFirstName);
        addFormRow(form, gbc, row++, "Last Name *",  txtLastName);
        addFormRow(form, gbc, row++, "Email *",      txtEmail);
        addFormRow(form, gbc, row++, "Phone",        txtPhone);
        addFormRow(form, gbc, row++, "Department *", cbDepartment);
        addFormRow(form, gbc, row++, "Position *",   txtPosition);
        addFormRow(form, gbc, row++, "Salary",       txtSalary);
        addFormRow(form, gbc, row++, "Hire Date",    txtHireDate);
        addFormRow(form, gbc, row,   "Status",       cbStatus);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        btnUpdate = new JButton("💾 Save Changes");
        btnReset  = new JButton("↩ Reset");
        btnClose  = new JButton("✖ Close");

        styleBtn(btnUpdate, new Color(52, 152, 219));
        styleBtn(btnReset,  new Color(230, 126, 34));
        styleBtn(btnClose,  new Color(150, 150, 150));

        btnPanel.add(btnUpdate);
        btnPanel.add(btnReset);
        btnPanel.add(btnClose);

        mainPanel.add(header,   BorderLayout.NORTH);
        mainPanel.add(form,     BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ── Events ────────────────────────────────────────────────────────────
        btnUpdate.addActionListener(e -> updateEmployee());
        btnReset.addActionListener(e  -> populateForm(originalEmployee));
        btnClose.addActionListener(e  -> dispose());
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

        // Set department combo
        for (int i = 0; i < cbDepartment.getItemCount(); i++) {
            if (cbDepartment.getItemAt(i).equals(emp.getDepartment())) {
                cbDepartment.setSelectedIndex(i);
                break;
            }
        }
        // Set status combo
        for (int i = 0; i < cbStatus.getItemCount(); i++) {
            if (cbStatus.getItemAt(i).equals(emp.getStatus())) {
                cbStatus.setSelectedIndex(i);
                break;
            }
        }
    }

    private void updateEmployee() {
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
                "Are you sure you want to update this employee?",
                "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Employee emp = new Employee();
        emp.setEmployeeId(originalEmployee.getEmployeeId());
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
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update employee. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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