package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.ui.service.PostService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import com.example.ui.service.CategoryService;

import static javax.swing.text.DefaultStyledDocument.ElementSpec.ContentType;

public class CreatePostPopupController {
    @FXML
    private TextField postTitle;

    @FXML
    private TextField postDescription;

    @FXML
    private TextArea postContent;

    @FXML
    private Label mediaStatusLabel;

    @FXML
    private ListView<String> categoryListView;

    private List<File> selectedMediaFiles = new ArrayList<>();
    private Map<String, String> categoryNameToIdMap = new HashMap<>(); // Lưu map name -> id

    public Button closeButton;

    public void initialize() {
        System.out.println("CreatePostPopupController initialized");
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
        List<String> selectedCategoryName = Collections
                .singletonList(categoryListView.getSelectionModel().getSelectedItem());

        List<String> categoryId = new ArrayList<>();
        for (String name : selectedCategoryName) {
            String id = categoryNameToIdMap.get(name);
            if (id != null) {
                categoryId.add(id);
            }
        }

        if (title.isEmpty() || content.isEmpty() || categoryId.isEmpty()) {
            System.err.println("Please fill in all fields and select at least one category.");
            return;
        }

        // Gọi service
        String response = PostService.createPost(title, content, categoryId, selectedMediaFiles);
        System.out.println("Server response: " + response); // Server response: {status: 'success',"message":"Post with
                                                            // files created successfully","data":{"postId":24}}

        // Kiểm tra phản hồi từ server: nếu status là 'success' thì hiển thị thông báo
        // thành công và đóng popup
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            if ("success".equals(status)) {
                System.out.println(
                        "Post created successfully with ID: " + jsonResponse.getJSONObject("data").getInt("postId"));
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error processing server response: " + e.getMessage(),
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        System.out.println("Popup closed");
        stage.close();
    }

    public void loadCategories() {
        String json = CategoryService.getAllCategory();
        System.out.println("Loading categories from API: " + json);

        if (json == null) {
            System.err.println("Không lấy được danh mục từ API.");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
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
