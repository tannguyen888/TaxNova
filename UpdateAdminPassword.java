import java.sql.*;

public class UpdateAdminPassword {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/taxService";
        String user = "postgres";
        String password = "lab";
        String correctHash = "JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, correctHash);
                stmt.setString(2, "admin");
                int rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);
                System.out.println("✓ Admin password hash updated successfully!");
            }
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
