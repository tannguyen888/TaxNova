package io.abc_def.kickstart_fx.persistence;

import org.flywaydb.core.Flyway;

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

        Flyway flyway = Flyway.configure()
                .dataSource(URL, USER, PASSWORD)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối taxService thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Kết nối thất bại: " + e.getMessage(), e);
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
