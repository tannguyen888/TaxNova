package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;
import io.abc_def.kickstart_fx.revenue.RevenueService;
import io.abc_def.kickstart_fx.tax.TaxService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class ReportsPageComp extends SimpleComp {

    private BarChart<String, Number> monthlyRevenueChart;
    private PieChart taxDistributionChart;
    private LineChart<String, Number> trendChart;
    private RevenueService revenueService;

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createSummary());
        root.getChildren().add(createCharts());

        initDatabase();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private void initDatabase() {
        new Thread(() -> {
                    try {
                        DatabaseManager dbManager = new DatabaseManager("");
                        dbManager.connect();
                        ReceiptRepository receiptRepository = new ReceiptRepository(dbManager);
                        revenueService = new RevenueService(receiptRepository, new TaxService());
                        loadReportData();
                    } catch (Exception e) {
                        System.out.println("Database error: " + e.getMessage());
                        loadSampleData();
                    }
                })
                .start();
    }

    private void loadReportData() {
        try {
            var receipts = revenueService.getAllReceipts();
            Platform.runLater(() -> {
                updateCharts(receipts);
            });
        } catch (Exception e) {
            System.out.println("Error loading reports: " + e.getMessage());
            loadSampleData();
        }
    }

    private void loadSampleData() {
        var receipts = Arrays.asList(
                new Receipt(1L, LocalDate.now(), 1000000, 150000, "Hàng hóa"),
                new Receipt(2L, LocalDate.now().minusDays(1), 500000, 75000, "Dịch vụ"),
                new Receipt(3L, LocalDate.now().minusDays(7), 2000000, 300000, "Hàng hóa"),
                new Receipt(4L, LocalDate.now().minusDays(14), 1500000, 225000, "Dịch vụ"),
                new Receipt(5L, LocalDate.now().minusDays(30), 3000000, 450000, "Khác"));
        Platform.runLater(() -> updateCharts(receipts));
    }

    private void updateCharts(java.util.List<Receipt> receipts) {
        updateMonthlyChart(receipts);
        updateTaxChart(receipts);
        updateTrendChart(receipts);
    }

    private void updateMonthlyChart(java.util.List<Receipt> receipts) {
        var monthlySeries = new XYChart.Series<String, Number>();
        monthlySeries.setName("Doanh Thu Theo Tháng");

        Map<YearMonth, Long> monthlyData = new TreeMap<>(Comparator.reverseOrder());
        receipts.forEach(r -> {
            YearMonth month = YearMonth.from(r.getDate());
            monthlyData.merge(month, r.getRevenue(), Long::sum);
        });

        monthlyData.forEach((month, revenue) -> {
            monthlySeries.getData().add(new XYChart.Data<>(month.toString(), revenue));
        });

        monthlyRevenueChart.getData().clear();
        monthlyRevenueChart.getData().add(monthlySeries);
    }

    private void updateTaxChart(java.util.List<Receipt> receipts) {
        Map<String, Long> categoryTax = new HashMap<>();
        receipts.forEach(r -> {
            categoryTax.merge(r.getDescription(), r.getTax(), Long::sum);
        });

        taxDistributionChart.getData().clear();
        categoryTax.forEach((category, tax) -> {
            taxDistributionChart.getData().add(new PieChart.Data(category + " (" + tax + "₫)", tax));
        });
    }

    private void updateTrendChart(java.util.List<Receipt> receipts) {
        var trendSeries = new XYChart.Series<String, Number>();
        trendSeries.setName("Xu Hướng Doanh Thu");

        Map<LocalDate, Long> dailyData = new TreeMap<>();
        receipts.forEach(r -> {
            dailyData.merge(r.getDate(), r.getRevenue(), Long::sum);
        });

        dailyData.forEach((date, revenue) -> {
            trendSeries.getData().add(new XYChart.Data<>(date.toString(), revenue));
        });

        trendChart.getData().clear();
        trendChart.getData().add(trendSeries);
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Báo Cáo và Thống Kê");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Phân tích chi tiết về doanh thu và thuế");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createSummary() {
        VBox summary = new VBox();
        summary.setPadding(new Insets(20, 40, 20, 40));
        summary.setStyle("-fx-background-color:white;");

        Label summaryTitle = new Label("Tóm Tắt");
        summaryTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label yearToDateLabel = new Label("📅 Năm nay: ");
        Label quarterLabel = new Label("📊 Quý này: ");
        Label monthLabel = new Label("📈 Tháng này: ");

        summary.getChildren().addAll(summaryTitle, new Separator(), yearToDateLabel, quarterLabel, monthLabel);

        return summary;
    }

    private VBox createCharts() {
        VBox chartsContainer = new VBox(20);
        chartsContainer.setPadding(new Insets(20, 40, 20, 40));

        // Monthly Revenue Chart
        VBox monthlyBox = new VBox(10);
        monthlyBox.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-padding:20;");
        Label monthlyTitle = new Label("Doanh Thu Theo Tháng");
        monthlyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        CategoryAxis monthXAxis = new CategoryAxis();
        NumberAxis monthYAxis = new NumberAxis();
        monthYAxis.setLabel("Doanh Thu (₫)");
        monthlyRevenueChart = new BarChart<>(monthXAxis, monthYAxis);
        monthlyRevenueChart.setPrefHeight(300);

        monthlyBox.getChildren().addAll(monthlyTitle, monthlyRevenueChart);

        // Tax Distribution Chart
        VBox taxBox = new VBox(10);
        taxBox.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-padding:20;");
        Label taxTitle = new Label("Phân Phối Thuế Theo Danh Mục");
        taxTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        taxDistributionChart = new PieChart();
        taxDistributionChart.setPrefHeight(300);
        taxDistributionChart.setLegendVisible(true);

        taxBox.getChildren().addAll(taxTitle, taxDistributionChart);

        // Trend Chart
        VBox trendBox = new VBox(10);
        trendBox.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-padding:20;");
        Label trendTitle = new Label("Xu Hướng Doanh Thu");
        trendTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        CategoryAxis trendXAxis = new CategoryAxis();
        NumberAxis trendYAxis = new NumberAxis();
        trendYAxis.setLabel("Doanh Thu (₫)");
        trendChart = new LineChart<>(trendXAxis, trendYAxis);
        trendChart.setPrefHeight(300);

        trendBox.getChildren().addAll(trendTitle, trendChart);

        chartsContainer.getChildren().addAll(monthlyBox, taxBox, trendBox);
        return chartsContainer;
    }
}
