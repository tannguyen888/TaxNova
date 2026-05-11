package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;

public class Cart {
    private SimpleLongProperty id;
    private SimpleLongProperty userId;
    private SimpleObjectProperty<LocalDateTime> createdAt;
    private SimpleObjectProperty<LocalDateTime> updatedAt;
    private SimpleStringProperty status; // ACTIVE, COMPLETED, CANCELLED
    private SimpleDoubleProperty totalPrice;
    private SimpleLongProperty totalQuantity;
    private ObservableList<CartItem> items;

    public Cart() {
        this.id = new SimpleLongProperty();
        this.userId = new SimpleLongProperty();
        this.createdAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.updatedAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.status = new SimpleStringProperty("ACTIVE");
        this.totalPrice = new SimpleDoubleProperty(0.0);
        this.totalQuantity = new SimpleLongProperty(0);
        this.items = FXCollections.observableArrayList();
    }

    public Cart(Long userId) {
        this();
        this.userId.set(userId);
    }

    public Cart(Long id, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt, String status) {
        this.id = new SimpleLongProperty(id);
        this.userId = new SimpleLongProperty(userId);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
        this.status = new SimpleStringProperty(status);
        this.totalPrice = new SimpleDoubleProperty(0.0);
        this.totalQuantity = new SimpleLongProperty(0);
        this.items = FXCollections.observableArrayList();
    }

    // Getters and Setters
    public Long getId() {
        return id.get();
    }

    public void setId(Long value) {
        id.set(value);
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public Long getUserId() {
        return userId.get();
    }

    public void setUserId(Long value) {
        userId.set(value);
    }

    public SimpleLongProperty userIdProperty() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime value) {
        createdAt.set(value);
    }

    public SimpleObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime value) {
        updatedAt.set(value);
    }

    public SimpleObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public void setTotalPrice(double value) {
        totalPrice.set(value);
    }

    public SimpleDoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public long getTotalQuantity() {
        return totalQuantity.get();
    }

    public void setTotalQuantity(long value) {
        totalQuantity.set(value);
    }

    public SimpleLongProperty totalQuantityProperty() {
        return totalQuantity;
    }

    public ObservableList<CartItem> getItems() {
        return items;
    }

    public void setItems(ObservableList<CartItem> items) {
        this.items = items;
    }

    public void addItem(CartItem item) {
        items.add(item);
        recalculateTotals();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        recalculateTotals();
    }

    public void removeItemById(Long itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        recalculateTotals();
    }

    public void clearItems() {
        items.clear();
        recalculateTotals();
    }

    private void recalculateTotals() {
        double total = 0;
        long quantity = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
            quantity += item.getQuantity();
        }
        this.totalPrice.set(total);
        this.totalQuantity.set(quantity);
        this.updatedAt.set(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Cart{" + "id="
                + id.get() + ", userId="
                + userId.get() + ", status='"
                + status.get() + '\'' + ", totalPrice="
                + totalPrice.get() + ", items="
                + items.size() + '}';
    }
}
