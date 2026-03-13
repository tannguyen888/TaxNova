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
                        Math.round(rs.getDouble("amount")),
                        Math.round(rs.getDouble("tax_amount")),
                        rs.getString("category")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách receipt: " + e.getMessage(), e);
        }
        return list;
    }

    public void save(Receipt receipt) {
        // Check if receipt already exists in database
        String checkSql = "SELECT COUNT(*) FROM receipts WHERE id = ?";
        try (PreparedStatement checkStmt = databaseManager.getConnection().prepareStatement(checkSql)) {
            checkStmt.setLong(1, receipt.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // UPDATE existing record
                String updateSql =
                        "UPDATE receipts SET date = ?, amount = ?, tax_amount = ?, category = ? WHERE id = ?";
                try (PreparedStatement updateStmt =
                        databaseManager.getConnection().prepareStatement(updateSql)) {
                    updateStmt.setDate(1, Date.valueOf(receipt.getDate()));
                    updateStmt.setDouble(2, receipt.getAmount());
                    updateStmt.setDouble(3, receipt.getTaxAmount());
                    updateStmt.setString(4, receipt.getCategory());
                    updateStmt.setLong(5, receipt.getId());
                    updateStmt.executeUpdate();
                    System.out.println("DEBUG: Updated receipt ID " + receipt.getId());
                }
            } else {
                // INSERT new record
                String insertSql =
                        "INSERT INTO receipts (id, date, amount, tax_amount, category) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt =
                        databaseManager.getConnection().prepareStatement(insertSql)) {
                    insertStmt.setLong(1, receipt.getId());
                    insertStmt.setDate(2, Date.valueOf(receipt.getDate()));
                    insertStmt.setDouble(3, receipt.getAmount());
                    insertStmt.setDouble(4, receipt.getTaxAmount());
                    insertStmt.setString(5, receipt.getCategory());
                    insertStmt.executeUpdate();
                    System.out.println("DEBUG: Inserted receipt ID " + receipt.getId());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu receipt: " + e.getMessage(), e);
        }
    }

    public void delete(Receipt receipt) {
        String sql = "DELETE FROM receipts WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, receipt.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa receipt: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM receipts WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa receipt theo ID: " + e.getMessage(), e);
        }
    }

    public void update(Receipt receipt) {
        String sql = "UPDATE receipts SET date = ?, amount = ?, tax_amount = ?, category = ? WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(receipt.getDate()));
            stmt.setLong(2, receipt.getRevenue());
            stmt.setLong(3, receipt.getTax());
            stmt.setString(4, receipt.getDescription());
            stmt.setLong(5, receipt.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật receipt: " + e.getMessage(), e);
        }
    }

    public Receipt findById(Long id) {
        String sql = "SELECT * FROM receipts WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Receipt(
                        rs.getLong("id"),
                        rs.getDate("date").toLocalDate(),
                        (long) rs.getDouble("amount"),
                        (long) rs.getDouble("tax_amount"),
                        rs.getString("category"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm receipt theo ID: " + e.getMessage(), e);
        }
        return null;
    }
}
