package io.abc_def.kickstart_fx.reporting;

import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.tax.TaxService;

import java.time.LocalDate;
import java.util.List;

public class ReportBuilder {
    private List<Receipt> receipts;
    private TaxService taxService;

    public ReportBuilder(List<Receipt> receipts, TaxService taxService) {
        this.receipts = receipts;
        this.taxService = taxService;
    }

    public void buildReport() {
        // Can add implementation for other report types
    }

    public String buildRevenueReport() {
        if (receipts == null || receipts.isEmpty()) {
            throw new IllegalArgumentException("There is no receipt in the database");
        }

        StringBuilder report = new StringBuilder();
        report.append("BÁO CÁO DOANH THU\n");
        report.append("Ngày xuất báo cáo: ").append(LocalDate.now()).append("\n\n");
        report.append("Chi tiết doanh thu:\n");

        double totalAmount = 0;
        double totalTax = 0;

        for (Receipt receipt : receipts) {
            report.append("Ngày: ").append(receipt.getDate()).append(", ");
            report.append("Số tiền: ").append(receipt.getRevenue()).append(", ");
            report.append("Thuế: ").append(receipt.getTax()).append(", ");
            report.append("Danh mục: ").append(receipt.getDescription()).append("\n");

            totalAmount += receipt.getRevenue();
            totalTax += receipt.getTax();
        }

        report.append("\nTổng doanh thu: ").append(totalAmount).append("\n");
        report.append("Tổng thuế: ").append(totalTax).append("\n");

        double calculateTax = receipts.stream().mapToDouble(r -> r.getTax()).sum();
        report.append("Tổng thuế sau khi tính: ").append(calculateTax).append("\n");

        return report.toString();
    }
}
