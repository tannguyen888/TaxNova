package io.abc_def.kickstart_fx.revenue;

import io.abc_def.kickstart_fx.domain.Receipt;

import java.time.YearMonth;
import java.util.List;

public class RevenueViewModel {
    private final RevenueService revenueService;

    public RevenueViewModel(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    public List<Receipt> getAllReceipts() {
        return revenueService.getAllReceipts();
    }

    public long getTotalRevenue() {
        return revenueService.calculateTotalRevenue();
    }

    public long getTotalTax() {
        return revenueService.calculateTotalTax();
    }

    public long getMonthlyRevenue(YearMonth month) {
        return revenueService.calculateMonthlyRevenue(month);
    }

    public long getMonthlyTax(YearMonth month) {
        return revenueService.calculateMonthlyTax(month);
    }

    public void deleteReceipt(Receipt receipt) {
        revenueService.deleteReceipt(receipt);
    }

    public void saveReceipt(Receipt newReceipt) {
        revenueService.saveReceipt(newReceipt);
    }

    public double getAverageRevenue() {
        return revenueService.getAverageRevenue();
    }

    public double getAverageTaxRate() {
        return revenueService.getAverageTaxRate();
    }
}
