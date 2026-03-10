package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;
import io.abc_def.kickstart_fx.revenue.RevenueService;
import io.abc_def.kickstart_fx.tax.TaxService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.DoubleStringConverter;

import java.time.LocalDate;

public class TaxPageComp extends SimpleComp {

    private final ObservableList<Receipt> receipts = FXCollections.observableArrayList();

    private Label totalRevenueLabel = new Label("0 ₫");
    private Label totalTaxLabel = new Label("0 ₫");
    private Label receiptCountLabel = new Label("0");

    private TextField searchField = new TextField();

    private RevenueService revenueService;
    private ReceiptRepository receiptRepository;
    private volatile boolean databaseReady = false;
    private final Object dbLock = new Object();

    @Override
    protected Region createSimple() {

        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createStatsCards());
        root.getChildren().add(createFormInput());
        root.getChildren().add(createReceiptTable());

        Thread dbThread = new Thread(() -> {
            System.out.println("DEBUG: Starting database initialization thread");
            try {
                synchronized (dbLock) {
                    if (receiptRepository == null) {
                        System.out.println("DEBUG: Creating DatabaseManager...");
                        DatabaseManager dbManager = new DatabaseManager("");
                        System.out.println("DEBUG: Connecting to database...");
                        dbManager.connect();
                        System.out.println("DEBUG: Creating ReceiptRepository...");
                        receiptRepository = new ReceiptRepository(dbManager);
                        System.out.println("DEBUG: Creating RevenueService...");
                        revenueService = new RevenueService(receiptRepository, new TaxService());
                        System.out.println("DEBUG: Loading from database...");
                        loadFromDatabase();
                        databaseReady = true;
                        System.out.println("DEBUG: Database initialization completed");
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khởi tạo database: " + e.getMessage());
                e.printStackTrace();
                System.out.println("DEBUG: Database initialization failed, loading sample data");
                loadFromDatabase(); // This will use sample data in catch block
            }
        });
        dbThread.setDaemon(true);
        dbThread.start();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private void loadFromDatabase() {
        try {
            if (receiptRepository != null) {
                var allReceipts = revenueService.getAllReceipts();
                System.out.println("DEBUG: Loaded from DB: " + allReceipts.size() + " receipts");

                // Update UI on FX Application Thread
                Platform.runLater(() -> {
                    receipts.clear();
                    receipts.addAll(allReceipts);
                    System.out.println("DEBUG: Total receipts in list: " + receipts.size());
                    updateStats();
                });
            }
        } catch (Exception e) {
            System.err.println("Lỗi load data: " + e.getMessage());
            System.out.println("DEBUG: Database appears to be empty - this is normal on first run");

            // Add sample data for testing if database fails
            System.out.println("DEBUG: Adding sample test data to table");
            Platform.runLater(() -> {
                receipts.addAll(
                        new Receipt(1L, LocalDate.now(), 1000000, 150000, "Hàng hóa"),
                        new Receipt(2L, LocalDate.now().minusDays(1), 500000, 75000, "Dịch vụ"));
                updateStats();
            });
        }
    }

    private VBox createHeader() {

        VBox box = new VBox(10);
        box.setPadding(new Insets(30));

        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0)");

        Label title = new Label("Quản Lý Thuế");
        title.setStyle("-fx-font-size:32;-fx-text-fill:white;-fx-font-weight:bold");

        Label sub = new Label("Quản lý và chỉnh sửa hóa đơn");
        sub.setStyle("-fx-text-fill:white");

        box.getChildren().addAll(title, sub);

        return box;
    }

    private VBox createStatsCards() {

        VBox container = new VBox();
        container.setPadding(new Insets(20, 40, 20, 40));

        GridPane grid = new GridPane();
        grid.setHgap(20);

        grid.add(createStatCard("Tổng Doanh Thu", totalRevenueLabel, "#4CAF50", "📊"), 0, 0);
        grid.add(createStatCard("Tổng Thuế", totalTaxLabel, "#F44336", "💰"), 1, 0);
        grid.add(createStatCard("Thuế Suất", new Label("15%"), "#2196F3", "📈"), 2, 0);
        grid.add(createStatCard("Số Hóa Đơn", receiptCountLabel, "#FF9800", "📄"), 3, 0);

        container.getChildren().add(grid);

        return container;
    }

    private VBox createStatCard(String title, Label value, String color, String icon) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));

        card.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:8;
                -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),4,0,0,2);
                """);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:26");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill:#888");

        value.setStyle("-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + color);

        card.getChildren().addAll(iconLabel, titleLabel, value);

        return card;
    }

    private VBox createFormInput() {

        VBox box = new VBox(15);
        box.setPadding(new Insets(20, 40, 20, 40));
        box.setStyle("-fx-background-color:white");

        Label title = new Label("Thêm Hóa Đơn");
        title.setStyle("-fx-font-size:16;-fx-font-weight:bold");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField amountField = new TextField();
        amountField.setPromptText("Doanh thu");

        TextField taxField = new TextField();
        taxField.setDisable(true);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Danh mục");

        amountField.textProperty().addListener((obs, o, n) -> {
            try {
                double amount = Double.parseDouble(n);
                taxField.setText(String.format("%.2f ₫", amount * 0.15));
            } catch (Exception e) {
                taxField.setText("0 ₫");
            }
        });

        Button addBtn = new Button("+ Thêm");

        addBtn.setOnAction(e -> {
            try {
                // Wait for database to be ready
                synchronized (dbLock) {
                    if (!databaseReady) {
                        showAlert("Lỗi", "Cơ sở dữ liệu đang khởi tạo, vui lòng chờ...");
                        return;
                    }
                }

                double amount = Double.parseDouble(amountField.getText());
                String category = categoryField.getText();

                if (category.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng nhập danh mục");
                    return;
                }

                Receipt r = new Receipt(System.nanoTime(), datePicker.getValue(), amount, amount * 0.15, category);

                System.out.println("DEBUG: Created receipt: " + r);

                // Lưu vào database nếu đã khởi tạo
                if (revenueService != null) {
                    revenueService.saveReceipt(r);
                    System.out.println("DEBUG: Saved to database");
                } else {
                    System.out.println("DEBUG: revenueService is null - database not initialized");
                }

                receipts.add(r);
                System.out.println("DEBUG: Added to ObservableList. Total: " + receipts.size());

                amountField.clear();
                categoryField.clear();
                taxField.clear();

                updateStats();
                showAlert("Thành công", "Hóa đơn đã được thêm");

            } catch (Exception ex) {
                showAlert("Lỗi", "Doanh thu phải là số: " + ex.getMessage());
            }
        });

        grid.add(new Label("Ngày"), 0, 0);
        grid.add(datePicker, 1, 0);

        grid.add(new Label("Doanh thu"), 2, 0);
        grid.add(amountField, 3, 0);

        grid.add(new Label("Thuế"), 0, 1);
        grid.add(taxField, 1, 1);

        grid.add(new Label("Danh mục"), 2, 1);
        grid.add(categoryField, 3, 1);

        box.getChildren().addAll(title, grid, addBtn);

        return box;
    }

    private VBox createReceiptTable() {

        VBox container = new VBox(10);
        container.setPadding(new Insets(20, 40, 40, 40));

        Label title = new Label("Danh Sách Hóa Đơn");
        title.setStyle("-fx-font-size:16;-fx-font-weight:bold");

        searchField.setPromptText("Tìm danh mục...");

        TableView<Receipt> table = new TableView<>();
        table.setEditable(true);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Receipt, LocalDate> dateCol = new TableColumn<>("Ngày");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Receipt, Double> amountCol = new TableColumn<>("Doanh Thu");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        amountCol.setOnEditCommit(e -> {
            Receipt r = e.getRowValue();

            r.setAmount(e.getNewValue());
            r.setTaxAmount(e.getNewValue() * 0.15);

            // Lưu vào database nếu đã khởi tạo
            if (receiptRepository != null) {
                receiptRepository.save(r);
            }

            table.refresh();

            updateStats();
        });

        TableColumn<Receipt, Double> taxCol = new TableColumn<>("Thuế");
        taxCol.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));

        TableColumn<Receipt, String> catCol = new TableColumn<>("Danh Mục");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        catCol.setCellFactory(TextFieldTableCell.forTableColumn());

        catCol.setOnEditCommit(e -> {
            Receipt r = e.getRowValue();
            String newVal = e.getNewValue() != null ? e.getNewValue() : "";
            r.setCategory(newVal);

            if (receiptRepository != null) {
                receiptRepository.save(r);
            }

            table.refresh();
        });

        TableColumn<Receipt, Void> actionCol = new TableColumn<>("Hành Động");

        actionCol.setCellFactory(col -> new TableCell<>() {

            Button deleteBtn = new Button("Xóa");

            {
                deleteBtn.setOnAction(e -> {
                    Receipt r = getTableView().getItems().get(getIndex());

                    if (receiptRepository != null) {
                        receiptRepository.delete(r);
                    }

                    receipts.remove(r);

                    updateStats();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().addAll(dateCol, amountCol, taxCol, catCol, actionCol);

        FilteredList<Receipt> filtered = new FilteredList<>(receipts, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtered.setPredicate(r -> {
                if (newVal == null || newVal.isEmpty()) return true;

                return r.getCategory().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        table.setItems(filtered);

        container.getChildren().addAll(title, searchField, table);

        return container;
    }

    private void updateStats() {

        double revenue = receipts.stream().mapToDouble(Receipt::getAmount).sum();

        double tax = receipts.stream().mapToDouble(Receipt::getTaxAmount).sum();

        totalRevenueLabel.setText(String.format("%.2f ₫", revenue));
        totalTaxLabel.setText(String.format("%.2f ₫", tax));
        receiptCountLabel.setText(String.valueOf(receipts.size()));
    }

    // public void updateTablesCol() {

    // DoubleStream revenue = receipts.stream()
    // .mapToDouble(Receipt::getAmount);

    // DoubleStream tax = receipts.stream()
    // .mapToDouble(Receipt::getTaxAmount);

    // String category = receipts.stream()
    // .map(Receipt::getCategory)
    // .findFirst()
    // .orElse("");

    // doanhThu.setText(String.format("%.2f ₫", revenue));
    // thue.setText(String.format("%.2f ₫", tax));
    // danhMuc.setText(String.format("%s", category));
    // }

    private void showAlert(String title, String msg) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
