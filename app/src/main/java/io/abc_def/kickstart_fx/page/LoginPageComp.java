package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.login.AuthService;
import io.abc_def.kickstart_fx.login.AuthState;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.UserRepository;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginPageComp extends SimpleComp {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private Button loginButton;
    private Button registerButton;
    private AuthService authService;

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");
        root.setPrefWidth(800);
        root.setPrefHeight(600);
        root.setAlignment(Pos.CENTER);

        // Initialize database and auth service
        initializeAuthService();

        // Create login form
        VBox loginForm = createLoginForm();
        root.getChildren().add(loginForm);

        return root;
    }

    private void initializeAuthService() {
        new Thread(() -> {
                    try {
                        DatabaseManager dbManager = new DatabaseManager("");
                        dbManager.connect();
                        UserRepository userRepository = new UserRepository(dbManager);
                        authService = new AuthService(userRepository, dbManager);
                    } catch (Exception e) {
                        System.out.println("Error initializing auth service: " + e.getMessage());
                        Platform.runLater(() -> {
                            errorLabel.setText("Lỗi kết nối cơ sở dữ liệu");
                            errorLabel.setVisible(true);
                        });
                    }
                })
                .start();
    }

    private VBox createLoginForm() {
        VBox form = new VBox();
        form.setStyle("-fx-background-color:white; -fx-border-color:#e0e0e0; -fx-border-width:1;");
        form.setPadding(new Insets(40));
        form.setSpacing(20);
        form.setMaxWidth(400);
        form.setAlignment(Pos.TOP_CENTER);

        // Header
        Label title = new Label("Đăng Nhập");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1976d2"));

        Label subtitle = new Label("Quản lý chi phí và thuế của bạn");
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web("#666666"));

        // Username field
        Label usernameLabel = new Label("Tên đăng nhập:");
        usernameLabel.setStyle("-fx-font-weight:bold; -fx-font-size:12;");
        usernameField = new TextField();
        usernameField.setPromptText("Nhập tên đăng nhập");
        usernameField.setStyle("-fx-control-inner-background:#f5f5f5; -fx-font-size:12; -fx-padding:10;");
        usernameField.setPrefHeight(40);

        // Password field
        Label passwordLabel = new Label("Mật khẩu:");
        passwordLabel.setStyle("-fx-font-weight:bold; -fx-font-size:12;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setStyle("-fx-control-inner-background:#f5f5f5; -fx-font-size:12; -fx-padding:10;");
        passwordField.setPrefHeight(40);

        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:#d32f2f; -fx-font-size:12;");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);

        // Buttons
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);

        loginButton = new Button("Đăng Nhập");
        loginButton.setPrefWidth(150);
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-background-color:#1976d2; -fx-text-fill:white; -fx-font-size:12; "
                + "-fx-font-weight:bold; -fx-cursor:hand;");
        loginButton.setOnAction(event -> handleLogin());

        registerButton = new Button("Đăng Ký");
        registerButton.setPrefWidth(150);
        registerButton.setPrefHeight(40);
        registerButton.setStyle("-fx-background-color:#ffffff; -fx-text-fill:#1976d2; -fx-border-color:#1976d2; "
                + "-fx-border-width:2; -fx-font-size:12; -fx-font-weight:bold; -fx-cursor:hand;");
        registerButton.setOnAction(event -> handleRegister());

        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Forgot password link
        Hyperlink forgotPassword = new Hyperlink("Quên mật khẩu?");
        forgotPassword.setStyle("-fx-font-size:11;");
        forgotPassword.setOnAction(event -> handleForgotPassword());

        form.getChildren()
                .addAll(
                        title,
                        subtitle,
                        new Separator(),
                        usernameLabel,
                        usernameField,
                        passwordLabel,
                        passwordField,
                        errorLabel,
                        buttonBox,
                        new Region() // Spacer
                        );

        // Add forgot password at the bottom
        VBox bottomSection = new VBox();
        bottomSection.setAlignment(Pos.BOTTOM_RIGHT);
        bottomSection.setSpacing(10);
        bottomSection.setPadding(new Insets(20, 0, 0, 0));
        bottomSection.getChildren().add(forgotPassword);
        form.getChildren().add(bottomSection);

        return form;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập tên đăng nhập và mật khẩu");
            errorLabel.setVisible(true);
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Đang xử lý...");

        new Thread(() -> {
                    try {
                        if (authService != null && authService.authenticate(username, password)) {
                            Platform.runLater(() -> {
                                AuthState.setAuthenticated(true);
                                AuthState.setCurrentUsername(username);
                                System.out.println("✓ Login successful for user: " + username);
                                errorLabel.setVisible(false);

                                // Switch to dashboard after successful login
                                io.abc_def.kickstart_fx.core.AppLayoutModel model =
                                        io.abc_def.kickstart_fx.core.AppLayoutModel.get();
                                if (model != null) {
                                    var dashboardEntry = model.getEntries().stream()
                                            .filter(e -> e.comp() instanceof DashboardPageComp)
                                            .findFirst();
                                    dashboardEntry.ifPresent(
                                            e -> model.getSelected().setValue(e));
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                errorLabel.setText("Sai tên đăng nhập hoặc mật khẩu");
                                errorLabel.setVisible(true);
                                loginButton.setDisable(false);
                                loginButton.setText("Đăng Nhập");
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            errorLabel.setText("Lỗi đăng nhập: " + e.getMessage());
                            errorLabel.setVisible(true);
                            loginButton.setDisable(false);
                            loginButton.setText("Đăng Nhập");
                        });
                    }
                })
                .start();
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập tên đăng nhập và mật khẩu");
            errorLabel.setVisible(true);
            return;
        }

        registerButton.setDisable(true);
        String originalText = registerButton.getText();
        registerButton.setText("Đang xử lý...");

        new Thread(() -> {
                    try {
                        if (authService != null) {
                            authService.register(username, password);
                            Platform.runLater(() -> {
                                errorLabel.setStyle("-fx-text-fill:#4caf50;");
                                errorLabel.setText("Đăng ký thành công! Vui lòng đăng nhập");
                                errorLabel.setVisible(true);
                                usernameField.clear();
                                passwordField.clear();
                                registerButton.setDisable(false);
                                registerButton.setText(originalText);
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            errorLabel.setStyle("-fx-text-fill:#d32f2f;");
                            errorLabel.setText("Lỗi đăng ký: " + e.getMessage());
                            errorLabel.setVisible(true);
                            registerButton.setDisable(false);
                            registerButton.setText(originalText);
                        });
                    }
                })
                .start();
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quên Mật Khẩu");
        alert.setHeaderText("Đặt Lại Mật Khẩu");
        alert.setContentText("Vui lòng liên hệ quản trị viên để đặt lại mật khẩu");
        alert.showAndWait();
    }
}
