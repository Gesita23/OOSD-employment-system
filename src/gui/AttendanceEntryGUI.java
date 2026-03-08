package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class AttendanceEntryGUI {

    // ── Mock Employee ────────────────────────────────────────────────────────
    static class Employee {
        final int    id;
        final String name, department;
        Employee(int id, String name, String department) {
            this.id = id; this.name = name; this.department = department;
        }
        @Override public String toString() { return id + "  —  " + name; }
    }

    // ── Attendance Record ────────────────────────────────────────────────────
    static class AttendanceRecord {
        final int    employeeId;
        final String employeeName, date, status, notes;
        AttendanceRecord(int employeeId, String employeeName,
                         String date, String status, String notes) {
            this.employeeId   = employeeId;
            this.employeeName = employeeName;
            this.date         = date;
            this.status       = status;
            this.notes        = notes;
        }
    }

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG         = new Color(0x0F1923);
    private static final Color SURFACE    = new Color(0x182534);
    private static final Color CARD       = new Color(0x1E2F42);
    private static final Color BORDER_CLR = new Color(0x2A3F58);
    private static final Color PRIMARY    = new Color(0x0EA5E9);
    private static final Color SUCCESS    = new Color(0x15803D);   // darker green for Present
    private static final Color LATE       = new Color(0xF59E0B);
    private static final Color ABSENT     = new Color(0xEF4444);
    private static final Color LEAVE      = new Color(0xA78BFA);
    private static final Color TEXT_MAIN  = new Color(0xF0F6FF);
    private static final Color TEXT_MUTED = new Color(0x6B8CAE);
    private static final Color FIELD_BG   = new Color(0x152030);
    private static final Color CAL_TODAY  = new Color(0x0EA5E9);
    private static final Color CAL_SEL    = new Color(0x0284C7);
    private static final Color CAL_HOVER  = new Color(0x1E3A52);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_STATUS  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Data ─────────────────────────────────────────────────────────────────
    private final List<Employee>         employees = new ArrayList<>();
    private final List<AttendanceRecord> records   = new ArrayList<>();

    // ── UI state ─────────────────────────────────────────────────────────────
    private JFrame              frame;
    private JComboBox<Employee> empCombo;
    private JLabel              empIdLabel, empNameLabel, empDeptLabel;
    private String              selectedStatus = "Present";
    private JButton[]           statusBtns;
    private JTextArea           notesArea;
    private JLabel              statusBar, summaryLabel;

    // Calendar state
    private Calendar  calSelected;
    private JPanel    calGrid;
    private JLabel    calMonthLabel;

    private static final String[] STATUSES  = {"Present", "Late", "Absent", "On Leave"};
    private static final String[] DAY_NAMES = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};

    // ── Entry ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new AttendanceEntryGUI().buildUI());
    }

    private void seedData() {
        employees.add(new Employee(1001, "Alice Smith",    "Engineering"));
        employees.add(new Employee(1002, "Bob Johnson",    "Sales"));
        employees.add(new Employee(1003, "Carol Williams", "HR"));
        employees.add(new Employee(1004, "David Lee",      "Finance"));
        employees.add(new Employee(1005, "Eva Martinez",   "Engineering"));
        employees.add(new Employee(1006, "Frank Chen",     "Engineering"));
        employees.add(new Employee(1007, "Grace Kim",      "Sales"));
        employees.add(new Employee(1008, "Henry Davis",    "Finance"));
    }

    // ── Build UI ─────────────────────────────────────────────────────────────
    public void buildUI() {
        seedData();
        calSelected = Calendar.getInstance();

        frame = new JFrame("Payroll Management  ·  Attendance Entry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 780);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        frame.setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        root.add(buildStatusBar(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(20, 28, 20, 28)));

        JLabel title = new JLabel("Attendance Entry");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Mark and save daily attendance records for employees.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        summaryLabel = new JLabel("0 records saved this session");
        summaryLabel.setFont(FONT_SMALL);
        summaryLabel.setForeground(PRIMARY);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        header.add(left,         BorderLayout.CENTER);
        header.add(summaryLabel, BorderLayout.EAST);
        return header;
    }

    // ── Form ─────────────────────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        outer.add(buildEmployeeSection());
        outer.add(spacer(14));
        outer.add(buildEmployeeCard());
        outer.add(spacer(20));
        outer.add(buildCalendarSection());
        outer.add(spacer(20));
        outer.add(buildStatusSection());
        outer.add(spacer(20));
        outer.add(buildNotesSection());
        outer.add(spacer(24));
        outer.add(buildButtons());

        return outer;
    }

    // ── Employee selector ────────────────────────────────────────────────────
    private JPanel buildEmployeeSection() {
        JPanel section = section();
        section.add(sectionLabel("SELECT EMPLOYEE"));
        section.add(spacer(8));

        empCombo = new JComboBox<>(employees.toArray(new Employee[0]));
        empCombo.setFont(FONT_FIELD);
        empCombo.setBackground(FIELD_BG);
        empCombo.setForeground(TEXT_MAIN);
        empCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        empCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        empCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        empCombo.addActionListener(e -> refreshEmployeeCard());

        section.add(empCombo);
        return section;
    }

    // ── Employee info card ───────────────────────────────────────────────────
    private JPanel buildEmployeeCard() {
        JPanel card = new JPanel(new GridLayout(1, 3, 1, 0));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(14, 18, 14, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        empIdLabel   = new JLabel();
        empNameLabel = new JLabel();
        empDeptLabel = new JLabel();

        card.add(empIdLabel);
        card.add(empNameLabel);
        card.add(empDeptLabel);

        refreshEmployeeCard();
        return card;
    }

    private void refreshEmployeeCard() {
        Employee emp = (Employee) empCombo.getSelectedItem();
        if (emp == null) return;
        empIdLabel.setText(
            "<html><span style='font-size:9px;color:#6B8CAE;'>EMPLOYEE ID</span>"
          + "<br><b style='font-size:13px;color:#F0F6FF;'>#" + emp.id + "</b></html>");
        empNameLabel.setText(
            "<html><span style='font-size:9px;color:#6B8CAE;'>FULL NAME</span>"
          + "<br><b style='font-size:13px;color:#F0F6FF;'>" + emp.name + "</b></html>");
        empDeptLabel.setText(
            "<html><span style='font-size:9px;color:#6B8CAE;'>DEPARTMENT</span>"
          + "<br><b style='font-size:13px;color:#0EA5E9;'>" + emp.department + "</b></html>");
    }

    // ── Calendar ─────────────────────────────────────────────────────────────
    private JPanel buildCalendarSection() {
        JPanel section = section();
        section.add(sectionLabel("SELECT DATE"));
        section.add(spacer(8));
        section.add(buildCalendarWidget());
        return section;
    }

    private JPanel buildCalendarWidget() {
        JPanel widget = new JPanel(new BorderLayout(0, 8));
        widget.setBackground(CARD);
        widget.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        widget.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        widget.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nav row
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);

        JButton prevBtn = calNavBtn("‹");
        JButton nextBtn = calNavBtn("›");
        calMonthLabel = new JLabel("", SwingConstants.CENTER);
        calMonthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        calMonthLabel.setForeground(TEXT_MAIN);

        prevBtn.addActionListener(e -> { calSelected.add(Calendar.MONTH, -1); refreshCalendar(); });
        nextBtn.addActionListener(e -> { calSelected.add(Calendar.MONTH,  1); refreshCalendar(); });

        nav.add(prevBtn,       BorderLayout.WEST);
        nav.add(calMonthLabel, BorderLayout.CENTER);
        nav.add(nextBtn,       BorderLayout.EAST);

        // Day-name headers
        JPanel dayHeaders = new JPanel(new GridLayout(1, 7, 4, 0));
        dayHeaders.setOpaque(false);
        for (String d : DAY_NAMES) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT_MUTED);
            dayHeaders.add(lbl);
        }

        calGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        calGrid.setOpaque(false);

        JPanel topPart = new JPanel(new BorderLayout(0, 6));
        topPart.setOpaque(false);
        topPart.add(nav,        BorderLayout.NORTH);
        topPart.add(dayHeaders, BorderLayout.SOUTH);

        widget.add(topPart, BorderLayout.NORTH);
        widget.add(calGrid, BorderLayout.CENTER);

        refreshCalendar();
        return widget;
    }

    private void refreshCalendar() {
        calMonthLabel.setText(new SimpleDateFormat("MMMM yyyy").format(calSelected.getTime()));
        calGrid.removeAll();

        Calendar today   = Calendar.getInstance();
        Calendar display = (Calendar) calSelected.clone();
        display.set(Calendar.DAY_OF_MONTH, 1);

        int firstDow    = display.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = display.getActualMaximum(Calendar.DAY_OF_MONTH);
        int selDay      = calSelected.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDow; i++) calGrid.add(new JLabel(""));

        for (int day = 1; day <= daysInMonth; day++) {
            final int d = day;
            boolean isToday = today.get(Calendar.YEAR)         == calSelected.get(Calendar.YEAR)
                           && today.get(Calendar.MONTH)        == calSelected.get(Calendar.MONTH)
                           && today.get(Calendar.DAY_OF_MONTH) == day;
            boolean isSel   = selDay == day;

            JButton btn = new JButton(String.valueOf(day));
            btn.setFont(new Font("Segoe UI", isSel ? Font.BOLD : Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (isSel) {
                btn.setBackground(CAL_SEL);
                btn.setForeground(Color.WHITE);
            } else if (isToday) {
                btn.setBackground(CAL_TODAY);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(CARD);
                btn.setForeground(TEXT_MAIN);
            }

            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (calSelected.get(Calendar.DAY_OF_MONTH) != d) btn.setBackground(CAL_HOVER);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if      (calSelected.get(Calendar.DAY_OF_MONTH) == d) btn.setBackground(CAL_SEL);
                    else if (isToday) btn.setBackground(CAL_TODAY);
                    else              btn.setBackground(CARD);
                }
            });

            btn.addActionListener(e -> {
                calSelected.set(Calendar.DAY_OF_MONTH, d);
                refreshCalendar();
                setStatus("Date selected: "
                        + new SimpleDateFormat("EEEE, dd MMMM yyyy").format(calSelected.getTime()),
                        TEXT_MUTED);
            });

            calGrid.add(btn);
        }

        int total     = firstDow + daysInMonth;
        int remainder = (total % 7 == 0) ? 0 : 7 - (total % 7);
        for (int i = 0; i < remainder; i++) calGrid.add(new JLabel(""));

        calGrid.revalidate();
        calGrid.repaint();
    }

    private JButton calNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(PRIMARY); }
            @Override public void mouseExited (MouseEvent e) { btn.setForeground(TEXT_MUTED); }
        });
        return btn;
    }

    // ── Status buttons ───────────────────────────────────────────────────────
    private JPanel buildStatusSection() {
        JPanel section = section();
        section.add(sectionLabel("ATTENDANCE STATUS"));
        section.add(spacer(10));

        Color[]  colors = {SUCCESS, LATE, ABSENT, LEAVE};
        String[] icons  = {"✓", "◷", "✗", "◈"};

        JPanel btnRow = new JPanel(new GridLayout(1, 4, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusBtns = new JButton[STATUSES.length];

        for (int i = 0; i < STATUSES.length; i++) {
            final int   idx = i;
            final Color clr = colors[i];

            JButton btn = new JButton(
                    "<html><center>" + icons[i] + "<br>" + STATUSES[i] + "</center></html>");
            btn.setFont(FONT_STATUS);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            styleStatusBtn(btn, STATUSES[i].equals(selectedStatus), clr);

            btn.addActionListener(e -> {
                selectedStatus = STATUSES[idx];
                for (int j = 0; j < STATUSES.length; j++)
                    styleStatusBtn(statusBtns[j], j == idx, colors[j]);
            });

            statusBtns[i] = btn;
            btnRow.add(btn);
        }

        section.add(btnRow);
        return section;
    }

    private void styleStatusBtn(JButton btn, boolean selected, Color clr) {
        if (selected) {
            btn.setBackground(clr);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(clr, 2, true),
                    new EmptyBorder(8, 4, 8, 4)));
        } else {
            btn.setBackground(CARD);
            btn.setForeground(clr);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
        }
    }

    // ── Notes ────────────────────────────────────────────────────────────────
    private JPanel buildNotesSection() {
        JPanel section = section();
        section.add(sectionLabel("NOTES  (optional)"));
        section.add(spacer(8));

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(FONT_FIELD);
        notesArea.setBackground(FIELD_BG);
        notesArea.setForeground(TEXT_MAIN);
        notesArea.setCaretColor(PRIMARY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.getViewport().setBackground(FIELD_BG);

        section.add(scroll);
        return section;
    }

    // ── Action buttons ───────────────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton clearBtn = styledButton("Clear", CARD, TEXT_MUTED, BORDER_CLR);
        clearBtn.addActionListener(e -> handleClear());

        JButton saveBtn = styledButton("Save Attendance Record", PRIMARY, Color.WHITE, PRIMARY);
        saveBtn.addActionListener(e -> handleSave());

        row.add(clearBtn, BorderLayout.WEST);
        row.add(saveBtn,  BorderLayout.EAST);
        return row;
    }

    // ── Status bar ───────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(7, 20, 7, 20)));

        statusBar = new JLabel("Ready to record attendance.");
        statusBar.setFont(FONT_SMALL);
        statusBar.setForeground(TEXT_MUTED);

        JLabel hint = new JLabel("Data is mock — will be linked to MySQL.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(0x2A3F58));

        bar.add(statusBar, BorderLayout.WEST);
        bar.add(hint,      BorderLayout.EAST);
        return bar;
    }

    // ── Handlers ─────────────────────────────────────────────────────────────
    private void handleSave() {
        Employee emp = (Employee) empCombo.getSelectedItem();
        if (emp == null) { setStatus("Please select an employee.", ABSENT); return; }

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(calSelected.getTime());
        String dateFmt = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(calSelected.getTime());
        String notes   = notesArea.getText().trim();

        // TODO: attendanceDAO.save(emp.id, dateStr, selectedStatus, notes);
        records.add(new AttendanceRecord(emp.id, emp.name, dateStr, selectedStatus, notes));

        String msg = "<html><b>Attendance recorded successfully.</b><br><br>"
                   + "Employee : " + emp.name + " (ID: " + emp.id + ")<br>"
                   + "Date     : " + dateFmt + "<br>"
                   + "Status   : " + selectedStatus + "<br>"
                   + (notes.isEmpty() ? "" : "Notes    : " + notes) + "</html>";

        JOptionPane.showMessageDialog(frame, msg, "Record Saved", JOptionPane.INFORMATION_MESSAGE);

        summaryLabel.setText(records.size() + " record"
                + (records.size() == 1 ? "" : "s") + " saved this session");
        setStatus("Saved: " + emp.name + " marked " + selectedStatus + " on " + dateStr + ".", SUCCESS);
        handleClear();
    }

    private void handleClear() {
        empCombo.setSelectedIndex(0);
        refreshEmployeeCard();
        calSelected = Calendar.getInstance();
        refreshCalendar();
        selectedStatus = "Present";
        Color[] colors = {SUCCESS, LATE, ABSENT, LEAVE};
        for (int i = 0; i < statusBtns.length; i++)
            styleStatusBtn(statusBtns[i], i == 0, colors[i]);
        notesArea.setText("");
        setStatus("Form cleared. Ready for next entry.", TEXT_MUTED);
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JPanel section() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private Component spacer(int h) { return Box.createVerticalStrut(h); }

    private JButton styledButton(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1, true),
                new EmptyBorder(10, 22, 10, 22)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            final Color normal = bg;
            final Color hover  = bg.darker();
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(normal); }
        });
        return btn;
    }
}