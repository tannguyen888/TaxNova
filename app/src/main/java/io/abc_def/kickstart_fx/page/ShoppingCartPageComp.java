package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.Cart;
import io.abc_def.kickstart_fx.domain.CartItem;
import io.abc_def.kickstart_fx.domain.CartService;
import io.abc_def.kickstart_fx.domain.ProductService;
import io.abc_def.kickstart_fx.persistence.CartRepository;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.ProductRepository;
import io.abc_def.kickstart_fx.reporting.InvoiceService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Optional;

public class ShoppingCartPageComp extends SimpleComp {

    private CartService cartService;
    private ProductService productService;
    private InvoiceService invoiceService;
    private Cart currentCart;
    private TableView<CartItem> cartTable;
    private Label totalItemsLabel;
    private Label totalPriceLabel;
    private Label taxLabel;
    private Label grandTotalLabel;
    private ComboBox<String> productCombo;
    private TextField quantityField;
    private static final long CURRENT_USER_ID = 1L; // Mock user, change as needed

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createAddItemSection());
        root.getChildren().add(createCartTable());
        root.getChildren().add(createSummary());
        root.getChildren().add(createCheckoutSection());

        initDatabase();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Giỏ Hàng");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Quản lý giỏ hàng của bạn");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createAddItemSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:0 0 1 0;");

        Label label = new Label("Thêm Sản Phẩm");
        label.setStyle("-fx-font-size:14; -fx-font-weight:bold;");

        HBox addBox = new HBox(10);

        productCombo = new ComboBox<>();
        productCombo.setPromptText("Chọn sản phẩm");
        productCombo.setPrefWidth(300);
        productCombo.setStyle("-fx-padding:8; -fx-font-size:12;");

        quantityField = new TextField("1");
        quantityField.setPrefWidth(80);
        quantityField.setStyle("-fx-padding:8; -fx-font-size:12;");

        Button addBtn = new Button("Thêm vào giỏ");
        addBtn.setStyle(
                "-fx-padding:8 15; -fx-font-size:12; -fx-background-color:#4caf50; -fx-text-fill:white; -fx-border-radius:4;");
        addBtn.setOnAction(e -> addItemToCart());

        addBox.getChildren().addAll(productCombo, quantityField, addBtn);
        section.getChildren().addAll(label, addBox);
        return section;
    }

    private VBox createCartTable() {
        VBox container = new VBox();
        container.setPadding(new Insets(20));
        container.setSpacing(10);

        Label label = new Label("Danh Sách Sản Phẩm Trong Giỏ");
        label.setStyle("-fx-font-size:14; -fx-font-weight:bold;");

        cartTable = new TableView<>();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setStyle("-fx-font-size:12;");

        TableColumn<CartItem, String> codeColumn = new TableColumn<>("Mã SP");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        codeColumn.setPrefWidth(80);

        TableColumn<CartItem, String> nameColumn = new TableColumn<>("Tên Sản Phẩm");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameColumn.setPrefWidth(200);

        TableColumn<CartItem, Double> priceColumn = new TableColumn<>("Đơn Giá (₫)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceColumn.setPrefWidth(120);

        TableColumn<CartItem, Long> quantityColumn = new TableColumn<>("Số Lượng");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(100);

        TableColumn<CartItem, Double> totalColumn = new TableColumn<>("Thành Tiền (₫)");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalColumn.setPrefWidth(120);

        TableColumn<CartItem, Void> actionColumn = new TableColumn<>("Thao Tác");
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button deleteBtn = new Button("Xóa");

            {
                deleteBtn.setStyle(
                        "-fx-padding:5 10; -fx-font-size:11; -fx-background-color:#f44336; -fx-text-fill:white;");
                deleteBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeItemFromCart(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        cartTable.getColumns().addAll(codeColumn, nameColumn, priceColumn, quantityColumn, totalColumn, actionColumn);

        container.getChildren().addAll(label, cartTable);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    private VBox createSummary() {
        VBox summary = new VBox(10);
        summary.setPadding(new Insets(20));
        summary.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1 0 1 0;");

        HBox row1 = new HBox(20);
        totalItemsLabel = new Label("Tổng mục: 0");
        totalItemsLabel.setStyle("-fx-font-size:12;");
        totalPriceLabel = new Label("Tổng cộng: 0 ₫");
        totalPriceLabel.setStyle("-fx-font-size:12; -fx-font-weight:bold;");
        row1.getChildren().addAll(totalItemsLabel, totalPriceLabel);

        HBox row2 = new HBox(20);
        taxLabel = new Label("Thuế (10%): 0 ₫");
        taxLabel.setStyle("-fx-font-size:12;");
        row2.getChildren().add(taxLabel);

        HBox row3 = new HBox(20);
        grandTotalLabel = new Label("Tổng thanh toán: 0 ₫");
        grandTotalLabel.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#d32f2f;");
        row3.getChildren().add(grandTotalLabel);

        summary.getChildren().addAll(row1, row2, row3);
        return summary;
    }

    private HBox createCheckoutSection() {
        HBox checkout = new HBox(10);
        checkout.setPadding(new Insets(20));
        checkout.setStyle("-fx-border-color:#e0e0e0; -fx-border-width:1 0 0 0;");

        Button clearBtn = new Button("🗑️ Xóa Giỏ");
        clearBtn.setStyle(
                "-fx-padding:10 15; -fx-font-size:12; -fx-background-color:#ff9800; -fx-text-fill:white; -fx-border-radius:4;");
        clearBtn.setOnAction(e -> clearCart());

        Button invoiceBtn = new Button("📄 Xuất Hóa Đơn");
        invoiceBtn.setStyle(
                "-fx-padding:10 15; -fx-font-size:12; -fx-background-color:#4caf50; -fx-text-fill:white; -fx-border-radius:4;");
        invoiceBtn.setOnAction(e -> generateInvoice());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        checkout.getChildren().addAll(clearBtn, spacer, invoiceBtn);
        return checkout;
    }

    private void initDatabase() {
        new Thread(() -> {
                    try {
                        DatabaseManager dbManager = new DatabaseManager("");
                        dbManager.connect();
                        ProductRepository productRepository = new ProductRepository(dbManager);
                        CartRepository cartRepository = new CartRepository(dbManager);
                        productService = new ProductService(productRepository);
                        cartService = new CartService(cartRepository);
                        invoiceService = new InvoiceService(dbManager);

                        loadCart();
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
                    var products = productService.getAllProducts();
                    for (var product : products) {
                        productCombo.getItems().add(product.getCode() + " - " + product.getName());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading products: " + e.getMessage());
            }
        });
    }

    private void loadCart() {
        Platform.runLater(() -> {
            try {
                if (cartService != null) {
                    currentCart = cartService.getOrCreateCart(CURRENT_USER_ID);
                    cartTable.setItems(currentCart.getItems());
                    updateSummary();
                }
            } catch (Exception e) {
                System.err.println("Error loading cart: " + e.getMessage());
            }
        });
    }

    private void addItemToCart() {
        if (currentCart == null || productCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn sản phẩm");
            return;
        }

        try {
            String selectedValue = productCombo.getValue();
            String productCode = selectedValue.split(" - ")[0];
            long quantity = Long.parseLong(quantityField.getText());

            var product = productService.getProductByCode(productCode);
            if (product == null) {
                showAlert("Lỗi", "Sản phẩm không tồn tại");
                return;
            }

            if (!productService.checkAvailability(product.getId(), quantity)) {
                showAlert("Lỗi", "Sản phẩm không đủ số lượng");
                return;
            }

            CartItem item =
                    new CartItem(product.getId(), product.getCode(), product.getName(), product.getPrice(), quantity);

            cartService.addItemToCart(currentCart, item);
            updateSummary();
            quantityField.setText("1");
            showAlert("Thành công", "Thêm sản phẩm vào giỏ thành công");
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Vui lòng nhập số lượng hợp lệ");
        }
    }

    private void removeItemFromCart(CartItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartService.removeItemFromCart(currentCart, item.getId());
            updateSummary();
        }
    }

    private void clearCart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartService.clearCart(currentCart);
            loadCart();
        }
    }

    private void generateInvoice() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            showAlert("Lỗi", "Giỏ hàng trống, không thể xuất hóa đơn");
            return;
        }

        try {
            Long invoiceId = invoiceService.createInvoiceFromCart(currentCart, CURRENT_USER_ID);
            showAlert("Thành công", "Hóa đơn được tạo thành công:\nMã hóa đơn: " + invoiceId);

            // Clear the cart after invoice
            loadCart();
        } catch (Exception e) {
            showAlert("Lỗi", "Lỗi tạo hóa đơn: " + e.getMessage());
        }
    }

    private void updateSummary() {
        if (currentCart == null) return;

        long totalItems = currentCart.getItems().size();
        double totalPrice = currentCart.getTotalPrice();
        double taxAmount = cartService.calculateTax(currentCart, 0.1);
        double grandTotal = cartService.calculateGrandTotal(currentCart, 0.1);

        totalItemsLabel.setText("Tổng mục: " + totalItems);
        totalPriceLabel.setText(String.format("Tổng cộng: %.0f ₫", totalPrice));
        taxLabel.setText(String.format("Thuế (10%%): %.0f ₫", taxAmount));
        grandTotalLabel.setText(String.format("Tổng thanh toán: %.0f ₫", grandTotal));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
