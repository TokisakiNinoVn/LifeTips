package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HeaderController {
    @FXML
    public HBox goHome;
    public HBox goLogin;
    public HBox goRegister;
    @FXML
    private HBox addPostLabel;
    @FXML
    private ImageView profileImage;

    public HBox getAddPostLabel() {
        return addPostLabel;
    }

    @FXML
    private void initialize() {
        addPostLabel.setOnMouseClicked(event -> {
            System.out.println("Clicked addPostLabel!");
            openCreatePostPopup();
        });
    }

    private void openCreatePostPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_post_popup.fxml"));
            Parent popupContent = loader.load();

            // Tạo scene và dialog
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
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent profileRoot = loader.load();

            Stage stage = (Stage) profileImage.getScene().getWindow();

            // Set the new scene (profile page)
            Scene scene = new Scene(profileRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void navigateToHome() {
        try {
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home_view.fxml"));
            Parent homeRoot = loader.load();

            Stage stage = (Stage) goHome.getScene().getWindow();

            Scene scene = new Scene(homeRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void navigateToLogin() {
        try {
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent homeRoot = loader.load();

            Stage stage = (Stage) goLogin.getScene().getWindow();

            Scene scene = new Scene(homeRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void navigateToRegister() {
        try {
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent homeRoot = loader.load();

            Stage stage = (Stage) goRegister.getScene().getWindow();

            Scene scene = new Scene(homeRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
