package io.abc_def.kickstart_fx.persistence;

import io.abc_def.kickstart_fx.domain.User;

import java.sql.*;

public class UserRepository {

    private final DatabaseManager databaseManager;

    public UserRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn user: " + e.getMessage(), e);
        }
        return null;
    }
}
