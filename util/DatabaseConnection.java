package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // ✅ SQL Authentication — no DLL needed
    private static final String DB_URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=CCCPSYOS;" +
                    "user=sa;" +  // 👈 change this
                    "password=sa123;" +  // 👈 change this
                    "encrypt=true;" +
                    "trustServerCertificate=true;";

    private DatabaseConnection() throws SQLException {
        reconnect();
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔁 Re-establishing database connection...");
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to re-establish connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    private void reconnect() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ JDBC driver loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC driver not found.");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Database connection successful.");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw e;
        }
    }
}
