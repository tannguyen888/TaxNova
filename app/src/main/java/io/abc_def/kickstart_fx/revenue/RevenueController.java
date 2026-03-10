package io.abc_def.kickstart_fx.revenue;

import io.abc_def.kickstart_fx.domain.Receipt;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class RevenueController {
    // TODO: @FXML TableView<Receipt> receiptTable
    // TODO: @FXML TableColumn date, amount, tax, category
    // TODO: @FXML TextField amountField
    // TODO: @FXML TextField categoryField
    // TODO: @FXML Label errorLabel
    @FXML
    private TableView<Receipt> receiptTable;

    @FXML
    private TableColumn<Receipt, LocalDate> date;

    @FXML
    private TableColumn<Receipt, Double> amount;

    @FXML
    private TableColumn<Receipt, Double> tax;

    @FXML
    private TableColumn<Receipt, String> category;

    @FXML
    private TextField amountField;

    @FXML
    private TextField categoryField;

    @FXML
    private Label errorLabel;

    // TODO: private final RevenueViewModel viewModel

    private final RevenueViewModel viewModel;

    // TODO: constructor nhận RevenueViewModel
    public RevenueController(RevenueViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // TODO: initialize()
    // → setup các cột TableView
    // → gọi loadReceipts()
    @FXML
    public void initialize() {
        date.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        amount.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        tax.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getTaxAmount()));
        category.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()));
        loadReceipts();
    }

    // TODO: loadReceipts()
    // → gọi viewModel.getAllReceipts()
    // → load vào receiptTable

    private void loadReceipts() {
        var receipts = viewModel.getAllReceipts();
        receiptTable.getItems().setAll(receipts);
    }

    // TODO: addReceipt()
    // → lấy amount và category từ field
    // → validate không được trống
    // → tạo Receipt mới
    // → gọi viewModel.saveReceipt()
    // → reload table

    private void addReceipt() {
        var amountInput = amountField.getText().trim();
        var categoryInput = categoryField.getText().trim();

        if (amountInput.isEmpty() || categoryInput.isEmpty()) {
            errorLabel.setText("Amount và Category không được để trống");
            return;
        }

        Receipt newReceipt = new Receipt();
        newReceipt.setDate(LocalDate.now());
        newReceipt.setAmount(Double.parseDouble(amountInput));
        newReceipt.setCategory(categoryInput);
        viewModel.saveReceipt(newReceipt);

        receiptTable.getItems().add(newReceipt);
        amountField.clear();
        categoryField.clear();
        errorLabel.setText("");
    }

    // TODO: deleteReceipt()
    // → lấy item đang chọn từ receiptTable
    // → nếu không có gì được chọn → hiện lỗi
    // → gọi viewModel.deleteReceipt()
    // → reload table
    public void deleteRecipt() {
        var selectedItem = receiptTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            errorLabel.setText("Vui lòng chọn một receipt để xóa");
            return;
        }
        viewModel.deleteReceipt(selectedItem);
        receiptTable.getItems().remove(selectedItem);
        errorLabel.setText("");
    }
}
