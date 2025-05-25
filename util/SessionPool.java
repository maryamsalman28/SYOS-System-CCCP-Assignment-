package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SessionPool {

    private static final int MAX_CONNECTIONS = 10;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
    private static SessionPool instance;

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=CCCPSYOS;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa123";

    private SessionPool() {
        try {
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                connectionPool.offer(conn);
            }
            System.out.println("✅ SessionPool initialized with " + MAX_CONNECTIONS + " connections.");
        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to initialize session pool: " + e.getMessage(), e);
        }
    }

    public static synchronized SessionPool getInstance() {
        if (instance == null) {
            instance = new SessionPool();
        }
        return instance;
    }

    public Connection borrowConnection() throws InterruptedException {
        Connection conn = connectionPool.take(); // blocks if pool is empty
        System.out.println("🔓 Borrowed DB connection (Remaining: " + connectionPool.size() + ")");
        return conn;
    }

    public void returnConnection(Connection conn) {
        if (conn != null) {
            connectionPool.offer(conn); // returns it to the pool
            System.out.println("🔒 Returned DB connection (Available: " + connectionPool.size() + ")");
        }
    }

    public void shutdown() {
        System.out.println("🛑 Shutting down SessionPool...");
        int closedCount = 0;

        while (!connectionPool.isEmpty()) {
            try {
                Connection conn = connectionPool.poll();
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    closedCount++;
                }
            } catch (SQLException e) {
                System.err.println("❌ Error closing DB connection: " + e.getMessage());
            }
        }

        System.out.println("✅ Closed " + closedCount + " connections. Shutdown complete.");
    }

}
