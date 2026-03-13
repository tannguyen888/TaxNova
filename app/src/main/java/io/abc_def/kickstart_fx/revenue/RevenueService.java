package io.abc_def.kickstart_fx.revenue;

import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;
import io.abc_def.kickstart_fx.tax.TaxService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class RevenueService {

    private final ReceiptRepository receiptRepository;
    private final TaxService taxService;

    public RevenueService(ReceiptRepository receiptRepository, TaxService taxService) {
        this.receiptRepository = receiptRepository;
        this.taxService = taxService;
    }

    public List<Receipt> getAllReceipts() {
        try {
            return receiptRepository.findAll();
        } catch (Exception e) {
            System.out.println("No receipts found: " + e.getMessage());
            return List.of();
        }
    }

    public List<Receipt> getReceiptsByMonth(YearMonth month) {
        return getAllReceipts().stream()
                .filter(r -> YearMonth.from(r.getDate()).equals(month))
                .collect(Collectors.toList());
    }

    public List<Receipt> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getAllReceipts().stream()
                .filter(r -> !r.getDate().isBefore(startDate) && !r.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public long calculateTotalRevenue() {
        return getAllReceipts().stream().mapToLong(Receipt::getRevenue).sum();
    }

    public long calculateMonthlyRevenue(YearMonth month) {
        return getReceiptsByMonth(month).stream().mapToLong(Receipt::getRevenue).sum();
    }

    public long calculateTotalTax() {
        return getAllReceipts().stream().mapToLong(Receipt::getTax).sum();
    }

    public long calculateMonthlyTax(YearMonth month) {
        return getReceiptsByMonth(month).stream().mapToLong(Receipt::getTax).sum();
    }

    public void saveReceipt(Receipt receipt) {
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }
        try {
            // Generate ID if not present
            if (receipt.getId() == null) {
                receipt.setId(System.currentTimeMillis());
            }
            receiptRepository.save(receipt);
        } catch (Exception e) {
            throw new RuntimeException("Error saving receipt: " + e.getMessage(), e);
        }
    }

    public void deleteReceipt(Receipt receipt) {
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }
        try {
            receiptRepository.delete(receipt);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting receipt: " + e.getMessage(), e);
        }
    }

    public double getAverageRevenue() {
        List<Receipt> receipts = getAllReceipts();
        if (receipts.isEmpty()) {
            return 0.0;
        }
        return (double) calculateTotalRevenue() / receipts.size();
    }

    public double getAverageTaxRate() {
        long totalRevenue = calculateTotalRevenue();
        long totalTax = calculateTotalTax();
        if (totalRevenue == 0) {
            return 0.0;
        }
        return (double) totalTax / totalRevenue;
    }

    public TaxService.TaxReport generateReport() {
        return taxService.generateTaxReport(getAllReceipts());
    }
}
