package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.*;

public class CartItem {
    private SimpleLongProperty id;
    private SimpleLongProperty cartId;
    private SimpleLongProperty productId;
    private SimpleStringProperty productCode;
    private SimpleStringProperty productName;
    private SimpleDoubleProperty unitPrice;
    private SimpleLongProperty quantity;
    private SimpleDoubleProperty totalPrice;

    public CartItem() {
        this.id = new SimpleLongProperty();
        this.cartId = new SimpleLongProperty();
        this.productId = new SimpleLongProperty();
        this.productCode = new SimpleStringProperty();
        this.productName = new SimpleStringProperty();
        this.unitPrice = new SimpleDoubleProperty();
        this.quantity = new SimpleLongProperty();
        this.totalPrice = new SimpleDoubleProperty();
    }

    public CartItem(Long productId, String productCode, String productName, double unitPrice, long quantity) {
        this();
        this.productId.set(productId);
        this.productCode.set(productCode);
        this.productName.set(productName);
        this.unitPrice.set(unitPrice);
        this.quantity.set(quantity);
        this.totalPrice.set(unitPrice * quantity);
    }

    public CartItem(
            Long id,
            Long cartId,
            Long productId,
            String productCode,
            String productName,
            double unitPrice,
            long quantity) {
        this.id = new SimpleLongProperty(id);
        this.cartId = new SimpleLongProperty(cartId);
        this.productId = new SimpleLongProperty(productId);
        this.productCode = new SimpleStringProperty(productCode);
        this.productName = new SimpleStringProperty(productName);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.quantity = new SimpleLongProperty(quantity);
        this.totalPrice = new SimpleDoubleProperty(unitPrice * quantity);
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

    public Long getCartId() {
        return cartId.get();
    }

    public void setCartId(Long value) {
        cartId.set(value);
    }

    public SimpleLongProperty cartIdProperty() {
        return cartId;
    }

    public Long getProductId() {
        return productId.get();
    }

    public void setProductId(Long value) {
        productId.set(value);
    }

    public SimpleLongProperty productIdProperty() {
        return productId;
    }

    public String getProductCode() {
        return productCode.get();
    }

    public void setProductCode(String value) {
        productCode.set(value);
    }

    public SimpleStringProperty productCodeProperty() {
        return productCode;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String value) {
        productName.set(value);
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(double value) {
        unitPrice.set(value);
    }

    public SimpleDoubleProperty unitPriceProperty() {
        return unitPrice;
    }

    public long getQuantity() {
        return quantity.get();
    }

    public void setQuantity(long value) {
        quantity.set(value);
        updateTotalPrice();
    }

    public SimpleLongProperty quantityProperty() {
        return quantity;
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

    private void updateTotalPrice() {
        this.totalPrice.set(this.unitPrice.get() * this.quantity.get());
    }

    @Override
    public String toString() {
        return "CartItem{" + "id="
                + id.get() + ", productName='"
                + productName.get() + '\'' + ", quantity="
                + quantity.get() + ", unitPrice="
                + unitPrice.get() + ", totalPrice="
                + totalPrice.get() + '}';
    }
}
