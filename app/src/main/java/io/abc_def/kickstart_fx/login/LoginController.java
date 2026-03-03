package io.abc_def.kickstart_fx.login;

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
    public void onLogin() {
        String username = userName.getText().trim();
        String password = passwordField.getText().trim(); // chữ p thường

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please don't leave blank empty");
            errorLabel.setVisible(true);
            return;
        }

        viewModel.setUsername(username);
        viewModel.setPassword(password);
        boolean success = viewModel.login();

        if (success) {
            System.out.println("Đăng nhập thành công!");
            // TODO: chuyển sang Dashboard
        } else {
            errorLabel.setText("Sai tài khoản hoặc mật khẩu!");
            errorLabel.setVisible(true);
        }
    }
}
