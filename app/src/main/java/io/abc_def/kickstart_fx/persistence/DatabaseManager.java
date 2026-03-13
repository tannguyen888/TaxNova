package io.abc_def.kickstart_fx.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
    private static final String USER = "postgres";
    private static final String PASSWORD = "lab";

    private Connection connection;

    public DatabaseManager(String connectionString) {}

    public void connect() {

        try {
            CreateDatabaseSchema.createSchema();
        } catch (Exception e) {
            System.out.println("Flyway migration warning (not critical): " + e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Connected to taxService successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
