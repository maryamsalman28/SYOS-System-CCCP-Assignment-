package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:sqlserver://DESKTOP-CIJ18KU:1433;databaseName=CCCPSYOS;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private DatabaseConnection() throws SQLException {
        reconnect(); // Ensure connection is always established
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
                System.out.println("Re-establishing database connection...");
                reconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void reconnect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("Database connection established successfully.");
    }
}