package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Product {
    private SimpleLongProperty id;
    private SimpleStringProperty code;
    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private SimpleDoubleProperty price;
    private SimpleLongProperty quantity;
    private SimpleStringProperty category;
    private SimpleObjectProperty<LocalDateTime> createdAt;
    private SimpleObjectProperty<LocalDateTime> updatedAt;

    public Product() {
        this.id = new SimpleLongProperty();
        this.code = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.quantity = new SimpleLongProperty();
        this.category = new SimpleStringProperty();
        this.createdAt = new SimpleObjectProperty<>();
        this.updatedAt = new SimpleObjectProperty<>();
    }

    public Product(String code, String name, String description, double price, long quantity, String category) {
        this();
        this.code.set(code);
        this.name.set(name);
        this.description.set(description);
        this.price.set(price);
        this.quantity.set(quantity);
        this.category.set(category);
        this.createdAt.set(LocalDateTime.now());
        this.updatedAt.set(LocalDateTime.now());
    }

    public Product(
            Long id,
            String code,
            String name,
            String description,
            double price,
            long quantity,
            String category,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = new SimpleLongProperty(id);
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleLongProperty(quantity);
        this.category = new SimpleStringProperty(category);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
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

    public String getCode() {
        return code.get();
    }

    public void setCode(String value) {
        code.set(value);
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double value) {
        price.set(value);
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public long getQuantity() {
        return quantity.get();
    }

    public void setQuantity(long value) {
        quantity.set(value);
    }

    public SimpleLongProperty quantityProperty() {
        return quantity;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String value) {
        category.set(value);
    }

    public SimpleStringProperty categoryProperty() {
        return category;
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

    @Override
    public String toString() {
        return "Product{" + "id="
                + id.get() + ", code='"
                + code.get() + '\'' + ", name='"
                + name.get() + '\'' + ", price="
                + price.get() + ", quantity="
                + quantity.get() + '}';
    }
}
