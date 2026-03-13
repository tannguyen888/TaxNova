package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createSettingsContent());

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Cài Đặt");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Quản lý cài đặt ứng dụng của bạn");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createSettingsContent() {
        VBox content = new VBox();
        content.setPadding(new Insets(40));
        content.setSpacing(20);

        // Appearance Settings
        VBox appearanceSection = createAppearanceSection();
        content.getChildren().add(appearanceSection);

        // Notification Settings
        VBox notificationSection = createNotificationSection();
        content.getChildren().add(notificationSection);

        // Data Settings
        VBox dataSection = createDataSection();
        content.getChildren().add(dataSection);

        // System Settings
        VBox systemSection = createSystemSection();
        content.getChildren().add(systemSection);

        // Save Button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button saveButton = new Button("💾 Lưu Cài Đặt");
        saveButton.setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-padding:10; -fx-font-size:12;");
        saveButton.setOnAction(event -> handleSaveSettings());

        Button resetButton = new Button("Đặt Lại Mặc Định");
        resetButton.setStyle("-fx-background-color:#2196F3; -fx-text-fill:white; -fx-padding:10; -fx-font-size:12;");
        resetButton.setOnAction(event -> handleResetSettings());

        buttonBox.getChildren().addAll(resetButton, saveButton);
        content.getChildren().add(buttonBox);

        return content;
    }

    private VBox createAppearanceSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; -fx-border-width:1; "
                + "-fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Giao Diện");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Theme
        HBox themeBox = new HBox(15);
        Label themeLabel = new Label("Chủ đề:");
        themeLabel.setPrefWidth(150);

        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Sáng", "Tối", "Tự động");
        themeCombo.setValue("Sáng");
        themeCombo.setPrefWidth(200);

        themeBox.getChildren().addAll(themeLabel, themeCombo);

        // Language
        HBox languageBox = new HBox(15);
        Label languageLabel = new Label("Ngôn ngữ:");
        languageLabel.setPrefWidth(150);

        ComboBox<String> languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll("Tiếng Việt", "English", "中文", "日本語");
        languageCombo.setValue("Tiếng Việt");
        languageCombo.setPrefWidth(200);

        languageBox.getChildren().addAll(languageLabel, languageCombo);

        // Font Size
        HBox fontSizeBox = new HBox(15);
        Label fontSizeLabel = new Label("Kích thước chữ:");
        fontSizeLabel.setPrefWidth(150);

        ComboBox<String> fontSizeCombo = new ComboBox<>();
        fontSizeCombo.getItems().addAll("Nhỏ", "Bình thường", "Lớn", "Rất lớn");
        fontSizeCombo.setValue("Bình thường");
        fontSizeCombo.setPrefWidth(200);

        fontSizeBox.getChildren().addAll(fontSizeLabel, fontSizeCombo);

        section.getChildren().addAll(sectionTitle, themeBox, languageBox, fontSizeBox);
        return section;
    }

    private VBox createNotificationSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; -fx-border-width:1; "
                + "-fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Thông Báo");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Email notifications
        CheckBox emailNotifications = new CheckBox("Nhận thông báo qua email");
        emailNotifications.setSelected(true);

        // Desktop notifications
        CheckBox desktopNotifications = new CheckBox("Thông báo trên màn hình");
        desktopNotifications.setSelected(true);

        // Sound notifications
        CheckBox soundNotifications = new CheckBox("Phát âm thanh thông báo");
        soundNotifications.setSelected(false);

        // New receipt notification
        HBox receiptBox = new HBox(15);
        Label receiptLabel = new Label("Thông báo khi có hóa đơn mới:");
        receiptLabel.setPrefWidth(300);

        CheckBox receiptNotification = new CheckBox("Bật");
        receiptNotification.setSelected(true);

        receiptBox.getChildren().addAll(receiptLabel, receiptNotification);

        section.getChildren()
                .addAll(sectionTitle, emailNotifications, desktopNotifications, soundNotifications, receiptBox);
        return section;
    }

    private VBox createDataSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; -fx-border-width:1; "
                + "-fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Dữ Liệu");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Auto backup
        HBox backupBox = new HBox(15);
        Label backupLabel = new Label("Sao lưu tự động:");
        backupLabel.setPrefWidth(200);

        CheckBox autoBackup = new CheckBox("Bật sao lưu hàng ngày");
        autoBackup.setSelected(true);

        backupBox.getChildren().addAll(backupLabel, autoBackup);

        // Data export
        HBox exportBox = new HBox(15);
        Label exportLabel = new Label("Xuất dữ liệu:");
        exportLabel.setPrefWidth(200);

        Button exportButton = new Button("Xuất dữ liệu");
        exportButton.setStyle("-fx-padding:8; -fx-font-size:11;");
        exportButton.setOnAction(event -> handleExportData());

        exportBox.getChildren().addAll(exportLabel, exportButton);

        // Import data
        HBox importBox = new HBox(15);
        Label importLabel = new Label("Nhập dữ liệu:");
        importLabel.setPrefWidth(200);

        Button importButton = new Button("Nhập dữ liệu");
        importButton.setStyle("-fx-padding:8; -fx-font-size:11;");
        importButton.setOnAction(event -> handleImportData());

        importBox.getChildren().addAll(importLabel, importButton);

        // Clear cache
        HBox cacheBox = new HBox(15);
        Label cacheLabel = new Label("Bộ đệm:");
        cacheLabel.setPrefWidth(200);

        Button clearCacheButton = new Button("Xóa bộ đệm");
        clearCacheButton.setStyle("-fx-padding:8; -fx-font-size:11;");
        clearCacheButton.setOnAction(event -> handleClearCache());

        cacheBox.getChildren().addAll(cacheLabel, clearCacheButton);

        section.getChildren().addAll(sectionTitle, backupBox, exportBox, importBox, cacheBox);
        return section;
    }

    private VBox createSystemSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; -fx-border-width:1; "
                + "-fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Hệ Thống");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Check for updates
        HBox updateBox = new HBox(15);
        Label updateLabel = new Label("Phiên bản: 1.0.0");
        updateLabel.setPrefWidth(300);

        Button checkUpdateButton = new Button("Kiểm tra cập nhật");
        checkUpdateButton.setStyle("-fx-padding:8; -fx-font-size:11;");
        checkUpdateButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kiểm Tra Cập Nhật");
            alert.setContentText("Bạn đang sử dụng phiên bản mới nhất!");
            alert.showAndWait();
        });

        updateBox.getChildren().addAll(updateLabel, checkUpdateButton);

        // Debug mode
        HBox debugBox = new HBox(15);
        Label debugLabel = new Label("Chế độ gỡ lỗi:");
        debugLabel.setPrefWidth(300);

        CheckBox debugCheck = new CheckBox("Bật");
        debugCheck.setSelected(false);

        debugBox.getChildren().addAll(debugLabel, debugCheck);

        // Logs
        HBox logsBox = new HBox(15);
        Label logsLabel = new Label("Nhật ký:");
        logsLabel.setPrefWidth(300);

        Button viewLogsButton = new Button("Xem nhật ký");
        viewLogsButton.setStyle("-fx-padding:8; -fx-font-size:11;");
        viewLogsButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nhật Ký");
            alert.setContentText("Xem nhật ký ứng dụng");
            alert.showAndWait();
        });

        logsBox.getChildren().addAll(logsLabel, viewLogsButton);

        section.getChildren().addAll(sectionTitle, updateBox, debugBox, logsBox);
        return section;
    }

    private void handleSaveSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setContentText("✓ Cài đặt đã được lưu thành công!");
        alert.showAndWait();
    }

    private void handleResetSettings() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác Nhận");
        confirm.setContentText("Bạn có chắc chắn muốn đặt lại tất cả cài đặt về mặc định?");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setContentText("Cài đặt đã được đặt lại về mặc định");
            alert.showAndWait();
        }
    }

    private void handleExportData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xuất Dữ Liệu");
        alert.setContentText("Dữ liệu sẽ được xuất thành tệp CSV");
        alert.showAndWait();
    }

    private void handleImportData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nhập Dữ Liệu");
        alert.setContentText("Hãy chọn tệp để nhập dữ liệu");
        alert.showAndWait();
    }

    private void handleClearCache() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác Nhận");
        confirm.setContentText("Bạn có chắc chắn muốn xóa bộ đệm?");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setContentText("Bộ đệm đã được xóa");
            alert.showAndWait();
        }
    }
}
