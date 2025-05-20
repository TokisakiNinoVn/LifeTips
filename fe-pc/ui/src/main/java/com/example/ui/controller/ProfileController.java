package com.example.ui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    public void loadProfileScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Profile.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Hồ sơ người dùng");
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();

        } catch (IOException e) {
            System.out.println("Lỗi khi tải màn hình Profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
