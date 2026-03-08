package PayrollManagementSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class DeleteEmployees {

    // ── Mock Employee ────────────────────────────────────────────────────────
    static class Employee {
        final int    id;
        final String name, department, position, email, phone;

        Employee(int id, String name, String department,
                 String position, String email, String phone) {
            this.id         = id;
            this.name       = name;
            this.department = department;
            this.position   = position;
            this.email      = email;
            this.phone      = phone;
        }
    }

    // ── Colour palette ───────────────────────────────────────────────────────
    private static final Color BG          = new Color(0xF4F6F9);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color PRIMARY     = new Color(0x1A3C6E);   // deep navy
    private static final Color ACCENT      = new Color(0xE63946);   // alert red
    private static final Color SUCCESS     = new Color(0x2D9B6F);   // green
    private static final Color BORDER_CLR  = new Color(0xDDE3EC);
    private static final Color TEXT_MAIN   = new Color(0x1C2B3A);
    private static final Color TEXT_MUTED  = new Color(0x7A8A9C);
    private static final Color FIELD_BG    = new Color(0xF0F4FA);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_DETAIL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_VALUE   = new Font("Segoe UI", Font.BOLD,  13);

    // ── State ────────────────────────────────────────────────────────────────
    private final Map<Integer, Employee> mockDb = new HashMap<>();
    private Employee currentEmployee = null;

    // ── UI components ────────────────────────────────────────────────────────
    private JFrame     frame;
    private JTextField idField;
    private JButton    searchBtn, deleteBtn;
    private JPanel     resultCard;
    private JLabel     lblName, lblDept, lblPos, lblEmail, lblPhone, lblEmpId;
    private JLabel     statusBar;

    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new DeleteEmployees().buildUI());
    }

    // ── Seed mock data ───────────────────────────────────────────────────────
    private void seedData() {
        mockDb.put(1001, new Employee(1001, "Alice Smith",    "Engineering", "Software Engineer",  "alice@company.com",  "555-0101"));
        mockDb.put(1002, new Employee(1002, "Bob Johnson",    "Sales",       "Sales Executive",    "bob@company.com",    "555-0102"));
        mockDb.put(1003, new Employee(1003, "Carol Williams", "HR",          "HR Manager",         "carol@company.com",  "555-0103"));
        mockDb.put(1004, new Employee(1004, "David Lee",      "Finance",     "Financial Analyst",  "david@company.com",  "555-0104"));
        mockDb.put(1005, new Employee(1005, "Eva Martinez",   "Engineering", "DevOps Engineer",    "eva@company.com",    "555-0105"));
    }

    // ── Build the window ─────────────────────────────────────────────────────
    private void buildUI() {
        seedData();

        frame = new JFrame("Payroll Management  ·  Delete Employee");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBackground(BG);
        frame.setContentPane(root);

        root.add(buildHeader(),     BorderLayout.NORTH);
        root.add(buildBody(),       BorderLayout.CENTER);
        root.add(buildStatusBar(),  BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Delete Employee");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Search by Employee ID, review details, then confirm deletion.");
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

    // ── Main body ────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(24, 28, 16, 28));

        body.add(buildSearchCard());
        body.add(Box.createVerticalStrut(20));
        body.add(buildResultCard());
        body.add(Box.createVerticalStrut(20));
        body.add(buildActionRow());

        return body;
    }

    // ── Search card ──────────────────────────────────────────────────────────
    private JPanel buildSearchCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(compound(12));

        JLabel lbl = new JLabel("Employee ID");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);

        idField = new JTextField();
        idField.setFont(FONT_FIELD);
        idField.setBackground(FIELD_BG);
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        idField.setForeground(TEXT_MAIN);
        idField.addActionListener(e -> handleSearch());

        searchBtn = styledButton("Search", PRIMARY, Color.WHITE);
        searchBtn.addActionListener(e -> handleSearch());

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(lbl,     BorderLayout.NORTH);
        top.add(idField, BorderLayout.CENTER);

        card.add(top,       BorderLayout.CENTER);
        card.add(searchBtn, BorderLayout.EAST);

        // hint row
        JLabel hint = new JLabel("Try IDs: 1001, 1002, 1003, 1004, 1005");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_MUTED);

        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        wrap.add(card, BorderLayout.CENTER);
        wrap.add(hint, BorderLayout.SOUTH);
        return wrap;
    }

    // ── Result card ──────────────────────────────────────────────────────────
    private JPanel buildResultCard() {
        resultCard = card();
        resultCard.setLayout(new BorderLayout(0, 12));
        resultCard.setBorder(compound(16));
        resultCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // placeholder state
        JLabel placeholder = new JLabel("No employee loaded — enter an ID and click Search.", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        placeholder.setForeground(TEXT_MUTED);
        placeholder.setName("placeholder");
        resultCard.add(placeholder, BorderLayout.CENTER);

        return resultCard;
    }

    private void populateResultCard(Employee emp) {
        resultCard.removeAll();

        // Title row
        JLabel header = new JLabel("Employee Details");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(PRIMARY);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CLR);

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(sep,    BorderLayout.SOUTH);

        // Detail grid
        JPanel grid = new JPanel(new GridLayout(3, 4, 14, 10));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblEmpId = new JLabel(); lblName  = new JLabel();
        lblDept  = new JLabel(); lblPos   = new JLabel();
        lblEmail = new JLabel(); lblPhone = new JLabel();

        grid.add(detailBlock("Employee ID",  String.valueOf(emp.id),  lblEmpId));
        grid.add(detailBlock("Full Name",    emp.name,                lblName));
        grid.add(detailBlock("Department",   emp.department,          lblDept));
        grid.add(detailBlock("Position",     emp.position,            lblPos));
        grid.add(detailBlock("Email",        emp.email,               lblEmail));
        grid.add(detailBlock("Phone",        emp.phone,               lblPhone));

        resultCard.add(top,  BorderLayout.NORTH);
        resultCard.add(grid, BorderLayout.CENTER);
        resultCard.revalidate();
        resultCard.repaint();
    }

    private JPanel detailBlock(String label, String value, JLabel valueLabel) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);

        valueLabel.setText(value);
        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(TEXT_MAIN);

        block.add(lbl);
        block.add(Box.createVerticalStrut(2));
        block.add(valueLabel);
        return block;
    }

    private void resetResultCard() {
        resultCard.removeAll();
        JLabel placeholder = new JLabel("No employee loaded — enter an ID and click Search.", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        placeholder.setForeground(TEXT_MUTED);
        resultCard.add(placeholder, BorderLayout.CENTER);
        resultCard.revalidate();
        resultCard.repaint();
    }

    // ── Action row ───────────────────────────────────────────────────────────
    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        deleteBtn = styledButton("  Delete Employee  ", ACCENT, Color.WHITE);
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> handleDelete());
        row.add(deleteBtn);
        return row;
    }

    // ── Status bar ───────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0xEAEFF5));
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(6, 16, 6, 16)));

        statusBar = new JLabel("Ready");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(TEXT_MUTED);
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }

    // ── Handlers ─────────────────────────────────────────────────────────────
    private void handleSearch() {
        String text = idField.getText().trim();

        if (text.isEmpty()) {
            showWarning("Please enter an Employee ID.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            showError("Employee ID must be a numeric value.");
            return;
        }

        // ── Step 3: Employee search ──
        currentEmployee = mockDb.get(id);  // replace with DB call later

        if (currentEmployee != null) {
            populateResultCard(currentEmployee);
            deleteBtn.setEnabled(true);
            setStatus("Employee found: " + currentEmployee.name + ". Review details below.", TEXT_MAIN);
        } else {
            resetResultCard();
            deleteBtn.setEnabled(false);
            setStatus("No employee found with ID " + id + ".", ACCENT);
        }
    }

    private void handleDelete() {
        if (currentEmployee == null) return;

        // ── Step 5: Confirmation dialog ──
        String msg = "<html><b>Are you sure you want to delete this employee?</b><br><br>"
                   + "ID: "         + currentEmployee.id         + "<br>"
                   + "Name: "       + currentEmployee.name       + "<br>"
                   + "Department: " + currentEmployee.department + "<br><br>"
                   + "<font color='#E63946'>This action cannot be undone.</font></html>";

        int choice = JOptionPane.showConfirmDialog(
                frame, msg,
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // ── Step 6: Delete from DB (mock for now) ──
            // TODO: employeeDAO.deleteById(currentEmployee.id);
            mockDb.remove(currentEmployee.id);

            // ── Step 7: Success message ──
            JOptionPane.showMessageDialog(
                    frame,
                    "<html><b>Employee deleted successfully.</b><br>"
                  + currentEmployee.name + " (ID: " + currentEmployee.id + ") has been removed.</html>",
                    "Deleted Successfully",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reset UI
            currentEmployee = null;
            idField.setText("");
            resetResultCard();
            deleteBtn.setEnabled(false);
            setStatus("Employee deleted successfully. You may search for another.", SUCCESS);
        }
        // if NO — do nothing, stay on the screen
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        return p;
    }

    private Border compound(int pad) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(pad, pad, pad, pad));
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            final Color normal  = bg;
            final Color hover   = bg.darker();
            @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(normal); }
        });
        return btn;
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Input Required", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }
}