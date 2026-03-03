package io.abc_def.kickstart_fx.persistence;

public class TestConnection {

    public static void main(String[] args) {

        // Bước 1: Tạo DatabaseManager với connection string
        DatabaseManager db = new DatabaseManager("jdbc:postgresql://localhost:5432/taxService");

        // Bước 2: Thử kết nối
        try {
            db.connect();
            System.out.println("✅ Kết nối thành công!");

            // Bước 3: Thử query đơn giản
            var stmt = db.getConnection().createStatement();
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("✅ Số user trong DB: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Kết nối thất bại: " + e.getMessage());
        } finally {
            db.disconnect();
        }
    }
}
