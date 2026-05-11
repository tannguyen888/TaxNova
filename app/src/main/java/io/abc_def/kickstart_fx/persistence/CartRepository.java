package io.abc_def.kickstart_fx.persistence;

import io.abc_def.kickstart_fx.domain.Cart;
import io.abc_def.kickstart_fx.domain.CartItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private final DatabaseManager databaseManager;

    public CartRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Cart findById(Long id) {
        String sql = "SELECT id, user_id, created_at, updated_at, status FROM carts WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("status"));
                cart.setItems(loadCartItems(id));
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart: " + e.getMessage());
            throw new RuntimeException("Lỗi tìm giỏ hàng: " + e.getMessage(), e);
        }
        return null;
    }

    public Cart findActiveCartByUserId(Long userId) {
        String sql =
                "SELECT id, user_id, created_at, updated_at, status FROM carts WHERE user_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("status"));
                cart.setItems(loadCartItems(rs.getLong("id")));
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding active cart: " + e.getMessage());
            throw new RuntimeException("Lỗi tìm giỏ hàng hoạt động: " + e.getMessage(), e);
        }
        return null;
    }

    private javafx.collections.ObservableList<CartItem> loadCartItems(Long cartId) {
        javafx.collections.ObservableList<CartItem> items = javafx.collections.FXCollections.observableArrayList();
        String sql =
                "SELECT id, cart_id, product_id, product_code, product_name, unit_price, quantity FROM cart_items WHERE cart_id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, cartId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new CartItem(
                        rs.getLong("id"),
                        rs.getLong("cart_id"),
                        rs.getLong("product_id"),
                        rs.getString("product_code"),
                        rs.getString("product_name"),
                        rs.getDouble("unit_price"),
                        rs.getLong("quantity")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading cart items: " + e.getMessage());
        }
        return items;
    }

    public void save(Cart cart) {
        if (cart.getId() != null && cart.getId() > 0) {
            update(cart);
        } else {
            insert(cart);
        }
        // Save cart items
        for (CartItem item : cart.getItems()) {
            saveCartItem(item);
        }
    }

    private void insert(Cart cart) {
        String sql = "INSERT INTO carts (user_id, created_at, updated_at, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt =
                databaseManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cart.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(cart.getCreatedAt()));
            stmt.setTimestamp(3, Timestamp.valueOf(cart.getUpdatedAt()));
            stmt.setString(4, cart.getStatus());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                cart.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting cart: " + e.getMessage());
            throw new RuntimeException("Lỗi thêm giỏ hàng: " + e.getMessage(), e);
        }
    }

    private void update(Cart cart) {
        updateCartStatus(cart);
    }

    public void updateCartStatus(Cart cart) {
        String sql = "UPDATE carts SET updated_at = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, cart.getStatus());
            stmt.setLong(3, cart.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating cart: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật giỏ hàng: " + e.getMessage(), e);
        }
    }

    public void saveCartItem(CartItem item) {
        if (item.getId() != null && item.getId() > 0) {
            updateCartItem(item);
        } else {
            insertCartItem(item);
        }
    }

    private void insertCartItem(CartItem item) {
        String sql =
                "INSERT INTO cart_items (cart_id, product_id, product_code, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt =
                databaseManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getCartId());
            stmt.setLong(2, item.getProductId());
            stmt.setString(3, item.getProductCode());
            stmt.setString(4, item.getProductName());
            stmt.setDouble(5, item.getUnitPrice());
            stmt.setLong(6, item.getQuantity());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                item.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting cart item: " + e.getMessage());
            throw new RuntimeException("Lỗi thêm mục giỏ hàng: " + e.getMessage(), e);
        }
    }

    private void updateCartItem(CartItem item) {
        String sql = "UPDATE cart_items SET quantity = ?, unit_price = ? WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, item.getQuantity());
            stmt.setDouble(2, item.getUnitPrice());
            stmt.setLong(3, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật mục giỏ hàng: " + e.getMessage(), e);
        }
    }

    public void deleteCartItem(Long itemId) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa mục giỏ hàng: " + e.getMessage(), e);
        }
    }

    public void clearCart(Long cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, cartId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa giỏ hàng: " + e.getMessage(), e);
        }
    }

    public void delete(Long cartId) {
        clearCart(cartId);
        String sql = "DELETE FROM carts WHERE id = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, cartId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting cart: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa giỏ hàng: " + e.getMessage(), e);
        }
    }

    public List<Cart> findByStatus(String status) {
        List<Cart> list = new ArrayList<>();
        String sql =
                "SELECT id, user_id, created_at, updated_at, status FROM carts WHERE status = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Cart(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding carts by status: " + e.getMessage());
        }
        return list;
    }
}
