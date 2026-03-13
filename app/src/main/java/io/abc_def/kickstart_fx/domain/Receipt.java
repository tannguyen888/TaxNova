package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Receipt {

    private SimpleLongProperty id;
    private SimpleObjectProperty<LocalDate> date;
    private SimpleLongProperty revenue;
    private SimpleLongProperty tax;
    private SimpleStringProperty description;

    public Receipt() {
        this.id = new SimpleLongProperty();
        this.date = new SimpleObjectProperty<>();
        this.revenue = new SimpleLongProperty();
        this.tax = new SimpleLongProperty();
        this.description = new SimpleStringProperty();
    }

    public Receipt(Long id, LocalDate date, long revenue, long tax, String description) {
        this.id = new SimpleLongProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.revenue = new SimpleLongProperty(revenue);
        this.tax = new SimpleLongProperty(tax);
        this.description = new SimpleStringProperty(description);
    }

    public Receipt(LocalDate date, long revenue, long tax, String description) {
        this(null, date, revenue, tax, description);
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

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate value) {
        date.set(value);
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public long getRevenue() {
        return revenue.get();
    }

    public void setRevenue(long value) {
        revenue.set(value);
    }

    public SimpleLongProperty revenueProperty() {
        return revenue;
    }

    public long getTax() {
        return tax.get();
    }

    public void setTax(long value) {
        tax.set(value);
    }

    public SimpleLongProperty taxProperty() {
        return tax;
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

    // Legacy method names for compatibility
    public double getAmount() {
        return revenue.get();
    }

    public void setAmount(double value) {
        revenue.set((long) value);
    }

    public double getTaxAmount() {
        return tax.get();
    }

    public void setTaxAmount(double value) {
        tax.set((long) value);
    }

    public String getCategory() {
        return description.get();
    }

    public void setCategory(String value) {
        description.set(value);
    }

    @Override
    public String toString() {
        return "Receipt{" + "id="
                + id.get() + ", date="
                + date.get() + ", revenue="
                + revenue.get() + ", tax="
                + tax.get() + ", description='"
                + description.get() + '\'' + '}';
    }
}
