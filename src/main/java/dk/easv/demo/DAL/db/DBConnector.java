package dk.easv.demo.DAL.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static final String PROP_FILE = "src/main/resources/database.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            initializeConnection();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeConnection() throws IOException, ClassNotFoundException {
        Properties databaseProperties = new Properties();

        // Load properties from file
        File propFile = new File(PROP_FILE);
        if (!propFile.exists()) {
            throw new IOException("Database properties file not found: " + PROP_FILE);
        }

        databaseProperties.load(Files.newInputStream(propFile.toPath()));

        // Get properties
        String server = databaseProperties.getProperty("Server");
        String database = databaseProperties.getProperty("Database");
        user = databaseProperties.getProperty("User");
        password = databaseProperties.getProperty("Password");

        if (server == null || database == null || user == null || password == null) {
            throw new IOException("Missing required database properties in " + PROP_FILE);
        }

        // Build connection URL (SQL Server format)
        url = String.format("jdbc:sqlserver://%s:1433;databaseName=%s;encrypt=true;trustServerCertificate=true;loginTimeout=5",
                server, database);

        // Load SQL Server driver
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        System.out.println("Database configuration loaded successfully");
        System.out.println("Server: " + server);
        System.out.println("Database: " + database);
        System.out.println("User: " + user);

        // Test connection
        if (testConnection()) {
            System.out.println("Database connection test: SUCCESS");
        } else {
            System.out.println("Database connection test: FAILED");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("Database not initialized. Check database configuration.");
        }

        System.out.println("Attempting to connect to database...");
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println("Database connection established successfully");
        return conn;
    }

    // Test connection method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}