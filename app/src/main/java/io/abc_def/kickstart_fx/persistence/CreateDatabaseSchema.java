package io.abc_def.kickstart_fx.persistence;

import java.sql.*;

/**
 * Fallback schema creation when Flyway migration fails
 */
public class CreateDatabaseSchema {

    private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
    private static final String USER = "postgres";
    private static final String PASSWORD = "lab";
    private static final String CORRECT_ADMIN_PASSWORD_HASH = "JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=";

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

            // Insert default admin user with correct password hash
            String insertAdmin = "INSERT INTO users (username, password_hash, role)\n"
                    + "VALUES ('admin', '" + CORRECT_ADMIN_PASSWORD_HASH + "', 'ADMIN')\n"
                    + "ON CONFLICT (username) DO NOTHING;";

            stmt.execute(createUsersTable);
            stmt.execute(createReceiptsTable);
            stmt.execute(insertAdmin);

            System.out.println("✓ Database schema created successfully!");

            // Update admin password if it's incorrect (from old migration)
            updateAdminPasswordIfNeeded(conn);
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                System.out.println("Schema creation note: " + e.getMessage());
            }
        }
    }

    private static void updateAdminPasswordIfNeeded(Connection conn) {
        try {
            String query = "SELECT password_hash FROM users WHERE username = 'admin'";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    String currentHash = rs.getString("password_hash");
                    if (!CORRECT_ADMIN_PASSWORD_HASH.equals(currentHash)) {
                        System.out.println("⚠ Admin password hash mismatch detected. Updating...");
                        String updateQuery = "UPDATE users SET password_hash = ? WHERE username = 'admin'";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, CORRECT_ADMIN_PASSWORD_HASH);
                            int rowsUpdated = updateStmt.executeUpdate();
                            System.out.println("✓ Admin password hash updated: " + rowsUpdated + " row(s)");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Note while updating admin password: " + e.getMessage());
        }
    }
}
