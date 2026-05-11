package io.abc_def.kickstart_fx.persistence;

import io.abc_def.kickstart_fx.domain.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private final DatabaseManager databaseManager;

    public ProductRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql =
                "SELECT id, code, name, description, price, quantity, category, created_at, updated_at FROM products ORDER BY code ASC";
        try (Statement stmt = databaseManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getLong("quantity"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm: " + e.getMessage(), e);
        }
        return list;
    }

    public Product findById(Long id) {
        String sql =
                "SELECT id, code, name, description, price, quantity, category, created_at, updated_at FROM products WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getLong("quantity"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
            throw new RuntimeException("Lỗi tìm sản phẩm: " + e.getMessage(), e);
        }
        return null;
    }

    public Product findByCode(String code) {
        String sql =
                "SELECT id, code, name, description, price, quantity, category, created_at, updated_at FROM products WHERE code = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getLong("quantity"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by code: " + e.getMessage());
            throw new RuntimeException("Lỗi tìm sản phẩm theo mã: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql =
                "SELECT id, code, name, description, price, quantity, category, created_at, updated_at FROM products WHERE category = ? ORDER BY code ASC";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Product(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getLong("quantity"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            System.err.println("Error finding products by category: " + e.getMessage());
            throw new RuntimeException("Lỗi tìm sản phẩm theo loại: " + e.getMessage(), e);
        }
        return list;
    }

    public void save(Product product) {
        if (product.getId() != null && product.getId() > 0) {
            update(product);
        } else {
            insert(product);
        }
    }

    private void insert(Product product) {
        String sql =
                "INSERT INTO products (code, name, description, price, quantity, category, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt =
                databaseManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());
            stmt.setLong(5, product.getQuantity());
            stmt.setString(6, product.getCategory());
            stmt.setTimestamp(7, Timestamp.valueOf(product.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(product.getUpdatedAt()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            throw new RuntimeException("Lỗi thêm sản phẩm: " + e.getMessage(), e);
        }
    }

    private void update(Product product) {
        String sql =
                "UPDATE products SET name = ?, description = ?, price = ?, quantity = ?, category = ?, updated_at = ? WHERE code = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setLong(4, product.getQuantity());
            stmt.setString(5, product.getCategory());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(7, product.getCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật sản phẩm: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa sản phẩm: " + e.getMessage(), e);
        }
    }

    public void updateQuantity(Long productId, long quantity) {
        String sql = "UPDATE products SET quantity = quantity + ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, quantity);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật số lượng sản phẩm: " + e.getMessage(), e);
        }
    }
}
