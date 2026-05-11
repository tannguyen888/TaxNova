package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Product;
import io.abc_def.kickstart_fx.domain.ProductService;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ProductRepository;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.util.Optional;

public class InventoryPageComp extends SimpleComp {

    private ProductService productService;
    private TableView<Product> productTable;
    private TextField searchField;
    private ComboBox<String> categoryCombo;
    private Label totalProductsLabel;
    private Label totalValueLabel;

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

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Quản Lý Kho");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Quản lý sản phẩm trong kho hàng");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(20));
        toolbar.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:0 0 1 0;");

        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm mã sản phẩm hoặc tên...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-padding:8; -fx-font-size:12;");
        searchField.textProperty().addListener((obs, old, newVal) -> filterProducts());

        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Chọn loại sản phẩm");
        categoryCombo.setPrefWidth(200);
        categoryCombo.setStyle("-fx-padding:8; -fx-font-size:12;");
        categoryCombo.getItems().addAll("Tất cả", "Electronics", "Accessories", "Software");
        categoryCombo.setValue("Tất cả");
        categoryCombo.setOnAction(e -> filterProducts());

        Button addBtn = new Button("➕ Thêm Sản Phẩm");
        addBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#4caf50; -fx-text-fill:white; -fx-border-radius:4;");
        addBtn.setOnAction(e -> showAddProductDialog());

        Button deleteBtn = new Button("🗑️ Xóa");
        deleteBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#f44336; -fx-text-fill:white; -fx-border-radius:4;");
        deleteBtn.setOnAction(e -> deleteSelectedProduct());

        Button editBtn = new Button("✏️ Sửa");
        editBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#2196f3; -fx-text-fill:white; -fx-border-radius:4;");
        editBtn.setOnAction(e -> editSelectedProduct());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchField, categoryCombo, addBtn, editBtn, deleteBtn);
        return toolbar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setPadding(new Insets(20));
        container.setSpacing(10);

        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setStyle("-fx-font-size:12;");

        TableColumn<Product, String> codeColumn = new TableColumn<>("Mã SP");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setPrefWidth(80);

        TableColumn<Product, String> nameColumn = new TableColumn<>("Tên Sản Phẩm");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Loại");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(100);

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Giá (₫)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(120);

        TableColumn<Product, Long> quantityColumn = new TableColumn<>("Số Lượng");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(100);

        productTable.getColumns().addAll(codeColumn, nameColumn, categoryColumn, priceColumn, quantityColumn);

        container.getChildren().add(productTable);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    private HBox createFooter() {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1 0 0 0;");

        totalProductsLabel = new Label("Tổng sản phẩm: 0");
        totalProductsLabel.setStyle("-fx-font-size:12;");

        totalValueLabel = new Label("Tổng giá trị: 0 ₫");
        totalValueLabel.setStyle("-fx-font-size:12;");

        footer.getChildren().addAll(totalProductsLabel, totalValueLabel);
        return footer;
    }

    private void initDatabase() {
        new Thread(() -> {
                    try {
                        DatabaseManager dbManager = new DatabaseManager("");
                        dbManager.connect();
                        ProductRepository productRepository = new ProductRepository(dbManager);
                        productService = new ProductService(productRepository);
                        loadProducts();
                    } catch (Exception e) {
                        System.err.println("Error initializing database: " + e.getMessage());
                    }
                })
                .start();
    }

    private void loadProducts() {
        Platform.runLater(() -> {
            try {
                if (productService != null) {
                    productTable.setItems(productService.getAllProducts());
                    updateFooter();
                }
            } catch (Exception e) {
                System.err.println("Error loading products: " + e.getMessage());
            }
        });
    }

    private void filterProducts() {
        if (productService == null) return;

        String searchText = searchField.getText().toLowerCase();
        String category = categoryCombo.getValue();

        var allProducts = category.equals("Tất cả")
                ? productService.getAllProducts()
                : productService.getProductsByCategory(category);

        var filtered = allProducts.filtered(p -> p.getCode().toLowerCase().contains(searchText)
                || p.getName().toLowerCase().contains(searchText));

        productTable.setItems(filtered);
        updateFooter();
    }

    private void updateFooter() {
        if (productTable.getItems().isEmpty()) {
            totalProductsLabel.setText("Tổng sản phẩm: 0");
            totalValueLabel.setText("Tổng giá trị: 0 ₫");
        } else {
            long totalProducts = productTable.getItems().stream()
                    .mapToLong(Product::getQuantity)
                    .sum();

            double totalValue = productTable.getItems().stream()
                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                    .sum();

            totalProductsLabel.setText("Tổng sản phẩm: " + totalProducts);
            totalValueLabel.setText(String.format("Tổng giá trị: %.0f ₫", totalValue));
        }
    }

    private void showAddProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Thêm Sản Phẩm");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField codeField = new TextField();
        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();
        TextField categoryField = new TextField();

        grid.add(new Label("Mã SP:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Tên SP:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Mô tả:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Giá:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Số lượng:"), 0, 4);
        grid.add(quantityField, 1, 4);
        grid.add(new Label("Loại:"), 0, 5);
        grid.add(categoryField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Product product = new Product(
                            codeField.getText(),
                            nameField.getText(),
                            descField.getText(),
                            Double.parseDouble(priceField.getText()),
                            Long.parseLong(quantityField.getText()),
                            categoryField.getText());
                    return product;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Vui lòng nhập giá trị số hợp lệ");
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                productService.saveProduct(result.get());
                loadProducts();
                showAlert("Thành công", "Thêm sản phẩm thành công");
            } catch (Exception e) {
                showAlert("Lỗi", "Lỗi thêm sản phẩm: " + e.getMessage());
            }
        }
    }

    private void editSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm để sửa");
            return;
        }

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Sửa Sản Phẩm");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(selected.getName());
        TextField descField = new TextField(selected.getDescription());
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));
        TextField quantityField = new TextField(String.valueOf(selected.getQuantity()));
        TextField categoryField = new TextField(selected.getCategory());

        grid.add(new Label("Tên SP:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Mô tả:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Giá:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Số lượng:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Loại:"), 0, 4);
        grid.add(categoryField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    selected.setName(nameField.getText());
                    selected.setDescription(descField.getText());
                    selected.setPrice(Double.parseDouble(priceField.getText()));
                    selected.setQuantity(Long.parseLong(quantityField.getText()));
                    selected.setCategory(categoryField.getText());
                    selected.setUpdatedAt(LocalDateTime.now());
                    return selected;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Vui lòng nhập giá trị số hợp lệ");
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                productService.saveProduct(result.get());
                loadProducts();
                showAlert("Thành công", "Cập nhật sản phẩm thành công");
            } catch (Exception e) {
                showAlert("Lỗi", "Lỗi cập nhật sản phẩm: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm để xóa");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc chắn muốn xóa sản phẩm này?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productService.deleteProduct(selected.getId());
                loadProducts();
                showAlert("Thành công", "Xóa sản phẩm thành công");
            } catch (Exception e) {
                showAlert("Lỗi", "Lỗi xóa sản phẩm: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
