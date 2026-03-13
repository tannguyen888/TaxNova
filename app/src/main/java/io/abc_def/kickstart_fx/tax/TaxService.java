package io.abc_def.kickstart_fx.tax;

import java.time.LocalDate;

public class TaxService {

    private final TaxCalculator taxCalculator;
    private static final double DEFAULT_TAX_RATE = 0.1; // 10% tax

    public TaxService() {
        this.taxCalculator = new TaxCalculator();
    }

    public double computeTax(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        double tax = taxCalculator.calculator(amount);
        return tax;
    }

    public long calculateTax(long revenue) {
        return Math.round(revenue * DEFAULT_TAX_RATE);
    }

    public long calculateTaxByRate(long revenue, double customRate) {
        if (customRate < 0 || customRate > 1) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1");
        }
        return Math.round(revenue * customRate);
    }

    public long getNetRevenue(long revenue) {
        return revenue - calculateTax(revenue);
    }

    public double getTaxRate() {
        return DEFAULT_TAX_RATE;
    }

    public String getTaxDescription(long revenue, long tax) {
        double effectiveRate = revenue > 0 ? (double) tax / revenue * 100 : 0;
        return String.format("Doanh thu: %,d ₫, Thuế: %,d ₫, Mức thuế: %.1f%%", revenue, tax, effectiveRate);
    }

    public TaxReport generateTaxReport(java.util.List<io.abc_def.kickstart_fx.domain.Receipt> receipts) {
        if (receipts == null || receipts.isEmpty()) {
            return new TaxReport(0, 0, 0, LocalDate.now());
        }

        long totalRevenue = receipts.stream()
                .mapToLong(io.abc_def.kickstart_fx.domain.Receipt::getRevenue)
                .sum();
        long totalTax = receipts.stream()
                .mapToLong(io.abc_def.kickstart_fx.domain.Receipt::getTax)
                .sum();
        int receiptCount = receipts.size();

        return new TaxReport(totalRevenue, totalTax, receiptCount, LocalDate.now());
    }

    public static class TaxReport {
        public final long totalRevenue;
        public final long totalTax;
        public final int receiptCount;
        public final LocalDate generatedDate;

        public TaxReport(long totalRevenue, long totalTax, int receiptCount, LocalDate generatedDate) {
            this.totalRevenue = totalRevenue;
            this.totalTax = totalTax;
            this.receiptCount = receiptCount;
            this.generatedDate = generatedDate;
        }

        public double getEffectiveTaxRate() {
            return totalRevenue > 0 ? (double) totalTax / totalRevenue : 0;
        }

        @Override
        public String toString() {
            return "TaxReport{" + "totalRevenue="
                    + totalRevenue + ", totalTax="
                    + totalTax + ", receiptCount="
                    + receiptCount + ", generatedDate="
                    + generatedDate + ", effectiveRate="
                    + String.format("%.2f%%", getEffectiveTaxRate() * 100) + '}';
        }
    }
}
