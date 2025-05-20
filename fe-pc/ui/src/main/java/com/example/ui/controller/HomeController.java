package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HomeController {
    public HBox goDetailsPost;
//    public Label addPostLabel;

    @FXML
    private void initialize() {
    }


    public void goDetailsPost(MouseEvent mouseEvent) {
        try {
            // Load the profile.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detail_post.fxml"));
            Parent detailPostRoot = loader.load();

            Stage stage = (Stage) goDetailsPost.getScene().getWindow();

            Scene scene = new Scene(detailPostRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
