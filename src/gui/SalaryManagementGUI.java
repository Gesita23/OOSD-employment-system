package gui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

public class SalaryManagementGUI {
    
    // ── Colour palette (matches ViewEmployeesGUI) ────────────────────────────
    private static final Color BG         = new Color(0xF4F6F9);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color PRIMARY    = new Color(0x1A3C6E);
    private static final Color ACCENT     = new Color(0x2D9B6F);
    private static final Color BORDER_CLR = new Color(0xDDE3EC);
    private static final Color TEXT_MAIN  = new Color(0x1C2B3A);
    private static final Color TEXT_MUTED = new Color(0x7A8A9C);
    private static final Color FIELD_BG   = new Color(0xF0F4FA);
    private static final Color ROW_ALT    = new Color(0xF8FAFD);
    private static final Color ROW_SEL    = new Color(0xD6E4F7);
    private static final Color WARN_CLR   = new Color(0xE67E22);
    private static final Color DANGER_CLR = new Color(0xE63946);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_TABLE  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);

    private static final DecimalFormat MONEY_FMT = new DecimalFormat("#,##0.00");

    // ── DAO & data ───────────────────────────────────────────────────────────
    private final EmployeeDAO        dao  = new EmployeeDAO();
    private final List<Employee>     allEmployees = new ArrayList<>();

    // ── UI components ────────────────────────────────────────────────────────
    private JFrame            frame;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        txtSearch;
    private JLabel            lblTotalPayroll, lblAvgSalary, lblHighest, lblLowest;
    private JComboBox<String> cbDeptFilter;

    // ── Column indices ───────────────────────────────────────────────────────
    private static final int COL_ID   = 0;
    private static final int COL_NAME = 1;
    private static final int COL_DEPT = 2;
    private static final int COL_POS  = 3;
    private static final int COL_SAL  = 4;
    private static final int COL_STA  = 5;

    public void buildUI() {
        loadData();

        frame = new JFrame("💰 Salary Management");
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(900, 580));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(BG);
        frame.setLayout(new BorderLayout(0, 0));

        frame.add(buildHeader(),  BorderLayout.NORTH);
        frame.add(buildCenter(),  BorderLayout.CENTER);

        frame.setVisible(true);
        refreshTable(allEmployees);
        updateSummaryCards();
    }

    // ── Load ─────────────────────────────────────────────────────────────────
    private void loadData() {
        allEmployees.clear();
        allEmployees.addAll(dao.getAllEmployees());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HEADER
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY, getWidth(), 0, new Color(0x2D5AA0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 62));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));

        JLabel title = new JLabel("💰  Salary Management");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnClose = makeBtn("✖  Close", new Color(0xC0392B));
        btnClose.addActionListener(e -> frame.dispose());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 13));
        right.setOpaque(false);
        right.add(btnClose);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CENTER
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(18, 18, 18, 18));

        center.add(buildSummaryRow(), BorderLayout.NORTH);
        center.add(buildTableCard(),  BorderLayout.CENTER);
        return center;
    }

    // ── Summary cards ────────────────────────────────────────────────────────
    private JPanel buildSummaryRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 105));

        lblTotalPayroll = makeStatLabel("0.00");
        lblAvgSalary    = makeStatLabel("0.00");
        lblHighest      = makeStatLabel("0.00");
        lblLowest       = makeStatLabel("0.00");

        row.add(makeStatCard("💵  Total Monthly Payroll", lblTotalPayroll,
                new Color(0x1A3C6E), new Color(0x2D5AA0)));
        row.add(makeStatCard("📊  Average Salary",        lblAvgSalary,
                new Color(0x1A6E3C), new Color(0x2D9B6F)));
        row.add(makeStatCard("⬆  Highest Salary",         lblHighest,
                new Color(0x6E4A1A), new Color(0xC0892B)));
        row.add(makeStatCard("⬇  Lowest Salary",          lblLowest,
                new Color(0x6E1A1A), new Color(0xC03030)));
        return row;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color c1, Color c2) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(200, 225, 255));
        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel makeStatLabel(String text) {
        JLabel lbl = new JLabel("Rs " + text, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    // ── Table card ───────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(16, 16, 16, 16)));

        card.add(buildToolbar(), BorderLayout.NORTH);
        card.add(buildTable(),   BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);
        return card;
    }

    // ── Toolbar (search + filter) ─────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lbl = new JLabel("👥  Employee Salary Records");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(PRIMARY);
        bar.add(lbl, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        // Department filter
        cbDeptFilter = new JComboBox<>(new String[]{"All Departments","HR","IT","Finance",
                "Marketing","Operations","Sales","Legal","Engineering"});
        cbDeptFilter.setFont(FONT_FIELD);
        cbDeptFilter.setPreferredSize(new Dimension(165, 34));
        cbDeptFilter.addActionListener(e -> applyFilters());

        // Search
        txtSearch = new JTextField(16);
        txtSearch.setFont(FONT_FIELD);
        txtSearch.setBackground(FIELD_BG);
        txtSearch.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        txtSearch.setToolTipText("Search by name, department or position…");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilters(); }
        });

        JButton btnRefresh = makeBtn("🔄 Refresh", new Color(0x27AE60));
        btnRefresh.addActionListener(e -> { loadData(); applyFilters(); updateSummaryCards(); });

        right.add(new JLabel("Filter:"));
        right.add(cbDeptFilter);
        right.add(new JLabel("  Search:"));
        right.add(txtSearch);
        right.add(btnRefresh);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Table ────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Full Name", "Department", "Position", "Salary (Rs)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(FONT_TABLE);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT_MAIN);
        table.setFillsViewportHeight(true);

        // Header style
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(0xEEF2FB));
        th.setForeground(PRIMARY);
        th.setPreferredSize(new Dimension(0, 38));
        th.setBorder(new MatteBorder(0, 0, 2, 0, BORDER_CLR));

        // Column widths
        int[] widths = {55, 180, 120, 150, 130, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Alternating rows + salary renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(FONT_TABLE);
                setForeground(TEXT_MAIN);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);

                if (col == COL_SAL) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(new Color(0x1A6E3C));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (col == COL_STA && val != null) {
                    String s = val.toString();
                    if      ("Active".equalsIgnoreCase(s))   setForeground(ACCENT);
                    else if ("Inactive".equalsIgnoreCase(s)) setForeground(DANGER_CLR);
                    else                                      setForeground(WARN_CLR);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER_CLR, 1, true));
        sp.getViewport().setBackground(CARD_BG);
        return sp;
    }

    // ── Action buttons ────────────────────────────────────────────────────────
    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setOpaque(false);

        JButton btnRaise   = makeBtn("💹 Give Raise",       new Color(0x27AE60));
        JButton btnAdjust  = makeBtn("✏️ Set Salary",       new Color(0x2980B9));
        JButton btnBulk    = makeBtn("📈 Bulk % Raise",     new Color(0x8E44AD));
        JButton btnExport  = makeBtn("📋 Copy to Clipboard",new Color(0x7F8C8D));

        btnRaise.addActionListener(e  -> doGiveRaise());
        btnAdjust.addActionListener(e -> doSetSalary());
        btnBulk.addActionListener(e   -> doBulkRaise());
        btnExport.addActionListener(e -> doExport());

        panel.add(btnRaise);
        panel.add(btnAdjust);
        panel.add(btnBulk);
        panel.add(btnExport);

        // Hint label
        JLabel hint = new JLabel("  Select a row then choose an action");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_MUTED);
        panel.add(hint);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DATA / DISPLAY HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private void refreshTable(List<Employee> list) {
        tableModel.setRowCount(0);
        for (Employee e : list) {
            tableModel.addRow(new Object[]{
                e.getEmployeeId(),
                e.getFirstName() + " " + e.getLastName(),
                e.getDepartment(),
                e.getPosition(),
                "Rs " + MONEY_FMT.format(e.getSalary()),
                e.getStatus()
            });
        }
    }

    private void applyFilters() {
        String search = txtSearch.getText().trim().toLowerCase();
        String dept   = (String) cbDeptFilter.getSelectedItem();

        List<Employee> filtered = new ArrayList<>();
        for (Employee e : allEmployees) {
            boolean matchDept   = "All Departments".equals(dept) || dept.equals(e.getDepartment());
            boolean matchSearch = search.isEmpty()
                    || (e.getFirstName() + " " + e.getLastName()).toLowerCase().contains(search)
                    || e.getDepartment().toLowerCase().contains(search)
                    || e.getPosition().toLowerCase().contains(search);
            if (matchDept && matchSearch) filtered.add(e);
        }
        refreshTable(filtered);
        updateSummaryCards(filtered);
    }

    private void updateSummaryCards() { updateSummaryCards(allEmployees); }

    private void updateSummaryCards(List<Employee> list) {
        if (list.isEmpty()) {
            lblTotalPayroll.setText("Rs 0.00");
            lblAvgSalary.setText("Rs 0.00");
            lblHighest.setText("Rs 0.00");
            lblLowest.setText("Rs 0.00");
            return;
        }
        double total   = list.stream().mapToDouble(Employee::getSalary).sum();
        double avg     = total / list.size();
        double highest = list.stream().mapToDouble(Employee::getSalary).max().orElse(0);
        double lowest  = list.stream().mapToDouble(Employee::getSalary).min().orElse(0);

        lblTotalPayroll.setText("Rs " + MONEY_FMT.format(total));
        lblAvgSalary.setText("Rs "    + MONEY_FMT.format(avg));
        lblHighest.setText("Rs "      + MONEY_FMT.format(highest));
        lblLowest.setText("Rs "       + MONEY_FMT.format(lowest));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ACTIONS
    // ════════════════════════════════════════════════════════════════════════

    /** Give selected employee a fixed-amount raise */
    private void doGiveRaise() {
        int row = table.getSelectedRow();
        if (row < 0) { showNoSelection(); return; }
        int empId = (int) tableModel.getValueAt(row, COL_ID);
        Employee emp = dao.getEmployeeById(empId);
        if (emp == null) return;

        String input = JOptionPane.showInputDialog(frame,
                "Enter raise amount (Rs) for " + emp.getFirstName() + " " + emp.getLastName()
                + "\nCurrent salary: Rs " + MONEY_FMT.format(emp.getSalary()),
                "Give Raise", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            double raise = Double.parseDouble(input.trim());
            if (raise <= 0) { JOptionPane.showMessageDialog(frame, "Amount must be positive."); return; }

            double newSalary = emp.getSalary() + raise;
            int confirm = JOptionPane.showConfirmDialog(frame,
                    String.format("Raise salary for %s %s from Rs %s to Rs %s?",
                            emp.getFirstName(), emp.getLastName(),
                            MONEY_FMT.format(emp.getSalary()),
                            MONEY_FMT.format(newSalary)),
                    "Confirm Raise", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            emp.setSalary(newSalary);
            if (dao.updateEmployee(emp)) {
                JOptionPane.showMessageDialog(frame,
                        "✅ Salary updated successfully!\nNew salary: Rs " + MONEY_FMT.format(newSalary),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); applyFilters(); updateSummaryCards();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update salary. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Set an exact salary for the selected employee */
    private void doSetSalary() {
        int row = table.getSelectedRow();
        if (row < 0) { showNoSelection(); return; }
        int empId = (int) tableModel.getValueAt(row, COL_ID);
        Employee emp = dao.getEmployeeById(empId);
        if (emp == null) return;

        String input = JOptionPane.showInputDialog(frame,
                "Enter new salary (Rs) for " + emp.getFirstName() + " " + emp.getLastName()
                + "\nCurrent salary: Rs " + MONEY_FMT.format(emp.getSalary()),
                "Set Salary", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            double newSal = Double.parseDouble(input.trim());
            if (newSal < 0) { JOptionPane.showMessageDialog(frame, "Salary cannot be negative."); return; }

            int confirm = JOptionPane.showConfirmDialog(frame,
                    String.format("Set salary for %s %s to Rs %s?",
                            emp.getFirstName(), emp.getLastName(), MONEY_FMT.format(newSal)),
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            emp.setSalary(newSal);
            if (dao.updateEmployee(emp)) {
                JOptionPane.showMessageDialog(frame,
                        "✅ Salary set to Rs " + MONEY_FMT.format(newSal),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); applyFilters(); updateSummaryCards();
            } else {
                JOptionPane.showMessageDialog(frame, "Update failed. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Apply a percentage raise to ALL currently visible (filtered) employees */
    private void doBulkRaise() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No employees in the current view.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String input = JOptionPane.showInputDialog(frame,
                "Enter percentage raise (%) to apply to all " + tableModel.getRowCount() + " visible employees:",
                "Bulk % Raise", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            double pct = Double.parseDouble(input.trim());
            if (pct <= 0 || pct > 100) {
                JOptionPane.showMessageDialog(frame, "Enter a percentage between 0 and 100.", "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    String.format("Apply a %.1f%% raise to %d employees?", pct, tableModel.getRowCount()),
                    "Confirm Bulk Raise", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            int success = 0, fail = 0;
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                int empId = (int) tableModel.getValueAt(r, COL_ID);
                Employee emp = dao.getEmployeeById(empId);
                if (emp == null) { fail++; continue; }
                emp.setSalary(emp.getSalary() * (1 + pct / 100.0));
                if (dao.updateEmployee(emp)) success++;
                else fail++;
            }

            JOptionPane.showMessageDialog(frame,
                    String.format("✅ Done! %d updated, %d failed.", success, fail),
                    "Bulk Raise Complete", JOptionPane.INFORMATION_MESSAGE);
            loadData(); applyFilters(); updateSummaryCards();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Copy the visible table data to the system clipboard as plain text */
    private void doExport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "Nothing to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID\tFull Name\tDepartment\tPosition\tSalary\tStatus\n");
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                sb.append(tableModel.getValueAt(r, c));
                if (c < tableModel.getColumnCount() - 1) sb.append("\t");
            }
            sb.append("\n");
        }
        java.awt.datatransfer.StringSelection ss =
                new java.awt.datatransfer.StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        JOptionPane.showMessageDialog(frame,
                "✅ " + tableModel.getRowCount() + " records copied to clipboard.",
                "Exported", JOptionPane.INFORMATION_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ════════════════════════════════════════════════════════════════════════
    private void showNoSelection() {
        JOptionPane.showMessageDialog(frame, "Please select an employee row first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}

