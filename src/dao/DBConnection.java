package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "54321";
    private static final String DEFAULT_DB   = "hat_db";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASS = "PASSword321";

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            String host = getEnv("HAT_DB_HOST", DEFAULT_HOST);
            String port = getEnv("HAT_DB_PORT", DEFAULT_PORT);
            String db   = getEnv("HAT_DB_NAME",  DEFAULT_DB);
            String user = getEnv("HAT_DB_USER",  DEFAULT_USER);
            String pass = getEnv("HAT_DB_PASS",  DEFAULT_PASS);

            String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("[DBConnection] Connected to " + url);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "PostgreSQL JDBC driver not found. " +
                "Ensure postgresql-42.7.11.jar is on the classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to connect to the HAT database. " +
                "Check your DB credentials and that PostgreSQL is running.", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public static Connection getConnection() {
        DBConnection db = getInstance();
        try {
            if (db.connection == null || db.connection.isClosed()) {
                System.out.println("[DBConnection] Reconnecting…");
                instance = null;
                return getConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not verify connection state.", e);
        }
        return db.connection;
    }

    public static synchronized void shutdown() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException ignored) {}
            instance = null;
        }
    }
    
    private static String getEnv(String key, String fallback) {
        String val = System.getenv(key);
        return (val != null && !val.isBlank()) ? val : fallback;
    }
}