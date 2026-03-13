package io.abc_def.kickstart_fx.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fallback schema creation when Flyway migration fails
 */
public class CreateDatabaseSchema {

    private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
    private static final String USER = "postgres";
    private static final String PASSWORD = "lab";

    public static void createSchema() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Create users table if not exists
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                    + "    id            BIGSERIAL PRIMARY KEY,\n"
                    + "    username      VARCHAR(100) NOT NULL UNIQUE,\n"
                    + "    password_hash VARCHAR(255) NOT NULL,\n"
                    + "    role          VARCHAR(50)  NOT NULL DEFAULT 'USER'\n"
                    + ");";

            // Create receipts table if not exists
            String createReceiptsTable = "CREATE TABLE IF NOT EXISTS receipts (\n"
                    + "    id          BIGSERIAL PRIMARY KEY,\n"
                    + "    date        DATE             NOT NULL,\n"
                    + "    amount      DOUBLE PRECISION NOT NULL,\n"
                    + "    tax_amount  DOUBLE PRECISION NOT NULL,\n"
                    + "    category    VARCHAR(100)     NOT NULL\n"
                    + ");";

            // Insert default admin user
            String insertAdmin = "INSERT INTO users (username, password_hash, role)\n"
                    + "VALUES ('admin', 'admin123', 'ADMIN')\n"
                    + "ON CONFLICT (username) DO NOTHING;";

            stmt.execute(createUsersTable);
            stmt.execute(createReceiptsTable);
            stmt.execute(insertAdmin);

            System.out.println("✓ Database schema created successfully!");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                System.out.println("Schema creation note: " + e.getMessage());
            }
        }
    }
}
