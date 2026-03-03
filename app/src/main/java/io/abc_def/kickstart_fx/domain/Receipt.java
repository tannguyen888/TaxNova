package io.abc_def.kickstart_fx.domain;

import java.time.LocalDate;

public class Receipt {
    private Long id;
    private LocalDate date;
    private double taxAmount;
    private String category;

    public Receipt() {}

    public Receipt(Long id, LocalDate date, double amount, double taxAmount, String category) {
        this.id = id;
        this.date = date;
        this.taxAmount = taxAmount;
        this.category = category;
    }
}
