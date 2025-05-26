package com.example.ui.controller;

import com.example.ui.helper.EventBus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import java.io.IOException;

import utils.FXRouter;
import com.example.ui.service.StorageService;

public class HeaderController {

    @FXML
    public HBox goHome;
    public HBox goLogin;
    public HBox goRegister;
    public HBox logout;

    @FXML
    private HBox addPostLabel;

    @FXML
    private ImageView profileImage;

    @FXML
    private void initialize() {
        updateUIByLoginStatus();

        EventBus.register(event -> {
            if ("UserLoggedIn".equals(event) || "UserLoggedOut".equals(event)) {
                Platform.runLater(this::updateUIByLoginStatus);
            }
        });
    }

    private void updateUIByLoginStatus() {
        String loginStatus = getLoginStatus();
        boolean loggedIn = loginStatus != null && loginStatus.equals("true");

        if (loggedIn) {
            goLogin.setVisible(false);
            goLogin.setManaged(false);

            goRegister.setVisible(false);
            goRegister.setManaged(false);

            addPostLabel.setVisible(true);
            addPostLabel.setManaged(true);

            profileImage.setVisible(true);
            profileImage.setManaged(true);
        } else {
            goLogin.setVisible(true);
            goLogin.setManaged(true);

            goRegister.setVisible(true);
            goRegister.setManaged(true);

            addPostLabel.setVisible(false);
            addPostLabel.setManaged(false);

            profileImage.setVisible(false);
            profileImage.setManaged(false);
        }
    }

    private void openCreatePostPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_post_popup.fxml"));
            Parent popupContent = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.setScene(new Scene(popupContent, Color.TRANSPARENT));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToProfile() {
        try {
            FXRouter.goTo("profile");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Profile");
        }
    }

    @FXML
    private void navigateToHome() {
        try {
            FXRouter.goTo("home_view");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Home");
        }
    }

    @FXML
    private void navigateToLogin() {
        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Login");
        }
    }

    @FXML
    private void navigateToRegister() {
        try {
            FXRouter.goTo("register");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Register");
        }
    }

    @FXML
    private void handleLogout() {
        // Đăng ký sự kiện người dùng đã đăng xuất
        EventBus.post("UserLoggedOut");

        // Cập nhật giao diện người dùng
        updateUIByLoginStatus();

        // Xóa token và trạng thái đăng nhập
        StorageService.logout();
        // Chuyển hướng về trang đăng nhập
        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Login after logout");
        }
    }

    // Hàm này sẽ được gọi khi người dùng nhấn vào nút "Add Post"
    @FXML
    private void handleAddPost() {
        // Mở popup để tạo bài viết mới
        openCreatePostPopup();
    }

    // Load cái loginStatus bằng hàm getLoginStatus() từ StorageService
    public String getLoginStatus() {
        return StorageService.getStatusLogin();
    }

}
