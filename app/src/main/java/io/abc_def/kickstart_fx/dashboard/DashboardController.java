package io.abc_def.kickstart_fx.dashboard;

import io.abc_def.kickstart_fx.domain.Receipt;
import io.abc_def.kickstart_fx.reporting.PdfExportService;
import io.abc_def.kickstart_fx.revenue.RevenueViewModel;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label totalTaxLabel;
    @FXML
    private Label totalReceiptsLabel;
    @FXML
    private TableView<Receipt> receiptTable;
    @FXML
    private TableColumn<Receipt, String> dateColumn;
    @FXML
    private TableColumn<Receipt, Double> amountColumn;
    @FXML
    private TableColumn<Receipt, Double> taxColumn;
    @FXML
    private TableColumn<Receipt, String> categoryColumn;

    private final RevenueViewModel revenueViewModel;
    private final PdfExportService pdfExportService;

    public DashboardController(RevenueViewModel revenueViewModel, PdfExportService pdfExportService) {
        this.revenueViewModel = revenueViewModel;
        this.pdfExportService = pdfExportService;
    }

    @FXML
    public void initialize() {
        // Setup các cột TableView
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Load dữ liệu
        loadSummary();
    }

    @FXML
    public void loadSummary() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Hiển thị tổng doanh thu
        totalRevenueLabel.setText(formatter.format(revenueViewModel.getTotalRevenue()));

        // Hiển thị tổng thuế
        totalTaxLabel.setText(formatter.format(revenueViewModel.getTotalTax()));

        // Hiển thị số lượng hóa đơn
        var receipts = revenueViewModel.getAllReceipts();
        totalReceiptsLabel.setText(receipts.size() + " hóa đơn");

        // Load vào bảng
        receiptTable.getItems().setAll(receipts);
    }

    @FXML
    public void exportReport() {
        var receipts = revenueViewModel.getAllReceipts();
        pdfExportService.exportRevenueReport(receipts);

        // Thông báo thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xuất báo cáo");
        alert.setContentText("Xuất PDF thành công!");
        alert.showAndWait();
    }

}