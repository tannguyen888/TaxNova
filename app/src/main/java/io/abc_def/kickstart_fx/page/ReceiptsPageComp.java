package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ReceiptRepository;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.LongStringConverter;

import java.time.LocalDate;

public class ReceiptsPageComp extends SimpleComp {

    private ReceiptRepository receiptRepository;
    private TableView<Receipt> receiptTable;
    private TextField searchField;
    private Label totalCountLabel;
    private Label totalRevenueLabel;

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createToolbar());
        root.getChildren().add(createTableContainer());
        root.getChildren().add(createFooter());

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
                        loadReceipts();
                    } catch (Exception e) {
                        System.out.println("Database error: " + e.getMessage());
                        loadSampleReceipts();
                    }
                })
                .start();
    }

    private void loadReceipts() {
        try {
            var receipts = receiptRepository.findAll();
            Platform.runLater(() -> {
                receiptTable.getItems().setAll(receipts);
                updateFooter(receipts);
            });
        } catch (Exception e) {
            System.out.println("Error loading receipts: " + e.getMessage());
            loadSampleReceipts();
        }
    }

    private void loadSampleReceipts() {
        Platform.runLater(() -> {
            var sampleReceipts = java.util.Arrays.asList(
                    new Receipt(1L, LocalDate.now(), 1000000, 150000, "Hàng hóa"),
                    new Receipt(2L, LocalDate.now().minusDays(1), 500000, 75000, "Dịch vụ"),
                    new Receipt(3L, LocalDate.now().minusDays(7), 2000000, 300000, "Hàng hóa"),
                    new Receipt(4L, LocalDate.now().minusDays(14), 1500000, 225000, "Dịch vụ"));
            receiptTable.getItems().setAll(sampleReceipts);
            updateFooter(sampleReceipts);
        });
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Quản Lý Hóa Đơn");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Xem và chỉnh sửa các hóa đơn của bạn");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox();
        toolbar.setPadding(new Insets(20, 40, 20, 40));
        toolbar.setSpacing(10);
        toolbar.setStyle("-fx-background-color:white;");

        Label searchLabel = new Label("Tìm kiếm:");
        searchField = new TextField();
        searchField.setPromptText("Nhập mô tả hóa đơn...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-font-size:12; -fx-padding:8;");

        Button addButton = new Button("➕ Thêm Hóa Đơn");
        addButton.setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-font-size:12; -fx-padding:8;");
        addButton.setOnAction(event -> handleAddReceipt());

        Button deleteButton = new Button("🗑️ Xóa");
        deleteButton.setStyle("-fx-background-color:#F44336; -fx-text-fill:white; -fx-font-size:12; -fx-padding:8;");
        deleteButton.setOnAction(event -> handleDeleteReceipt());

        Button refreshButton = new Button("🔄 Làm Mới");
        refreshButton.setStyle("-fx-background-color:#2196F3; -fx-text-fill:white; -fx-font-size:12; -fx-padding:8;");
        refreshButton.setOnAction(event -> loadReceipts());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchLabel, searchField, spacer, addButton, deleteButton, refreshButton);
        return toolbar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setPadding(new Insets(20, 40, 20, 40));

        receiptTable = createReceiptTable();
        VBox.setVgrow(receiptTable, Priority.ALWAYS);

        container.getChildren().add(receiptTable);
        return container;
    }

    private TableView<Receipt> createReceiptTable() {
        TableView<Receipt> table = new TableView<>();

        TableColumn<Receipt, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Receipt, LocalDate> dateCol = new TableColumn<>("Ngày");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Receipt, Long> revenueCol = new TableColumn<>("Doanh Thu");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        revenueCol.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        revenueCol.setPrefWidth(150);

        TableColumn<Receipt, Long> taxCol = new TableColumn<>("Thuế");
        taxCol.setCellValueFactory(new PropertyValueFactory<>("tax"));
        taxCol.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        taxCol.setPrefWidth(100);

        TableColumn<Receipt, String> descCol = new TableColumn<>("Mô Tả");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descCol.setPrefWidth(250);

        table.getColumns().addAll(idCol, dateCol, revenueCol, taxCol, descCol);
        table.setPrefHeight(400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(20, 40, 20, 40));
        footer.setSpacing(30);
        footer.setStyle("-fx-background-color:white;");

        Label totalCountLabelTitle = new Label("Tổng Hóa Đơn:");
        totalCountLabelTitle.setStyle("-fx-font-weight:bold;");
        totalCountLabel = new Label("0");

        Label totalRevenueTitle = new Label("Tổng Doanh Thu:");
        totalRevenueTitle.setStyle("-fx-font-weight:bold; -fx-margin-left:40;");
        totalRevenueLabel = new Label("0 ₫");
        totalRevenueLabel.setStyle("-fx-text-fill:#4CAF50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren()
                .addAll(totalCountLabelTitle, totalCountLabel, totalRevenueTitle, totalRevenueLabel, spacer);

        return footer;
    }

    private void updateFooter(java.util.List<Receipt> receipts) {
        totalCountLabel.setText(String.valueOf(receipts.size()));
        long totalRevenue = receipts.stream().mapToLong(Receipt::getRevenue).sum();
        totalRevenueLabel.setText(String.format("%,d ₫", totalRevenue));
    }

    private void handleAddReceipt() {
        Dialog<Receipt> dialog = new Dialog<>();
        dialog.setTitle("Thêm Hóa Đơn Mới");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker();
        TextField revenueField = new TextField();
        TextField taxField = new TextField();
        TextField descField = new TextField();

        grid.add(new Label("Ngày:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Doanh Thu:"), 0, 1);
        grid.add(revenueField, 1, 1);
        grid.add(new Label("Thuế:"), 0, 2);
        grid.add(taxField, 1, 2);
        grid.add(new Label("Mô Tả:"), 0, 3);
        grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Receipt receipt = new Receipt(
                            null,
                            datePicker.getValue(),
                            Long.parseLong(revenueField.getText()),
                            Long.parseLong(taxField.getText()),
                            descField.getText());
                    receiptRepository.save(receipt);
                    loadReceipts();
                    return receipt;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setContentText("Lỗi khi thêm hóa đơn: " + e.getMessage());
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleDeleteReceipt() {
        Receipt selected = receiptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn hóa đơn để xóa");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác Nhận");
        confirm.setContentText("Bạn có chắc chắn muốn xóa hóa đơn này?");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                        try {
                            receiptRepository.delete(selected);
                            Platform.runLater(this::loadReceipts);
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Lỗi");
                                error.setContentText("Lỗi xóa hóa đơn: " + e.getMessage());
                                error.showAndWait();
                            });
                        }
                    })
                    .start();
        }
    }
}
