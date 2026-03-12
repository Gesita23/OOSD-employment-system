package gui;

import dao.EmployeeDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import model.Employee;

public class DashboardPage extends JFrame {

    private JLabel lblTotal, lblActive, lblInactive, lblDepts;
    private final EmployeeDAO dao = new EmployeeDAO();

    public DashboardPage() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Employee Management System – Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem miLogout = new JMenuItem("Logout");
        JMenuItem miExit   = new JMenuItem("Exit");
        menuFile.add(miLogout); menuFile.addSeparator(); menuFile.add(miExit);

        JMenu menuEmployee = new JMenu("Employee");
        JMenuItem miAdd    = new JMenuItem("Add Employee");
        JMenuItem miUpdate = new JMenuItem("Update Employee");
        JMenuItem miView   = new JMenuItem("View Employees");
        JMenuItem miDelete = new JMenuItem("Delete Employee");
        menuEmployee.add(miAdd); menuEmployee.add(miUpdate);
        menuEmployee.add(miView); menuEmployee.add(miDelete);

        JMenu menuAttendance = new JMenu("Attendance");
        JMenuItem miAttendance = new JMenuItem("Attendance Entry");
        menuAttendance.add(miAttendance);

        JMenu menuPayroll = new JMenu("Payroll");
        JMenuItem miPayroll = new JMenuItem("Payroll Management");
        menuPayroll.add(miPayroll);

        JMenu menuHelp = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem("About");
        menuHelp.add(miAbout);

