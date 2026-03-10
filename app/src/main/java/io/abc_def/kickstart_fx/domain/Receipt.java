package io.abc_def.kickstart_fx.domain;

import java.time.LocalDate;

public class Receipt {

    private Long id;
    private LocalDate date;
    private double amount; // ← bạn đang thiếu field này!
    private double taxAmount;
    private String category;

    public Receipt() {}

    public Receipt(Long id, LocalDate date, double amount, double taxAmount, String category) {
        this.id = id;
        this.date = date;
        this.amount = amount; // ← thiếu dòng này
        this.taxAmount = taxAmount;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
