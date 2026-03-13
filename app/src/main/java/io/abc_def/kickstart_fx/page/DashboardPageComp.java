package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;
import io.abc_def.kickstart_fx.revenue.RevenueService;
import io.abc_def.kickstart_fx.tax.TaxService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class DashboardPageComp extends SimpleComp {

    private Label totalRevenueLabel;
    private Label totalTaxLabel;
    private Label receiptCountLabel;
    private Label monthlyRevenueLabel;
    private BarChart<String, Number> revenueChart;
    private RevenueService revenueService;
    private ReceiptRepository receiptRepository;

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createStatCards());
        root.getChildren().add(createCharts());
        root.getChildren().add(createRecentReceipts());

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
                        receiptRepository = new ReceiptRepository(dbManager);
                        revenueService = new RevenueService(receiptRepository, new TaxService());
                        loadDashboardData();
                    } catch (Exception e) {
                        System.out.println("Database error: " + e.getMessage());
                        Platform.runLater(this::loadSampleData);
                    }
                })
                .start();
    }

    private void loadDashboardData() {
        try {
            var receipts = revenueService.getAllReceipts();
            Platform.runLater(() -> {
                updateStatistics(receipts);
                updateChart(receipts);
            });
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            Platform.runLater(this::loadSampleData);
        }
    }

    private void loadSampleData() {
        var receipts = Arrays.asList(
                new Receipt(1L, LocalDate.now(), 1000000, 150000, "Hàng hóa"),
                new Receipt(2L, LocalDate.now().minusDays(1), 500000, 75000, "Dịch vụ"),
                new Receipt(3L, LocalDate.now().minusDays(7), 2000000, 300000, "Hàng hóa"),
                new Receipt(4L, LocalDate.now().minusDays(14), 1500000, 225000, "Dịch vụ"));
        updateStatistics(receipts);
        updateChart(receipts);
    }

    private void updateStatistics(List<Receipt> receipts) {
        if (receipts.isEmpty()) {
            return;
        }

        long totalRevenue = receipts.stream().mapToLong(Receipt::getRevenue).sum();
        long totalTax = receipts.stream().mapToLong(Receipt::getTax).sum();
        int count = receipts.size();

        // Find current month revenue
        YearMonth currentMonth = YearMonth.now();
        long monthlyRevenue = receipts.stream()
                .filter(r -> YearMonth.from(r.getDate()).equals(currentMonth))
                .mapToLong(Receipt::getRevenue)
                .sum();

        totalRevenueLabel.setText(String.format("%,d ₫", totalRevenue));
        totalTaxLabel.setText(String.format("%,d ₫", totalTax));
        receiptCountLabel.setText(String.valueOf(count));
        monthlyRevenueLabel.setText(String.format("%,d ₫", monthlyRevenue));
    }

    private void updateChart(List<Receipt> receipts) {
        // Group by date for the month
        Map<String, Long> dailyRevenue = new TreeMap<>();
        receipts.forEach(r -> {
            String dateStr = r.getDate().toString();
            dailyRevenue.merge(dateStr, r.getRevenue(), Long::sum);
        });

        var series = new XYChart.Series<String, Number>();
        series.setName("Doanh Thu");
        dailyRevenue.forEach((date, revenue) -> {
            series.getData().add(new XYChart.Data<>(date, revenue));
        });

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Bảng Điều Khiển");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Tổng quan về chi phí và thuế của bạn");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createStatCards() {
        VBox container = new VBox();
        container.setPadding(new Insets(20, 40, 20, 40));
        container.setSpacing(20);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        totalRevenueLabel = new Label("0 ₫");
        totalTaxLabel = new Label("0 ₫");
        receiptCountLabel = new Label("0");
        monthlyRevenueLabel = new Label("0 ₫");

        grid.add(createStatCard("Tổng Doanh Thu", totalRevenueLabel, "#4CAF50"), 0, 0);
        grid.add(createStatCard("Tổng Thuế", totalTaxLabel, "#F44336"), 1, 0);
        grid.add(createStatCard("Số Hóa Đơn", receiptCountLabel, "#2196F3"), 2, 0);
        grid.add(createStatCard("Doanh Thu Tháng Này", monthlyRevenueLabel, "#FF9800"), 3, 0);

        container.getChildren().add(grid);
        return container;
    }

    private VBox createStatCard(String title, Label value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color:white; -fx-border-color:#e0e0e0; " + "-fx-border-width:1; -fx-border-radius:8;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:12; -fx-text-fill:#666666;");

        value.setStyle("-fx-font-size:24; -fx-font-weight:bold; -fx-text-fill:" + color + ";");

        card.getChildren().addAll(titleLabel, value);
        return card;
    }

    private VBox createCharts() {
        VBox container = new VBox();
        container.setPadding(new Insets(20, 40, 20, 40));
        container.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-margin:20;");

        Label chartTitle = new Label("Doanh Thu Theo Ngày");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh Thu (₫)");

        revenueChart = new BarChart<>(xAxis, yAxis);
        revenueChart.setTitle("Doanh Thu Theo Ngày");
        revenueChart.setPrefHeight(300);

        container.getChildren().addAll(chartTitle, revenueChart);
        return container;
    }

    private VBox createRecentReceipts() {
        VBox container = new VBox();
        container.setPadding(new Insets(20, 40, 20, 40));
        container.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-margin:20;");

        Label tableTitle = new Label("Hóa Đơn Gần Đây");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TableView<Receipt> table = createReceiptTable();
        container.getChildren().addAll(tableTitle, table);

        return container;
    }

    private TableView<Receipt> createReceiptTable() {
        TableView<Receipt> table = new TableView<>();

        TableColumn<Receipt, LocalDate> dateCol = new TableColumn<>("Ngày");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        dateCol.setPrefWidth(100);

        TableColumn<Receipt, Long> revenueCol = new TableColumn<>("Doanh Thu");
        revenueCol.setCellValueFactory(
                cellData -> cellData.getValue().revenueProperty().asObject());
        revenueCol.setPrefWidth(150);

        TableColumn<Receipt, Long> taxCol = new TableColumn<>("Thuế");
        taxCol.setCellValueFactory(cellData -> cellData.getValue().taxProperty().asObject());
        taxCol.setPrefWidth(100);

        TableColumn<Receipt, String> descCol = new TableColumn<>("Mô Tả");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        table.getColumns().addAll(dateCol, revenueCol, taxCol, descCol);
        table.setPrefHeight(250);

        return table;
    }
}