        menuBar.add(menuFile); menuBar.add(menuEmployee);
        menuBar.add(menuAttendance); menuBar.add(menuPayroll); menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(30, 60, 114));
        navBar.setPreferredSize(new Dimension(0, 58));
        navBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel navTitle = new JLabel("📊  Dashboard");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        navTitle.setForeground(Color.WHITE);
        navBar.add(navTitle, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        navButtons.setOpaque(false);

        JButton btnAddNav        = makeNavBtn("➕ Add Employee",   new Color(60, 100, 180));
        JButton btnUpdateNav     = makeNavBtn("✏️ Update",         new Color(60, 100, 180));
        JButton btnViewNav       = makeNavBtn("👥 View Employees", new Color(60, 100, 180));
        JButton btnDeleteNav     = makeNavBtn("🗑 Delete",         new Color(60, 100, 180));
        JButton btnAttendanceNav = makeNavBtn("📋 Attendance",     new Color(60, 100, 180));
        JButton btnPayrollNav    = makeNavBtn("💰 Payroll",        new Color(60, 100, 180));
        JButton btnLogoutNav     = makeNavBtn("🚪 Logout",         new Color(160, 40, 40));

        for (JButton b : new JButton[]{btnAddNav,btnUpdateNav,btnViewNav,btnDeleteNav,btnAttendanceNav,btnPayrollNav,btnLogoutNav})
            navButtons.add(b);
        navBar.add(navButtons, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBackground(new Color(235, 240, 252));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        lblTotal    = new JLabel("0", SwingConstants.LEFT);
        lblActive   = new JLabel("0", SwingConstants.LEFT);
        lblInactive = new JLabel("0", SwingConstants.LEFT);
        lblDepts    = new JLabel("0", SwingConstants.LEFT);
        statsRow.add(makeStatCard("👥  Total Employees",  lblTotal,    new Color(30,60,114),   new Color(60,100,180)));
        statsRow.add(makeStatCard("✅  Active",           lblActive,   new Color(34,139,87),   new Color(50,180,110)));
        statsRow.add(makeStatCard("⏸  Inactive / Leave", lblInactive, new Color(180,90,20),   new Color(220,130,40)));
        statsRow.add(makeStatCard("🏢  Departments",      lblDepts,    new Color(100,40,160),  new Color(140,80,200)));

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);

        JPanel chartCard = makeCard("📊  Employee Distribution by Department");
        BarChart barChart = new BarChart(dao.getAllEmployees());
        chartCard.add(barChart, BorderLayout.CENTER);
        bottomRow.add(chartCard);

        JPanel quickCard = makeCard("⚡  Quick Actions");
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(8, 8, 8, 8));
        JButton qaAdd   = makeQuickBtn("➕  Add Employee",    new Color(34,139,87));
        JButton qaUpd   = makeQuickBtn("✏️  Update Employee", new Color(41,128,185));
        JButton qaView  = makeQuickBtn("👥  View Employees",  new Color(100,40,160));
        JButton qaDel   = makeQuickBtn("🗑  Delete Employee", new Color(192,57,43));
        JButton qaAtt   = makeQuickBtn("📋  Attendance",      new Color(39,120,130));
        JButton qaPay   = makeQuickBtn("💰  Payroll",         new Color(130,80,20));
        for (JButton b : new JButton[]{qaAdd,qaUpd,qaView,qaDel,qaAtt,qaPay}) grid.add(b);
        quickCard.add(grid, BorderLayout.CENTER);
        bottomRow.add(quickCard);

        content.add(statsRow,  BorderLayout.NORTH);
        content.add(bottomRow, BorderLayout.CENTER);
        add(navBar,   BorderLayout.NORTH);
        add(content,  BorderLayout.CENTER);

        loadStats();

        btnAddNav.addActionListener(e        -> openAddEmployee());
        miAdd.addActionListener(e            -> openAddEmployee());
        btnUpdateNav.addActionListener(e     -> openUpdateEmployee());
        miUpdate.addActionListener(e         -> openUpdateEmployee());
        btnViewNav.addActionListener(e       -> new ViewEmployeesGUI().buildUI());
        miView.addActionListener(e           -> new ViewEmployeesGUI().buildUI());
        btnDeleteNav.addActionListener(e     -> new DeleteEmployees().buildUI());
        miDelete.addActionListener(e         -> new DeleteEmployees().buildUI());
        btnAttendanceNav.addActionListener(e -> new AttendanceEntryGUI().buildUI());
        miAttendance.addActionListener(e     -> new AttendanceEntryGUI().buildUI());
        btnPayrollNav.addActionListener(e    -> new SalaryManagementGUI().buildUI());
        miPayroll.addActionListener(e        -> new SalaryManagementGUI().buildUI());
      
        btnLogoutNav.addActionListener(e     -> logout());
        miLogout.addActionListener(e         -> logout());
        miExit.addActionListener(e           -> System.exit(0));
        miAbout.addActionListener(e          -> JOptionPane.showMessageDialog(this,
                "Employee Management System\nOOSD Assignment – SIS 2015Y","About",JOptionPane.INFORMATION_MESSAGE));

        qaAdd.addActionListener(e   -> openAddEmployee());
        qaUpd.addActionListener(e   -> openUpdateEmployee());
        qaView.addActionListener(e  -> new ViewEmployeesGUI().buildUI());
        qaDel.addActionListener(e   -> new DeleteEmployees().buildUI());
        qaAtt.addActionListener(e   -> new AttendanceEntryGUI().buildUI());
        qaPay.addActionListener(e   -> new SalaryManagementGUI().buildUI());
       
    }

    private void loadStats() {
        List<Employee> all = dao.getAllEmployees();
        int total    = all.size();
        int active   = (int) all.stream().filter(e -> "Active".equalsIgnoreCase(e.getStatus())).count();
        int inactive = total - active;
        long depts   = all.stream().map(Employee::getDepartment).distinct().count();
        lblTotal.setText(String.valueOf(total));
        lblActive.setText(String.valueOf(active));
        lblInactive.setText(String.valueOf(inactive));
        lblDepts.setText(String.valueOf(depts));
    }

    private void openAddEmployee()    { new AddEmployeePage(this).setVisible(true); }
    private void openUpdateEmployee() {
        new UpdateEmployeePage(this);
    }
    public void refresh() { loadStats(); }

    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,"Are you sure you want to logout?","Logout",JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { new LoginPage().setVisible(true); dispose(); }
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color c1, Color c2) {
        JPanel card = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20,22,20,22));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,12));
        lbl.setForeground(new Color(200,225,255));
        valueLabel.setFont(new Font("Segoe UI",Font.BOLD,44));
        valueLabel.setForeground(Color.WHITE);
        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0,10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210,220,240),1,true),
            new EmptyBorder(16,16,16,16)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,14));
        lbl.setForeground(new Color(30,60,114));
        lbl.setBorder(new EmptyBorder(0,0,8,0));
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JButton makeNavBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI",Font.PLAIN,11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    private JButton makeQuickBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI",Font.BOLD,12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(16,10,16,10));
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){btn.setBackground(bg.darker());}
            public void mouseExited(MouseEvent e) {btn.setBackground(bg);}
        });
        return btn;
    }

    class BarChart extends JPanel {
        private final Map<String,Integer> counts = new LinkedHashMap<>();
        private final Color[] COLORS = {
            new Color(30,60,114), new Color(34,139,87), new Color(41,128,185),
            new Color(192,57,43), new Color(100,40,160), new Color(39,120,130),
            new Color(180,90,20), new Color(130,80,20)
        };

        BarChart(List<Employee> employees) {
            for (Employee e : employees)
                counts.merge(e.getDepartment(), 1, Integer::sum);
            setOpaque(false);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (counts.isEmpty()) { return; }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pL=45, pR=20, pT=20, pB=50;
            int w = getWidth()-pL-pR, h = getHeight()-pT-pB;
            int max = counts.values().stream().max(Integer::compareTo).orElse(1);

            g2.setColor(new Color(230,235,245));
            for (int i=1; i<=max; i++) {
                int y = pT + h - (i*h/max);
                g2.drawLine(pL, y, pL+w, y);
            }
            g2.setColor(new Color(150,160,180));
            g2.drawLine(pL, pT, pL, pT+h);
            g2.drawLine(pL, pT+h, pL+w, pT+h);

            String[] depts = counts.keySet().toArray(new String[0]);
            int barW = Math.min(55, (w/depts.length)-8);
            int gap  = (w - barW*depts.length) / (depts.length+1);

            for (int i=0; i<depts.length; i++) {
                int cnt  = counts.get(depts[i]);
                int barH = (int)((double)cnt/max*h);
                int x    = pL + gap + i*(barW+gap);
                int y    = pT + h - barH;
                Color col = COLORS[i % COLORS.length];

                g2.setPaint(new GradientPaint(x,y,col.brighter(),x,y+barH,col.darker()));
                g2.fillRoundRect(x, y, barW, barH, 8, 8);

                g2.setColor(new Color(50,70,110));
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                String cs = String.valueOf(cnt);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(cs, x+(barW-fm.stringWidth(cs))/2, y-5);

                g2.setColor(new Color(60,80,120));
                g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
                String d = depts[i].length()>7 ? depts[i].substring(0,7)+".." : depts[i];
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(d, x+(barW-fm2.stringWidth(d))/2, pT+h+15);
            }

            g2.setColor(new Color(100,120,160));
            g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
            for (int i=0; i<=max; i++) {
                int y = pT + h - (i*h/max);
                g2.drawString(String.valueOf(i), pL-28, y+4);
            }
        }
    }
}