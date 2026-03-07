package dao;

import database.DBConnection;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee CRUD operations.
 */
public class EmployeeDAO {

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO employees (first_name, last_name, email, phone, " +
                     "department, position, salary, hire_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getDepartment());
            ps.setString(6, emp.getPosition());
            ps.setDouble(7, emp.getSalary());
            ps.setString(8, emp.getHireDate());
            ps.setString(9, emp.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addEmployee error: " + e.getMessage());
            return false;
        }
    }

    // ── READ (all) ────────────────────────────────────────────────────────────
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllEmployees error: " + e.getMessage());
        }
        return list;
    }

    // ── READ (by id) ──────────────────────────────────────────────────────────
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("getEmployeeById error: " + e.getMessage());
        }
        return null;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET first_name=?, last_name=?, email=?, phone=?, " +
                     "department=?, position=?, salary=?, hire_date=?, status=? " +
                     "WHERE employee_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getDepartment());
            ps.setString(6, emp.getPosition());
            ps.setDouble(7, emp.getSalary());
            ps.setString(8, emp.getHireDate());
            ps.setString(9, emp.getStatus());
            ps.setInt(10, emp.getEmployeeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateEmployee error: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteEmployee error: " + e.getMessage());
            return false;
        }
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    public List<Employee> searchEmployees(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE first_name LIKE ? OR last_name LIKE ? OR department LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("searchEmployees error: " + e.getMessage());
        }
        return list;
    }

    // ── COUNT (for dashboard) ─────────────────────────────────────────────────
    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTotalEmployees error: " + e.getMessage());
        }
        return 0;
    }

    public int getActiveEmployees() {
        String sql = "SELECT COUNT(*) FROM employees WHERE status = 'Active'";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getActiveEmployees error: " + e.getMessage());
        }
        return 0;
    }

    // ── REGISTER NEW USER ─────────────────────────────────────────────────────
    public boolean registerUser(String username, String password,
                                String fullName, String email, String role) {
        // Check if username already exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false; // username taken
        } catch (SQLException e) {
            System.err.println("registerUser check error: " + e.getMessage());
            return false;
        }

        // Insert new user
        String sql = "INSERT INTO users (username, password, full_name, email, role) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("registerUser error: " + e.getMessage());
            return false;
        }
    }

    // ── VALIDATE LOGIN ────────────────────────────────────────────────────────
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("validateLogin error: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("employee_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("department"),
            rs.getString("position"),
            rs.getDouble("salary"),
            rs.getString("hire_date"),
            rs.getString("status")
        );
    }
}