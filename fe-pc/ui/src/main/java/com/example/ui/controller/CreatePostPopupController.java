package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.*;

import com.example.ui.service.PostService;
import com.example.ui.service.CategoryService;

public class CreatePostPopupController {
    @FXML
    private TextField postTitle;

    @FXML
    private ChoiceBox<String> visibilityChoiceBox;

    @FXML
    private TextArea postContent;

    @FXML
    private Label mediaStatusLabel;

    @FXML
    private ListView<String> categoryListView;

    private List<File> selectedMediaFiles = new ArrayList<>();
    private Map<String, String> categoryNameToIdMap = new HashMap<>();

    public Button closeButton;

    public void initialize() {
        visibilityChoiceBox.setValue("Công khai");
        loadCategories();
    }

    @FXML
    private void uploadMedia() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select up to 5 media files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images & Videos", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.mp4",
                        "*.mov", "*.avi"));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null && files.size() <= 5) {
            selectedMediaFiles.clear();
            selectedMediaFiles.addAll(files);
            mediaStatusLabel.setText(files.size() + " file(s) selected");
        } else {
            mediaStatusLabel.setText("Please select up to 5 files only.");
        }
    }

    @FXML
    private void savePost() {
        String title = postTitle.getText();
        String content = postContent.getText();
        String visibility = visibilityChoiceBox.getValue();
        String selectedCategoryName = categoryListView.getSelectionModel().getSelectedItem();

        if (title.isEmpty() || content.isEmpty() || selectedCategoryName == null) {
            System.err.println("Please fill in all fields and select a category.");
            return;
        }

        String categoryIdStr = categoryNameToIdMap.get(selectedCategoryName);
        if (categoryIdStr == null) {
            System.err.println("Invalid category selected.");
            return;
        }

        int categoryId = Integer.parseInt(categoryIdStr);
        int visibilityPost = "Công khai".equals(visibility) ? 0 : 1;

        // Gọi service
        String response = PostService.createPost(title, content, categoryId, selectedMediaFiles, visibilityPost);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            if ("success".equals(status)) {
                System.out.println("Post created successfully with ID: " +
                        jsonResponse.getJSONObject("data").getInt("postId"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Post created successfully!", ButtonType.OK);
                alert.showAndWait();
                closePopup();
            } else {
                System.err.println("Failed to create post: " + jsonResponse.getString("message"));
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Failed to create post: " + jsonResponse.getString("message"), ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Error processing server response: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        System.out.println("Popup closed");
        stage.close();
    }

    public void loadCategories() {
        JSONObject jsonObject = CategoryService.getAllCategory();

        if (jsonObject == null) {
            System.err.println("Không lấy được danh mục từ API.");
            return;
        }

        try {
            JSONArray dataArray = jsonObject.getJSONArray("data");

            List<String> categoryNames = new ArrayList<>();
            categoryNameToIdMap.clear();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject categoryObj = dataArray.getJSONObject(i);
                int id = categoryObj.getInt("id");
                String name = categoryObj.getString("name");

                categoryNames.add(name);
                categoryNameToIdMap.put(name, String.valueOf(id)); // map name -> id
            }

            categoryListView.getItems().clear();
            categoryListView.getItems().addAll(categoryNames);
            categoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            // System.out.println("Danh mục đã load: " + categoryNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
