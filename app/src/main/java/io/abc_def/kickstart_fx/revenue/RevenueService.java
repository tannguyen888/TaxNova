package io.abc_def.kickstart_fx.revenue;

import java.util.List;

import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;
import io.abc_def.kickstart_fx.tax.TaxCalculator;
import io.abc_def.kickstart_fx.tax.TaxService;

public class RevenueService {

    private final ReceiptRepository receiptRepository;
    private final TaxService taxService;

    public RevenueService(ReceiptRepository receiptRepository,
            TaxService taxService) {
        this.receiptRepository = receiptRepository;
        this.taxService = taxService;
    }

    public List<Receipt> getAllReceipts() {

        List<Receipt> receipts = receiptRepository.findAll();

        if (receipts.isEmpty()) {
            throw new IllegalArgumentException("There is no receipt in the database");
        }

        return receipts;
    }

    public double calculateRevenue() {

        List<Receipt> receipts = receiptRepository.findAll();

        if (receipts.isEmpty()) {
            throw new IllegalArgumentException("There is no data to calculate revenue");
        }

        double total = receipts.stream()
                .mapToDouble(Receipt::getAmount)
                .sum();

        return total;
    }

    public double calculateTax() {

        List<Receipt> receipts = receiptRepository.findAll();

        if (receipts.isEmpty()) {
            return 0.0;
        }

        return receipts.stream()
                .mapToDouble(r -> taxService.computeTax(r.getAmount()))
                .sum();
    }
}
