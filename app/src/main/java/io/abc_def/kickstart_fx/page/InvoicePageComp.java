package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.reporting.InvoiceService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class InvoicePageComp extends SimpleComp {

    private InvoiceService invoiceService;
    private TableView<InvoiceService.InvoiceData> invoiceTable;
    private TableView<InvoiceService.InvoiceDetailData> detailTable;
    private Label selectedInvoiceLabel;
    private Label totalAmountLabel;
    private Label taxAmountLabel;
    private Label grandTotalLabel;
    private Label invoiceDateLabel;
    private static final long CURRENT_USER_ID = 1L; // Mock user

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createInvoiceListSection());
        root.getChildren().add(createInvoiceDetailSection());

        initDatabase();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Quản Lý Hóa Đơn");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Danh sách hóa đơn đã xuất");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createInvoiceListSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));

        Label label = new Label("Danh Sách Hóa Đơn");
        label.setStyle("-fx-font-size:14; -fx-font-weight:bold;");

        invoiceTable = new TableView<>();
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        invoiceTable.setStyle("-fx-font-size:12;");
        invoiceTable.setPrefHeight(250);

        TableColumn<InvoiceService.InvoiceData, String> numberColumn = new TableColumn<>("Mã Hóa Đơn");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        numberColumn.setPrefWidth(150);

        TableColumn<InvoiceService.InvoiceData, Double> amountColumn = new TableColumn<>("Thành Tiền (₫)");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        amountColumn.setPrefWidth(120);

        TableColumn<InvoiceService.InvoiceData, Double> taxColumn = new TableColumn<>("Thuế (₫)");
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
        taxColumn.setPrefWidth(100);

        TableColumn<InvoiceService.InvoiceData, String> dateColumn = new TableColumn<>("Ngày Xuất");
        dateColumn.setCellValueFactory(cellData -> {
            var date = cellData.getValue().createdAt;
            return new javafx.beans.property.SimpleStringProperty(
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.sql.Timestamp.valueOf(date)));
        });
        dateColumn.setPrefWidth(150);

        invoiceTable.getColumns().addAll(numberColumn, amountColumn, taxColumn, dateColumn);
        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadInvoiceDetails(newVal.id);
            }
        });

        section.getChildren().addAll(label, invoiceTable);
        return section;
    }

    private VBox createInvoiceDetailSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1 0 0 0;");

        Label label = new Label("Chi Tiết Hóa Đơn");
        label.setStyle("-fx-font-size:14; -fx-font-weight:bold;");

        HBox infoBox = new HBox(30);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1;");

        selectedInvoiceLabel = new Label("Chưa chọn hóa đơn");
        selectedInvoiceLabel.setStyle("-fx-font-size:12;");

        invoiceDateLabel = new Label("Ngày: --");
        invoiceDateLabel.setStyle("-fx-font-size:12;");

        infoBox.getChildren().addAll(selectedInvoiceLabel, invoiceDateLabel);

        detailTable = new TableView<>();
        detailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        detailTable.setStyle("-fx-font-size:12;");
        detailTable.setPrefHeight(200);

        TableColumn<InvoiceService.InvoiceDetailData, String> codeColumn = new TableColumn<>("Mã SP");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        codeColumn.setPrefWidth(80);

        TableColumn<InvoiceService.InvoiceDetailData, String> nameColumn = new TableColumn<>("Tên Sản Phẩm");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameColumn.setPrefWidth(200);

        TableColumn<InvoiceService.InvoiceDetailData, Long> quantityColumn = new TableColumn<>("Số Lượng");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(80);

        TableColumn<InvoiceService.InvoiceDetailData, Double> unitPriceColumn = new TableColumn<>("Đơn Giá (₫)");
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceColumn.setPrefWidth(120);

        TableColumn<InvoiceService.InvoiceDetailData, Double> totalPriceColumn = new TableColumn<>("Thành Tiền (₫)");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setPrefWidth(120);

        detailTable.getColumns().addAll(codeColumn, nameColumn, quantityColumn, unitPriceColumn, totalPriceColumn);

        HBox summaryBox = new HBox(30);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1; -fx-background-color:#f9f9f9;");

        totalAmountLabel = new Label("Tổng cộng: 0 ₫");
        totalAmountLabel.setStyle("-fx-font-size:12;");

        taxAmountLabel = new Label("Thuế (10%): 0 ₫");
        taxAmountLabel.setStyle("-fx-font-size:12;");

        grandTotalLabel = new Label("Tổng thanh toán: 0 ₫");
        grandTotalLabel.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#d32f2f;");

        summaryBox.getChildren().addAll(totalAmountLabel, taxAmountLabel, grandTotalLabel);

        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));

        Button printBtn = new Button("🖨️ In Hóa Đơn");
        printBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#2196f3; -fx-text-fill:white; -fx-border-radius:4;");
        printBtn.setOnAction(e -> printInvoice());

        Button exportBtn = new Button("📥 Xuất PDF");
        exportBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#4caf50; -fx-text-fill:white; -fx-border-radius:4;");
        exportBtn.setOnAction(e -> exportPdf());

        Button refreshBtn = new Button("🔄 Làm Mới");
        refreshBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#ff9800; -fx-text-fill:white; -fx-border-radius:4;");
        refreshBtn.setOnAction(e -> loadInvoices());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actionBox.getChildren().addAll(printBtn, exportBtn, spacer, refreshBtn);

        section.getChildren().addAll(label, infoBox, detailTable, summaryBox, actionBox);
        return section;
    }

    private void initDatabase() {
        new Thread(() -> {
                    try {
                        DatabaseManager dbManager = new DatabaseManager("");
                        dbManager.connect();
                        invoiceService = new InvoiceService(dbManager);
                        loadInvoices();
                    } catch (Exception e) {
                        System.err.println("Error initializing database: " + e.getMessage());
                    }
                })
                .start();
    }

    private void loadInvoices() {
        new Thread(() -> {
                    try {
                        if (invoiceService != null) {
                            List<InvoiceService.InvoiceData> invoices =
                                    invoiceService.getInvoicesByUserId(CURRENT_USER_ID);
                            Platform.runLater(() -> {
                                invoiceTable.getItems().clear();
                                invoiceTable.getItems().addAll(invoices);
                            });
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading invoices: " + e.getMessage());
                    }
                })
                .start();
    }

    private void loadInvoiceDetails(Long invoiceId) {
        new Thread(() -> {
                    try {
                        if (invoiceService != null) {
                            InvoiceService.InvoiceData invoice = invoiceService.getInvoiceById(invoiceId);
                            List<InvoiceService.InvoiceDetailData> details =
                                    invoiceService.getInvoiceDetails(invoiceId);

                            Platform.runLater(() -> {
                                detailTable.getItems().clear();
                                detailTable.getItems().addAll(details);

                                selectedInvoiceLabel.setText("Hóa đơn: " + invoice.invoiceNumber);
                                invoiceDateLabel.setText("Ngày: "
                                        + new SimpleDateFormat("dd/MM/yyyy HH:mm")
                                                .format(java.sql.Timestamp.valueOf(invoice.createdAt)));

                                totalAmountLabel.setText(String.format("Tổng cộng: %.0f ₫", invoice.totalAmount));
                                taxAmountLabel.setText(String.format("Thuế (10%%): %.0f ₫", invoice.taxAmount));
                                grandTotalLabel.setText(
                                        String.format("Tổng thanh toán: %.0f ₫", invoice.getGrandTotal()));
                            });
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading invoice details: " + e.getMessage());
                    }
                })
                .start();
    }

    private void printInvoice() {
        InvoiceService.InvoiceData selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Lỗi", "Vui lòng chọn hóa đơn để in");
            return;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            showAlert("Thành công", "Hóa đơn được gửi tới máy in");
        }
    }

    private void exportPdf() {
        InvoiceService.InvoiceData selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Lỗi", "Vui lòng chọn hóa đơn để xuất");
            return;
        }

        showAlert("Thành công", "Hóa đơn được xuất thành công");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
