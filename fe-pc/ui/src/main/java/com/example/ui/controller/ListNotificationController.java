package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.ui.service.NotificationService;

import java.io.IOException;

public class ListNotificationController {

    @FXML
    private ListView<JSONObject> notificationListView;

    // Initialize the controller
    @FXML
    public void initialize() {
        // Configure ListView to display notifications
        notificationListView.setCellFactory(listView -> new ListCell<JSONObject>() {
            @Override
            protected void updateItem(JSONObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getString("title") + ": " + item.getString("content"));
                }
            }
        });

        // Handle click on notification
        notificationListView.setOnMouseClicked(event -> {
            JSONObject selectedNotification = notificationListView.getSelectionModel().getSelectedItem();
            if (selectedNotification != null) {
                int postId = selectedNotification.getInt("post_id");
                goDetailsPost(postId);
            }
        });

        // Load notifications
        loadListNotification();
    }

    // Navigate to post details
    private void goDetailsPost(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detail_post.fxml"));
            Parent detailPostRoot = loader.load();

            DetailsController controller = loader.getController();
            controller.setPostId(postId);

            Stage stage = (Stage) notificationListView.getScene().getWindow();
            stage.setScene(new Scene(detailPostRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load list notification
    public void loadListNotification() {
        JSONObject response = NotificationService.getAllNotificationService();
        JSONArray listNotification = response.getJSONArray("data");
        notificationListView.getItems().clear();
        for (int i = 0; i < listNotification.length(); i++) {
            notificationListView.getItems().add(listNotification.getJSONObject(i));
        }
    }
}