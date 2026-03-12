package gui;

import dao.EmployeeDAO;
import model.Employee;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ViewEmployeesGUI {

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final EmployeeDAO dao = new EmployeeDAO();

    // ── Colour palette ───────────────────────────────────────────────────────
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
    private static final Color BADGE_ENG  = new Color(0xDDEAFF);
    private static final Color BADGE_SAL  = new Color(0xDDF5E8);
    private static final Color BADGE_HR   = new Color(0xFFF3DD);
    private static final Color BADGE_FIN  = new Color(0xFFE8E8);
    private static final Color ACTIVE_CLR = new Color(0x2D9B6F);
    private static final Color INACTIVE   = new Color(0xE63946);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_TABLE  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);

    // ── Data ─────────────────────────────────────────────────────────────────
    private List<Employee> allEmployees = new ArrayList<>();

    // ── UI components ────────────────────────────────────────────────────────
    private JFrame            frame;
    private JTextField        searchField;
    private JComboBox<String> deptFilter;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            countLabel;
    private JLabel            statusBar;

    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new ViewEmployeesGUI().buildUI());
    }

    // ── Load from database ───────────────────────────────────────────────────
    private void loadFromDB() {
        allEmployees = dao.getAllEmployees();
    }

    // ── Convenience: first + last name joined ────────────────────────────────
    private String fullName(Employee e) {
        return e.getFirstName() + " " + e.getLastName();
    }

    // ── Build the window ─────────────────────────────────────────────────────
    public void buildUI() {
        loadFromDB();

        frame = new JFrame("Employee Management System  ·  View Employees");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(980, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(760, 480));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        frame.setContentPane(root);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildBody(),      BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        populateDeptFilter();

        frame.setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Employee Directory");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("View, search and filter all employees in the system.");
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
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 0, 24));

        body.add(buildToolbar(),   BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);

        return body;
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        searchField = new JTextField(22);
        searchField.setFont(FONT_FIELD);
        searchField.setBackground(CARD_BG);
        searchField.setForeground(TEXT_MAIN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(7, 12, 7, 12)));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, ID or email…");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilters(); }
        });

        // Populated dynamically after DB load
        deptFilter = new JComboBox<>(new String[]{"All Departments"});
        deptFilter.setFont(FONT_FIELD);
        deptFilter.setBackground(CARD_BG);
        deptFilter.setForeground(TEXT_MAIN);
        deptFilter.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        deptFilter.addActionListener(e -> applyFilters());

        left.add(searchIcon());
        left.add(searchField);
        left.add(Box.createHorizontalStrut(4));
        left.add(new JLabel("Department:"));
        left.add(deptFilter);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_MUTED);

        JButton refreshBtn = styledButton("⟳  Refresh", PRIMARY, Color.WHITE);
        refreshBtn.addActionListener(e -> handleRefresh());

        right.add(countLabel);
        right.add(refreshBtn);

        toolbar.add(left,  BorderLayout.CENTER);
        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    // ── Table card ───────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));

        String[] columns = {"ID", "Full Name", "Department", "Position", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                } else {
                    c.setBackground(ROW_SEL);
                }
                c.setForeground(TEXT_MAIN);
                return c;
            }
        };

        table.setFont(FONT_TABLE);
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_CLR);
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT_MAIN);
        table.setFocusable(false);
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(0xEEF2F8));
        th.setForeground(TEXT_MUTED);
        th.setPreferredSize(new Dimension(0, 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));

        int[] widths = {60, 160, 120, 160, 190, 100, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Status badge renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val == null ? "" : val.toString(), SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);
                boolean active = "Active".equalsIgnoreCase(val == null ? "" : val.toString());
                lbl.setForeground(active ? ACTIVE_CLR : INACTIVE);
                lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? CARD_BG : ROW_ALT));
                return lbl;
            }
        });

        // Department badge renderer
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val == null ? "" : val.toString(), SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);
                String dept = val == null ? "" : val.toString();
                Color bg;
                if      ("Engineering".equals(dept)) bg = BADGE_ENG;
                else if ("Sales".equals(dept))       bg = BADGE_SAL;
                else if ("HR".equals(dept))          bg = BADGE_HR;
                else if ("Finance".equals(dept))     bg = BADGE_FIN;
                else                                 bg = FIELD_BG;
                lbl.setBackground(sel ? ROW_SEL : bg);
                lbl.setForeground(TEXT_MAIN);
                return lbl;
            }
        });

        // Centre-align ID column
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CARD_BG);

        card.add(scroll, BorderLayout.CENTER);

        populateTable(allEmployees);
        return card;
    }

    // ── Status bar ───────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0xEAEFF5));
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(6, 16, 6, 16)));

        statusBar = new JLabel("Showing all employees.");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(TEXT_MUTED);
        bar.add(statusBar, BorderLayout.WEST);

        JLabel info = new JLabel("Live data from SQL Server.");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        info.setForeground(new Color(0xAABDD6));
        bar.add(info, BorderLayout.EAST);

        return bar;
    }

    // ── Populate dept filter dynamically from DB data ─────────────────────────
    private void populateDeptFilter() {
        deptFilter.removeAllItems();
        deptFilter.addItem("All Departments");
        allEmployees.stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .forEach(deptFilter::addItem);
    }

    // ── Populate table ────────────────────────────────────────────────────────
    private void populateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                e.getEmployeeId(),
                fullName(e),           // getFirstName() + " " + getLastName()
                e.getDepartment(),
                e.getPosition(),
                e.getEmail(),
                e.getPhone(),
                e.getStatus()
            });
        }
        updateCount(employees.size());
    }

    // ── Filter logic ──────────────────────────────────────────────────────────
    private void applyFilters() {
        String query    = searchField.getText().trim().toLowerCase();
        String dept     = (String) deptFilter.getSelectedItem();
        boolean allDept = "All Departments".equals(dept);

        List<Employee> filtered = new ArrayList<>();
        for (Employee e : allEmployees) {
            boolean matchDept   = allDept || e.getDepartment().equals(dept);
            boolean matchSearch = query.isEmpty()
                    || fullName(e).toLowerCase().contains(query)
                    || String.valueOf(e.getEmployeeId()).contains(query)
                    || e.getEmail().toLowerCase().contains(query)
                    || e.getPosition().toLowerCase().contains(query)
                    || e.getFirstName().toLowerCase().contains(query)
                    || e.getLastName().toLowerCase().contains(query);

            if (matchDept && matchSearch) filtered.add(e);
        }

        populateTable(filtered);

        String deptStr  = allDept ? "all departments" : dept;
        String queryStr = query.isEmpty() ? "" : " matching \"" + query + "\"";
        statusBar.setText("Showing " + filtered.size() + " employee(s) in " + deptStr + queryStr + ".");
    }

    // ── Refresh from DB ───────────────────────────────────────────────────────
    private void handleRefresh() {
        loadFromDB();
        populateDeptFilter();
        searchField.setText("");
        deptFilter.setSelectedIndex(0);
        populateTable(allEmployees);
        setStatus("List refreshed. Showing all " + allEmployees.size() + " employees.", ACCENT);
    }

    private void updateCount(int count) {
        countLabel.setText(count + " employee" + (count == 1 ? "" : "s"));
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            final Color normal = bg;
            final Color hover  = bg.darker();
            @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(normal); }
        });
        return btn;
    }

    private JLabel searchIcon() {
        JLabel lbl = new JLabel("🔍");
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        return lbl;
    }
}