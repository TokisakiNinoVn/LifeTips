package com.example.ui.controller;

//import com.example.ui.router.SceneManager;
import com.example.ui.helper.EventBus;
import com.example.ui.service.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import com.example.ui.service.StorageService;
import utils.FXRouter;

import java.io.IOException;

public class LoginController {

    @FXML
    public AnchorPane loginContainer;

    @FXML
    public Label errorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        errorLabel.setText(""); // Clear lỗi cũ
        errorLabel.setStyle("-fx-text-fill: red;");

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ Email và Mật khẩu.");
            return;
        }

        loginButton.setDisable(true); // Tránh spam

        new Thread(() -> {
            try {
                String response = AuthService.login(email, password);
                System.out.println("Login response: " + response);
                String token = response.split("\"token\":\"")[1].split("\"")[0];
                String rawStatus = response.split("\"status\":\"")[1].split("\"")[0];
                if (rawStatus.equals("success")) {
//                    System.out.println("Đăng nhập thành công.");
                    StorageService.saveToken(token);
                    StorageService.setStatusLogin("true");
                    EventBus.post("UserLoggedIn");
                    Platform.runLater(() -> {
                        try {
                            FXRouter.goTo("home");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else {
                    Platform.runLater(() -> {
                        errorLabel.setText("Đăng nhập thất bại. Vui lòng kiểm tra lại.");
                        loginButton.setDisable(false);
                    });
                    System.out.println("Đăng nhập không thành công.");
                    return;
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText("Đăng nhập thất bại. Vui lòng kiểm tra lại.");
                    loginButton.setDisable(false);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        errorLabel.setText("Tính năng quên mật khẩu chưa được hỗ trợ.");
    }

    @FXML
    public void handleSignUp(ActionEvent actionEvent) {
        errorLabel.setText("Tính năng đăng ký chưa được hỗ trợ.");
    }
}
