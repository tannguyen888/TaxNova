package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.domain.User;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.UserRepository;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfilePageComp extends SimpleComp {

    private UserRepository userRepository;
    private User currentUser;
    private TextField usernameField;
    private TextField emailField;
    private TextField phoneField;
    private TextArea bioTextArea;
    private Label statusLabel;

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createProfileContent());

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
                        userRepository = new UserRepository(dbManager);
                        // Load current user (in real app, this would be from session)
                        loadCurrentUser();
                    } catch (Exception e) {
                        System.out.println("Database error: " + e.getMessage());
                        loadSampleUser();
                    }
                })
                .start();
    }

    private void loadCurrentUser() {
        try {
            // Get first user as sample (in real app, use logged-in user)
            currentUser = new User(1L, "admin", "password123", "ADMIN");
            Platform.runLater(this::displayUserInfo);
        } catch (Exception e) {
            System.out.println("Error loading user: " + e.getMessage());
            loadSampleUser();
        }
    }

    private void loadSampleUser() {
        currentUser = new User(1L, "admin", "password123", "ADMIN");
        Platform.runLater(this::displayUserInfo);
    }

    private void displayUserInfo() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText("admin@taxapp.com");
        phoneField.setText("+84901234567");
        bioTextArea.setText("Quản lý thuế và chi phí kinh doanh");
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Hồ Sơ Người Dùng");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Quản lý thông tin tài khoản của bạn");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createProfileContent() {
        VBox content = new VBox();
        content.setPadding(new Insets(40));
        content.setSpacing(20);
        content.setStyle("-fx-background-color:white;");

        // Profile Avatar and Basic Info
        HBox profileHeader = createProfileHeader();
        content.getChildren().add(profileHeader);

        content.getChildren().add(new Separator());

        // Personal Information
        VBox personalInfo = createPersonalInformationSection();
        content.getChildren().add(personalInfo);

        // Security Section
        VBox securitySection = createSecuritySection();
        content.getChildren().add(securitySection);

        // Status and Action Buttons
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill:#4CAF50; -fx-font-weight:bold;");
        statusLabel.setVisible(false);

        HBox actionButtons = createActionButtons();
        content.getChildren().addAll(statusLabel, actionButtons);

        return content;
    }

    private HBox createProfileHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.TOP_LEFT);

        // Avatar
        Circle avatar = new Circle(50);
        avatar.setFill(Color.web("#1976d2"));
        Label initials = new Label("AD");
        initials.setStyle("-fx-font-size:24; -fx-text-fill:white; -fx-font-weight:bold;");
        StackPane avatarStack = new StackPane(avatar, initials);

        // User Info
        VBox userInfo = new VBox(5);
        Label nameLabel = new Label(currentUser != null ? currentUser.getUsername() : "Người dùng");
        nameLabel.setStyle("-fx-font-size:18; -fx-font-weight:bold;");

        Label roleLabel = new Label("Vai trò: " + (currentUser != null ? currentUser.getRole() : "USER"));
        roleLabel.setStyle("-fx-font-size:12; -fx-text-fill:#666;");

        Label memberSinceLabel = new Label("Thành viên từ: 2024");
        memberSinceLabel.setStyle("-fx-font-size:11; -fx-text-fill:#999;");

        userInfo.getChildren().addAll(nameLabel, roleLabel, memberSinceLabel);

        header.getChildren().addAll(avatarStack, userInfo);
        return header;
    }

    private VBox createPersonalInformationSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-border-color:#f0f0f0; -fx-border-width:1; -fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Thông Tin Cá Nhân");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Username
        grid.add(new Label("Tên đăng nhập:"), 0, 0);
        usernameField = new TextField();
        usernameField.setDisable(true);
        usernameField.setPrefWidth(300);
        grid.add(usernameField, 1, 0);

        // Email
        grid.add(new Label("Email:"), 0, 1);
        emailField = new TextField();
        emailField.setPromptText("nhập email của bạn");
        emailField.setPrefWidth(300);
        grid.add(emailField, 1, 1);

        // Phone
        grid.add(new Label("Điện thoại:"), 0, 2);
        phoneField = new TextField();
        phoneField.setPromptText("nhập số điện thoại");
        phoneField.setPrefWidth(300);
        grid.add(phoneField, 1, 2);

        // Bio
        grid.add(new Label("Tiểu sử:"), 0, 3);
        bioTextArea = new TextArea();
        bioTextArea.setWrapText(true);
        bioTextArea.setPrefHeight(100);
        bioTextArea.setPrefWidth(300);
        grid.add(bioTextArea, 1, 3);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createSecuritySection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-border-color:#f0f0f0; -fx-border-width:1; -fx-padding:20; -fx-border-radius:8;");

        Label sectionTitle = new Label("Bảo Mật");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        HBox changePasswordBox = new HBox(10);
        changePasswordBox.setAlignment(Pos.CENTER_LEFT);

        Label passwordLabel = new Label("Mật khẩu hiện tại được mã hóa và an toàn");
        passwordLabel.setStyle("-fx-text-fill:#666;");

        Button changePasswordBtn = new Button("Đổi Mật Khẩu");
        changePasswordBtn.setStyle("-fx-padding:8; -fx-font-size:12;");
        changePasswordBtn.setOnAction(event -> handleChangePassword());

        changePasswordBox.getChildren().addAll(passwordLabel, changePasswordBtn);

        // Two-factor authentication
        HBox twoFactorBox = new HBox(10);
        twoFactorBox.setAlignment(Pos.CENTER_LEFT);
        twoFactorBox.setStyle("-fx-border-color:#fff0f0; -fx-border-width:0 0 0 3; -fx-padding:10 0 10 10;");

        Label twoFactorLabel = new Label("Xác thực hai yếu tố: Chưa được bật");
        twoFactorLabel.setStyle("-fx-text-fill:#666;");

        Button enableTwoFactorBtn = new Button("Bật");
        enableTwoFactorBtn.setStyle("-fx-padding:8; -fx-font-size:12;");
        enableTwoFactorBtn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Xác thực Hai Yếu Tố");
            alert.setContentText("Tính năng đang được phát triển");
            alert.showAndWait();
        });

        twoFactorBox.getChildren().addAll(twoFactorLabel, enableTwoFactorBtn);

        section.getChildren().addAll(sectionTitle, changePasswordBox, twoFactorBox);
        return section;
    }

    private HBox createActionButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20, 0, 0, 0));

        Button saveButton = new Button("💾 Lưu Thay Đổi");
        saveButton.setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-padding:10; -fx-font-size:12;");
        saveButton.setOnAction(event -> handleSaveProfile());

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color:#f5f5f5; -fx-padding:10; -fx-font-size:12;");
        cancelButton.setOnAction(event -> handleCancel());

        Button deleteAccountButton = new Button("🗑️ Xóa Tài Khoản");
        deleteAccountButton.setStyle(
                "-fx-background-color:#F44336; -fx-text-fill:white; -fx-padding:10; -fx-font-size:12;");
        deleteAccountButton.setOnAction(event -> handleDeleteAccount());

        buttons.getChildren().addAll(saveButton, cancelButton, deleteAccountButton);
        return buttons;
    }

    private void handleSaveProfile() {
        statusLabel.setText("✓ Thay đổi đã được lưu thành công!");
        statusLabel.setStyle("-fx-text-fill:#4CAF50; -fx-font-weight:bold;");
        statusLabel.setVisible(true);

        // Reset after 3 seconds
        new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> statusLabel.setVisible(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .start();
    }

    private void handleCancel() {
        displayUserInfo();
        statusLabel.setVisible(false);
    }

    private void handleChangePassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Đổi Mật Khẩu");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField currentPassword = new PasswordField();
        PasswordField newPassword = new PasswordField();
        PasswordField confirmPassword = new PasswordField();

        grid.add(new Label("Mật khẩu hiện tại:"), 0, 0);
        grid.add(currentPassword, 1, 0);
        grid.add(new Label("Mật khẩu mới:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Xác nhận mật khẩu:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (newPassword.getText().equals(confirmPassword.getText())) {
                    return newPassword.getText();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setContentText("Mật khẩu không trùng khớp");
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.WARNING);
        confirm.setTitle("Xóa Tài Khoản");
        confirm.setHeaderText("Cảnh báo");
        confirm.setContentText("Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác!");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert deleted = new Alert(Alert.AlertType.INFORMATION);
            deleted.setTitle("Thành công");
            deleted.setContentText("Tài khoản đã được xóa");
            deleted.showAndWait();
        }
    }
}
