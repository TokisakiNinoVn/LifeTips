package com.example.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.FXRouter;

import org.json.JSONArray;
import org.json.JSONObject;
import com.example.ui.service.PostService;
import com.example.ui.service.CategoryService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostDetailsController implements Initializable {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentField;
    @FXML
    private ComboBox<String> categoryComboBox;

    private int postId;
    private ProfileController profileController;
    private VBox mainContainer;

    // Phương thức khởi tạo dữ liệu
    public void initData(int postId) {
        this.postId = postId;
        loadCategories(); // Gọi sau khi postId được gán
        loadPostDetails(); // Gọi sau khi postId được gán
    }

    public void setProfileController(ProfileController profileController) {
        this.profileController = profileController;
    }

    public void setMainContainer(VBox mainContainer) {
        this.mainContainer = mainContainer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (titleField == null || contentField == null || categoryComboBox == null) {
            System.err.println("FXML components not initialized: " +
                    "titleField=" + (titleField == null ? "null" : "ok") +
                    ", contentField=" + (contentField == null ? "null" : "ok") +
                    ", categoryComboBox=" + (categoryComboBox == null ? "null" : "ok"));
            showErrorAlert("Lỗi giao diện: Một hoặc nhiều thành phần FXML không được khởi tạo.");
            return;
        }
        // Không gọi loadPostDetails() và loadCategories() ở đây nữa
    }

    private void loadPostDetails() {
        if (postId <= 0) {
            showErrorAlert("ID bài viết không hợp lệ.");
            return;
        }
        JSONObject response = PostService.getPostById(postId);
        if (response == null || !response.has("data")) {
            showErrorAlert("Không thể tải chi tiết bài viết.");
            return;
        }
        JSONObject postDetails = response.optJSONObject("data");
        titleField.setText(postDetails.optString("title", ""));
        contentField.setText(postDetails.optString("content", ""));
        int categoryId = postDetails.optInt("category_id", 0);
        if (postDetails.has("inforCategoryOfPost")) {
            JSONObject category = postDetails.getJSONObject("inforCategoryOfPost");
            String categoryDisplay = category.optString("name", "") + " (ID: " + categoryId + ")";
            categoryComboBox.setValue(categoryDisplay);
        }
    }

    private void loadCategories() {
        JSONObject response = CategoryService.getAllCategory();
        JSONArray categories = response != null ? response.optJSONArray("data") : null;
        if (categories != null) {
            categoryComboBox.getItems().clear();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                categoryComboBox.getItems().add(category.getString("name") + " (ID: " + category.getInt("id") + ")");
            }
        } else {
            showErrorAlert("Không thể tải danh sách danh mục.");
        }
    }

    @FXML
    private void savePost() {
        if (titleField == null || contentField == null || categoryComboBox == null) {
            showErrorAlert("Lỗi giao diện: Một hoặc nhiều thành phần FXML không được khởi tạo.");
            return;
        }

        JSONObject body = new JSONObject();
        body.put("title", titleField.getText());
        body.put("content", contentField.getText());

        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            String[] parts = selectedCategory.split(" \\(ID: ");
            if (parts.length > 1) {
                String categoryIdStr = parts[1].replace(")", "");
                try {
                    body.put("categoryId", Integer.parseInt(categoryIdStr));
                } catch (NumberFormatException e) {
                    showErrorAlert("ID danh mục không hợp lệ.");
                    return;
                }
            } else {
                showErrorAlert("Vui lòng chọn một danh mục.");
                return;
            }
        } else {
            showErrorAlert("Vui lòng chọn một danh mục.");
            return;
        }
        // gọi API để lưu bài viết
        JSONObject response = PostService.updatePost(postId, body);
        // Alert người dùng nếu có lỗi
        if (response.getString("status").equals("success")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Cập nhật bài viết thành công!");
            alert.showAndWait();
        } else {
            showErrorAlert("Cập nhật bài viết thất bại: " + response.getString("message"));
        }

        // Gọi API update post (đang bị comment)
        // profileController.updatePost(postId, body);
        returnToPostsScreen();
    }

    @FXML
    private void cancel() {
        returnToPostsScreen();
    }

    private void returnToPostsScreen() {
        //dùng FXrouter chuyển về màn hình profile.fxml kiểu redirect
        try {
            FXRouter.goTo("profile");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot navigate to Profile");
            showErrorAlert("Không thể chuyển về" + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}