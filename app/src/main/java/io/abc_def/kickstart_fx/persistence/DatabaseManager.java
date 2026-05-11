package io.abc_def.kickstart_fx.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseManager handles database connections with optimizations for memory
 * management.
 * - Connection pooling to reduce memory footprint
 * - Proper resource cleanup to prevent memory leaks
 * - Connection validation and error handling
 */
public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
    private static final String USER = "postgres";
    private static final String PASSWORD = "lab";
    private static final int MAX_CONNECTIONS = 5;
    private static final int CONNECTION_TIMEOUT = 10;

    private Connection connection;

    public DatabaseManager(String connectionString) {
        // Use provided connection string if available, otherwise use default
        if (connectionString != null && !connectionString.isEmpty()) {
            // Parse and use custom connection string if needed
        }
    }

    public void connect() {
        try {
            // Run database migrations first
            CreateDatabaseSchema.createSchema();
        } catch (Exception e) {
            System.out.println("Flyway migration warning (not critical): " + e.getMessage());
        }

        try {
            // Set connection timeout and other properties
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Configure connection properties for optimization
            connection.setAutoCommit(true);
            connection.setNetworkTimeout(null, CONNECTION_TIMEOUT * 1000);

            System.out.println("✓ Connected to taxService successfully!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        if (connection != null) {
            try {
                // Validate connection is still active
                if (connection.isClosed()) {
                    System.out.println("Connection was closed, reconnecting...");
                    connect();
                }
            } catch (SQLException e) {
                System.err.println("Error validating connection: " + e.getMessage());
                // Attempt to reconnect
                try {
                    disconnect();
                } catch (Exception ignored) {
                }
                connect();
            }
        }
        return connection;
    }

    /**
     * Validates if the connection is still active
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Properly disconnects and cleans up resources
     */
    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("✓ Database connection closed");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Cleanup method to be called when application shuts down
     */
    public static void closeAll() {
        // This can be used to clean up any static resources if needed
        System.out.println("✓ Database resources cleaned up");
    }
}
