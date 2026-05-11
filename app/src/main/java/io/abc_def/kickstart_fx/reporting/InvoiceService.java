package io.abc_def.kickstart_fx.reporting;

import io.abc_def.kickstart_fx.domain.Cart;
import io.abc_def.kickstart_fx.domain.CartItem;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {
    private final DatabaseManager databaseManager;
    private static final double TAX_RATE = 0.1; // 10% tax

    public InvoiceService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Long createInvoiceFromCart(Cart cart, Long userId) {
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Generate invoice number
                String invoiceNumber = generateInvoiceNumber();

                // Calculate amounts
                double totalAmount = cart.getTotalPrice();
                double taxAmount = totalAmount * TAX_RATE;
                double grandTotal = totalAmount + taxAmount;

                // Insert invoice
                String invoiceSql =
                        "INSERT INTO invoices (invoice_number, user_id, cart_id, total_amount, tax_amount, created_at, status) VALUES (?, ?, ?, ?, ?, ?, 'COMPLETED')";
                long invoiceId;

                try (PreparedStatement stmt = conn.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, invoiceNumber);
                    stmt.setLong(2, userId);
                    stmt.setLong(3, cart.getId());
                    stmt.setDouble(4, totalAmount);
                    stmt.setDouble(5, taxAmount);
                    stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        invoiceId = rs.getLong(1);
                    } else {
                        throw new RuntimeException("Failed to create invoice");
                    }
                }

                // Insert invoice details
                for (CartItem item : cart.getItems()) {
                    String detailSql =
                            "INSERT INTO invoice_details (invoice_id, product_code, product_name, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                        stmt.setLong(1, invoiceId);
                        stmt.setString(2, item.getProductCode());
                        stmt.setString(3, item.getProductName());
                        stmt.setLong(4, item.getQuantity());
                        stmt.setDouble(5, item.getUnitPrice());
                        stmt.setDouble(6, item.getTotalPrice());
                        stmt.executeUpdate();
                    }
                }

                // Update cart status
                String updateCartSql = "UPDATE carts SET status = 'COMPLETED', updated_at = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateCartSql)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setLong(2, cart.getId());
                    stmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Invoice created successfully: " + invoiceNumber);
                return invoiceId;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error creating invoice: " + e.getMessage());
                throw new RuntimeException("Lỗi tạo hóa đơn: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            System.err.println("Error in invoice transaction: " + e.getMessage());
            throw new RuntimeException("Lỗi trong giao dịch hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<InvoiceData> getInvoicesByUserId(Long userId) {
        List<InvoiceData> invoices = new ArrayList<>();
        String sql =
                "SELECT id, invoice_number, total_amount, tax_amount, created_at FROM invoices WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InvoiceData invoice = new InvoiceData(
                        rs.getLong("id"),
                        rs.getString("invoice_number"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("tax_amount"),
                        rs.getTimestamp("created_at").toLocalDateTime());
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoices: " + e.getMessage());
        }

        return invoices;
    }

    public InvoiceData getInvoiceById(Long invoiceId) {
        String sql = "SELECT id, invoice_number, total_amount, tax_amount, created_at FROM invoices WHERE id = ?";

        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new InvoiceData(
                        rs.getLong("id"),
                        rs.getString("invoice_number"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("tax_amount"),
                        rs.getTimestamp("created_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoice: " + e.getMessage());
        }

        return null;
    }

    public List<InvoiceDetailData> getInvoiceDetails(Long invoiceId) {
        List<InvoiceDetailData> details = new ArrayList<>();
        String sql =
                "SELECT product_code, product_name, quantity, unit_price, total_price FROM invoice_details WHERE invoice_id = ?";

        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InvoiceDetailData detail = new InvoiceDetailData(
                        rs.getString("product_code"),
                        rs.getString("product_name"),
                        rs.getLong("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total_price"));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoice details: " + e.getMessage());
        }

        return details;
    }

    private String generateInvoiceNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        String timePart = String.valueOf(System.currentTimeMillis() % 100000);
        return "INV-" + datePart + "-" + timePart;
    }

    public static class InvoiceData {
        public Long id;
        public String invoiceNumber;
        public double totalAmount;
        public double taxAmount;
        public LocalDateTime createdAt;

        public InvoiceData(
                Long id, String invoiceNumber, double totalAmount, double taxAmount, LocalDateTime createdAt) {
            this.id = id;
            this.invoiceNumber = invoiceNumber;
            this.totalAmount = totalAmount;
            this.taxAmount = taxAmount;
            this.createdAt = createdAt;
        }

        public double getGrandTotal() {
            return totalAmount + taxAmount;
        }
    }

    public static class InvoiceDetailData {
        public String productCode;
        public String productName;
        public long quantity;
        public double unitPrice;
        public double totalPrice;

        public InvoiceDetailData(
                String productCode, String productName, long quantity, double unitPrice, double totalPrice) {
            this.productCode = productCode;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
        }
    }
}
