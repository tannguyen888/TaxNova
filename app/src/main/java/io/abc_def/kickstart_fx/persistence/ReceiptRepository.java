package io.abc_def.kickstart_fx.persistence;

import io.abc_def.kickstart_fx.domain.Receipt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptRepository {

    private final DatabaseManager databaseManager;

    public ReceiptRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Receipt> findAll() {
        List<Receipt> list = new ArrayList<>();
        String sql = "SELECT * FROM receipts";
        try (Statement stmt = databaseManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Receipt(
                        rs.getLong("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getDouble("amount"),
                        rs.getDouble("tax_amount"),
                        rs.getString("category")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách receipt: " + e.getMessage(), e);
        }
        return list;
    }

    public void save(Receipt receipt) {
        String sql = "INSERT INTO receipts (date, amount, tax_amount, category) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(receipt.getDate()));
            stmt.setDouble(2, receipt.getAmount());
            stmt.setDouble(3, receipt.getTaxAmount());
            stmt.setString(4, receipt.getCategory());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu receipt: " + e.getMessage(), e);
        }
    }
}