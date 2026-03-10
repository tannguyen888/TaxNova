package io.abc_def.kickstart_fx.revenue;

import io.abc_def.kickstart_fx.domain.Receipt;

import java.util.List;

public class RevenueViewModel {
    private final RevenueService revenueService;

    public RevenueViewModel(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    public List<Receipt> getAllReceipts() {
        return revenueService.getAllReceipts();
    }

    public double getTotalRevenue() {
        return revenueService.calculateRevenue();
    }

    public double getTotalTax() {
        return revenueService.calculateTax();
    }

    public void deleteReceipt(Receipt receipt) {
        revenueService.deleteReceipt(receipt);
    }

    public Object saveReceipt(Receipt newReceipt) {
        return revenueService.saveReceipt(newReceipt);
    }
}
