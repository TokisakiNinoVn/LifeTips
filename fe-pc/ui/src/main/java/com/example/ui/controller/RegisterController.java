package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;
import utils.FXRouter;
import com.example.ui.service.AuthService;

import java.io.IOException;

public class RegisterController {

    @FXML
    private AnchorPane registerContainer;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repasswordField;

    @FXML
    private TextField fullnameField;

    public void toLogin() {
        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể chuyển tới trang đăng nhập.");
        }
    }

    public void toHome() {
        try {
            FXRouter.goTo("home");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể chuyển tới trang chủ.");
        }
    }

    public void sendInfoRegister() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String repassword = repasswordField.getText();
            String fullname = fullnameField.getText().trim();

            // Kiểm tra rỗng
            if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || fullname.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Vui lòng điền đầy đủ thông tin.");
                return;
            }

            // Kiểm tra mật khẩu trùng khớp
            if (!password.equals(repassword)) {
                showAlert(Alert.AlertType.WARNING, "Mật khẩu nhập lại không khớp.");
                return;
            }

            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            body.put("fullname", fullname);

            JSONObject response = AuthService.register(body);
            if (response.getString("status").equalsIgnoreCase("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Bạn đã đăng ký thành công. Vui lòng đăng nhập.");
                toLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Đăng ký thất bại: " + response.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Đã xảy ra lỗi. Vui lòng thử lại sau.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
