package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DBConnection for Microsoft SQL Server Express 2022.
 *
 * Option A uses Windows Authentication (default for most Express installs).
 * Option B uses SQL Server Authentication (sa login).
 */
public class DBConnection {

    // ── Option A: Windows Authentication (try this first) ────────────────
    private static final String URL =
        "jdbc:sqlserver://GEET\\SQLEXPRESS;" +
        "databaseName=employee_management;" +
        "integratedSecurity=true;" +
        "encrypt=false;" +
        "trustServerCertificate=true;";

    // ── Option B: SQL Server Authentication ──────────────────────────────
    // Uncomment below and comment out Option A if you use sa login:
    //
    // private static final String URL =
    //     "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
    //     "databaseName=employee_management;encrypt=false;trustServerCertificate=true;";
    // private static final String USER     = "sa";
    // private static final String PASSWORD = "YourPassword123";

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL);
                System.out.println("Connected to SQL Server Express successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found. Add mssql-jdbc.jar to your project libraries.");
        } catch (SQLException e) {
            System.err.println("DB Connection failed: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing: " + e.getMessage());
        }
    }
}