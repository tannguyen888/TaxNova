package io.abc_def.kickstart_fx.login;

import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.UserRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField; // chữ p thường

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final LoginViewModel viewModel;

    public LoginController(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    public void onRegister() {
        String username = userName.getText().trim();
        String password = passwordField.getText().trim();

        try {
            if (viewModel != null) {
                viewModel.setUsername(username);
                viewModel.setPassword(password);
                UserRepository userRepository = viewModel.getUserRepository();
                DatabaseManager dbManager = new DatabaseManager("");
                dbManager.connect();
                AuthService authService = new AuthService(userRepository, dbManager);
                authService.register(username, password);
                errorLabel.setStyle("-fx-text-fill:#4caf50;");
                errorLabel.setText("Đăng ký thành công! Vui lòng đăng nhập");
                errorLabel.setVisible(true);
                userName.clear();
                passwordField.clear();
            }
        } catch (IllegalArgumentException e) {
            errorLabel.setStyle("-fx-text-fill:#d32f2f;");
            errorLabel.setText("Lỗi đăng ký: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void onLogin() {
        String username = userName.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please don't leave blank empty");
            errorLabel.setVisible(true);
            return;
        }

        viewModel.setUsername(username);
        viewModel.setPassword(password);
        boolean success = viewModel.login();

        if (success) {
            System.out.println("✓ Login successful!");
            errorLabel.setStyle("-fx-text-fill:#4caf50;");
            errorLabel.setText("Đăng nhập thành công!");
            errorLabel.setVisible(true);
        } else {
            errorLabel.setStyle("-fx-text-fill:#d32f2f;");
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
        }
    }
}
