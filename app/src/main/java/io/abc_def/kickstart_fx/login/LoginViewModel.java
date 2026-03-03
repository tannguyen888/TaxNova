package io.abc_def.kickstart_fx.login;

import io.abc_def.kickstart_fx.persistence.DatabaseManager;

import java.sql.*;

public class LoginViewModel {

    private final AuthService authService;
    private final DatabaseManager databaseManager;
    private String username;
    private String password;

    public LoginViewModel(AuthService authService, DatabaseManager databaseManager) {
        this.authService = authService;
        this.databaseManager = databaseManager;
    }

    private boolean checkCredential(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn: " + e.getMessage());
            return false;
        }
    }

    public boolean login() {

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Username hoặc password không được để trống!");
            return false;
        }

        return checkCredential(username, password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
