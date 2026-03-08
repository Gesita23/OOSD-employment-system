package gui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Dashboard Page – Interface #2
 * Shows summary stats and the full employee table with Search.
 */
public class DashboardPage extends JFrame {

    private JLabel       lblTotal, lblActive, lblDepts;
    private JTable       employeeTable;
    private DefaultTableModel tableModel;
    private JTextField   txtSearch;
    private final EmployeeDAO dao = new EmployeeDAO();

    public DashboardPage() {
        initComponents();
        loadDashboardStats();
        loadEmployeeTable("");
    }

    private void initComponents() {
        setTitle("Employee Management System – Dashboard");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Menu Bar ─────────────────────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem miLogout = new JMenuItem("Logout");
        JMenuItem miExit   = new JMenuItem("Exit");
        menuFile.add(miLogout);
        menuFile.addSeparator();
        menuFile.add(miExit);

        JMenu menuEmployee = new JMenu("Employee");
        JMenuItem miAdd    = new JMenuItem("Add Employee");
        JMenuItem miUpdate = new JMenuItem("Update Employee");
        JMenuItem miView   = new JMenuItem("View Employees");
        JMenuItem miDelete = new JMenuItem("Delete Employee");
        menuEmployee.add(miAdd);
        menuEmployee.add(miUpdate);
        menuEmployee.add(miView);
        menuEmployee.add(miDelete);

        JMenu menuAttendance = new JMenu("Attendance");
        JMenuItem miAttendance = new JMenuItem("Attendance Entry");
        menuAttendance.add(miAttendance);

        JMenu menuHelp = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem("About");
        menuHelp.add(miAbout);

        menuBar.add(menuFile);
        menuBar.add(menuEmployee);
        menuBar.add(menuAttendance);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        // ── Top nav bar ───────────────────────────────────────────────────────
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(30, 60, 114));
        navBar.setPreferredSize(new Dimension(0, 55));
        navBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel navTitle = new JLabel("📊 Dashboard");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        navTitle.setForeground(Color.WHITE);
        navBar.add(navTitle, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        navButtons.setOpaque(false);

        JButton btnAddNav        = createNavButton("➕ Add Employee");
        JButton btnUpdateNav     = createNavButton("✏️ Update Employee");
        JButton btnViewNav       = createNavButton("👥 View Employees");
        JButton btnDeleteNav     = createNavButton("🗑 Delete Employee");
        JButton btnAttendanceNav = createNavButton("📋 Attendance");
        JButton btnLogoutNav     = createNavButton("🚪 Logout");

        navButtons.add(btnAddNav);
        navButtons.add(btnUpdateNav);
        navButtons.add(btnViewNav);
        navButtons.add(btnDeleteNav);
        navButtons.add(btnAttendanceNav);
        navButtons.add(btnLogoutNav);
        navBar.add(navButtons, BorderLayout.EAST);

        // ── Stat cards ────────────────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(new Color(240, 245, 255));
        statsPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        lblTotal  = new JLabel("0", SwingConstants.CENTER);
        lblActive = new JLabel("0", SwingConstants.CENTER);
        lblDepts  = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Employees", lblTotal, new Color(30, 60, 114)));
        statsPanel.add(createStatCard("Active Employees", lblActive, new Color(39, 174, 96)));
        statsPanel.add(createStatCard("Departments",      lblDepts,  new Color(142, 68, 173)));

        // ── Search bar ────────────────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        searchPanel.setBackground(new Color(240, 245, 255));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(280, 32));

        JButton btnSearch  = new JButton("🔍 Search");
        JButton btnRefresh = new JButton("🔄 Refresh");
        styleButton(btnSearch,  new Color(30, 60, 114));
        styleButton(btnRefresh, new Color(100, 100, 100));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // ── Employee table ────────────────────────────────────────────────────
        String[] cols = {"ID", "First Name", "Last Name", "Email", "Phone",
                         "Department", "Position", "Salary", "Hire Date", "Status"};
        tableModel   = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(26);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(new Color(30, 60, 114));
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.setSelectionBackground(new Color(180, 210, 255));

        // Alternating row colors
        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 245, 255));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(new EmptyBorder(0, 20, 20, 20));

        // ── Layout ───────────────────────────────────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 245, 255));
        centerPanel.add(statsPanel,  BorderLayout.NORTH);
        centerPanel.add(searchPanel, BorderLayout.CENTER);

        add(navBar,       BorderLayout.NORTH);
        add(centerPanel,  BorderLayout.CENTER);
        add(scrollPane,   BorderLayout.SOUTH);

        // Give scroll pane more space
        scrollPane.setPreferredSize(new Dimension(0, 350));

      
        btnSearch.addActionListener(e -> loadEmployeeTable(txtSearch.getText().trim()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadEmployeeTable(""); loadDashboardStats(); });
        btnAddNav.addActionListener(e -> openAddEmployee());
        miAdd.addActionListener(e -> openAddEmployee());
        btnUpdateNav.addActionListener(e -> openUpdateEmployee());
        miUpdate.addActionListener(e -> openUpdateEmployee());
        btnViewNav.addActionListener(e -> new ViewEmployeesGUI().buildUI());
        miView.addActionListener(e -> new ViewEmployeesGUI().buildUI());
        btnDeleteNav.addActionListener(e -> new DeleteEmployees().buildUI());
        miDelete.addActionListener(e -> new DeleteEmployees().buildUI());
        btnAttendanceNav.addActionListener(e -> new AttendanceEntryGUI().buildUI());
        miAttendance.addActionListener(e -> new AttendanceEntryGUI().buildUI());
        btnLogoutNav.addActionListener(e -> logout());
        miLogout.addActionListener(e -> logout());
        miExit.addActionListener(e -> System.exit(0));
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Employee Management System\nOOSD Assignment – SIS 2015Y", "About",
                JOptionPane.INFORMATION_MESSAGE));

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loadEmployeeTable(txtSearch.getText().trim());
            }
        });
    }

   

    private void loadDashboardStats() {
        lblTotal.setText(String.valueOf(dao.getTotalEmployees()));
        lblActive.setText(String.valueOf(dao.getActiveEmployees()));
        // Count distinct departments from table
        List<Employee> all = dao.getAllEmployees();
        long depts = all.stream().map(Employee::getDepartment).distinct().count();
        lblDepts.setText(String.valueOf(depts));
    }

    private void loadEmployeeTable(String keyword) {
        tableModel.setRowCount(0);
        List<Employee> list = keyword.isEmpty()
                ? dao.getAllEmployees()
                : dao.searchEmployees(keyword);

        for (Employee e : list) {
            tableModel.addRow(new Object[]{
                e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                e.getEmail(), e.getPhone(), e.getDepartment(),
                e.getPosition(), e.getSalary(), e.getHireDate(), e.getStatus()
            });
        }
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    private void openAddEmployee() {
        AddEmployeePage addPage = new AddEmployeePage(this);
        addPage.setVisible(true);
    }

    private void openUpdateEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee from the table first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int empId = (int) tableModel.getValueAt(row, 0);
        Employee emp = dao.getEmployeeById(empId);
        if (emp != null) new UpdateEmployeePage(this, emp).setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }

    /** Called by child pages after add/update to refresh. */
    public void refresh() {
        loadDashboardStats();
        loadEmployeeTable(txtSearch.getText().trim());
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new CompoundBorder(
                new LineBorder(color.darker(), 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(200, 230, 255));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(60, 100, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}