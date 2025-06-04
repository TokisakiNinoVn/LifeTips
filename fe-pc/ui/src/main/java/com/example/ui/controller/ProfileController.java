package com.example.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.example.ui.service.*;

public class ProfileController {
    @FXML
    private Label emailLabel;
    @FXML
    private TextField fullName;
    @FXML
    private TabPane profileTabPane;
    @FXML
    private Tab myTipsTab;
    @FXML
    private Tab savedTab;
    @FXML
    private VBox postsContainer;
    @FXML
    private VBox filterOptions;
    @FXML
    private ToggleButton publicToggle;
    @FXML
    private ToggleButton privateToggle;
    @FXML
    private Button reloadButton;

    private boolean showPublic = true;
    private boolean showPrivate = true;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        loadUserInfo();
        setupTabListener();
        loadPostsScreen();

        publicToggle.setOnAction(event -> applyFilter());
        privateToggle.setOnAction(event -> applyFilter());
    }

    private void setupTabListener() {
        profileTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            postsContainer.getChildren().clear();
            filterOptions.setVisible(newTab == myTipsTab);
            if (newTab == myTipsTab) {
                loadPostsScreen();
            } else if (newTab == savedTab) {
                loadSavedPostsScreen();
            }
        });
    }

    public void loadUserInfo() {
        JSONObject response = UserService.getInforUserService();
        if (response != null && response.has("data")) {
            JSONObject userData = response.getJSONObject("data");
            emailLabel.setText(userData.optString("email", ""));
            fullName.setText(userData.optString("full_name", ""));
        }
    }

    public void loadPostsScreen() {
        postsContainer.getChildren().clear();
        JSONObject response = PostService.getUserPostsService();
        if (response != null && response.has("data")) {
            JSONArray posts = response.getJSONArray("data");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                if (shouldDisplayPost(post)) {
                    postsContainer.getChildren().add(createPostNode(post, false));
                }
            }
        }
    }

    public void loadSavedPostsScreen() {
        postsContainer.getChildren().clear();
        JSONObject response = PostService.getSavedPosts();
        if (response != null && response.has("data")) {
            JSONArray posts = response.getJSONArray("data");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                postsContainer.getChildren().add(createPostNode(post, true));
            }
        }
    }

    private boolean shouldDisplayPost(JSONObject post) {
        boolean isPrivate = post.getInt("is_private") == 1;
        return (showPublic && !isPrivate) || (showPrivate && isPrivate);
    }

    private VBox createPostNode(JSONObject post, boolean isSaved) {
        VBox postNode = new VBox(10);
        postNode.setStyle(
                "-fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-background-radius: 10;");

        int postId = post.getInt("id");
        Label titleLabel = new Label("Tiêu đề: " + post.getString("title"));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label contentLabel = new Label("Nội dung: " + post.getString("content"));
        contentLabel.setStyle("-fx-font-size: 14px;");

        Label categoryLabel = new Label("Danh mục ID: " + post.getInt("category_id"));
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label createdAtLabel = new Label("Ngày tạo: " +
                java.time.ZonedDateTime.parse(post.getString("created_at")).format(DATE_FORMATTER));
        createdAtLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label privacyLabel = new Label(post.getInt("is_private") == 1 ? "Riêng tư" : "Công khai");
        privacyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label likesLabel = new Label("Lượt thích: " + post.getInt("total_like"));
        likesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        VBox filesContainer = new VBox(5);
        if (post.has("filesNormal") && post.getJSONArray("filesNormal").length() > 0) {
            JSONArray files = post.getJSONArray("filesNormal");
            for (int i = 0; i < files.length(); i++) {
                JSONObject file = files.getJSONObject(i);
                String fileUrl = file.getString("url");
                Label fileLabel = new Label("Tệp: " + fileUrl);
                fileLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #0078d7;");
                filesContainer.getChildren().add(fileLabel);
            }
        } else {
            filesContainer.getChildren().add(new Label("Không có tệp đính kèm"));
        }

        HBox actionsBox = new HBox(10);
        actionsBox.setStyle("-fx-padding: 10 0 0 0;");
        Button detailsButton = new Button("Xem chi tiết");
        detailsButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-background-radius: 5;");
        detailsButton.setOnAction(e -> goDetailsPost(postId));

        if (!isSaved) {
            Button updateButton = new Button("Cập nhật");
            updateButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-background-radius: 5;");
            updateButton.setOnAction(e -> goUpdatePost(postId));

            Button deleteButton = new Button("Xóa");
            deleteButton.setStyle("-fx-background-color: #d83b01; -fx-text-fill: white; -fx-background-radius: 5;");
            deleteButton.setOnAction(e -> confirmDeletePost(postId));

            actionsBox.getChildren().addAll(detailsButton, updateButton, deleteButton);
        } else {
            Button unsaveButton = new Button("Bỏ lưu");
            unsaveButton.setStyle("-fx-background-color: #d83b01; -fx-text-fill: white; -fx-background-radius: 5;");
            unsaveButton.setOnAction(e -> unsavePost(postId));
            actionsBox.getChildren().addAll(detailsButton, unsaveButton);
            if (post.has("user")) {
                JSONObject user = post.getJSONObject("user");
                Label authorLabel = new Label("Tác giả: " + user.getString("full_name"));
                authorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                postNode.getChildren().add(authorLabel);
            }
        }

        postNode.getChildren().addAll(titleLabel, contentLabel, categoryLabel, createdAtLabel,
                privacyLabel, likesLabel, filesContainer, actionsBox);
        return postNode;
    }

    @FXML
    private void applyFilter() {
        showPublic = publicToggle.isSelected();
        showPrivate = privateToggle.isSelected();
        loadPostsScreen();
    }

    @FXML
    private void resetFilter() {
        publicToggle.setSelected(true);
        privateToggle.setSelected(true);
        applyFilter();
    }

    @FXML
    private void reloadData() {
        postsContainer.getChildren().clear();
        loadUserInfo();
        if (profileTabPane.getSelectionModel().getSelectedItem() == myTipsTab) {
            loadPostsScreen();
        } else {
            loadSavedPostsScreen();
        }
    }

    private void confirmDeletePost(int postId) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa bài viết này?");
        confirmAlert.setContentText("Hành động này không thể hoàn tác.");

        ButtonType confirmButton = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                deletePost(postId);
            }
        });
    }

    public void deletePost(int postId) {
        JSONObject response = PostService.deletePost(postId);
        if (response.getString("status").equals("success")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Bài viết đã được xóa thành công.");
            alert.showAndWait();
            loadPostsScreen();
        } else {
            showErrorAlert("Lỗi khi xóa bài viết: " + response.getString("message"));
        }
    }

    public void unsavePost(int postId) {
        JSONObject response = PostService.unsavePost(postId);
        if (response.getString("status").equals("success")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Đã bỏ lưu bài viết thành công.");
            alert.showAndWait();
            loadSavedPostsScreen();
        } else {
            showErrorAlert("Lỗi khi bỏ lưu bài viết: " + response.getString("message"));
        }
    }

    public void updateUser() {
        JSONObject body = new JSONObject();
        body.put("fullName", fullName.getText());
        JSONObject response = UserService.updateUserService(body);
        if (response.getString("status").equals("success")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Thông tin người dùng đã được cập nhật.");
            alert.showAndWait();
        } else {
            showErrorAlert("Lỗi khi cập nhật thông tin: " + response.getString("message"));
        }
    }

    // Navigate to post details
    private void goUpdatePost(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/post_details.fxml"));
            Parent updatePostRoot = loader.load();

            PostDetailsController controller = loader.getController();
            controller.initData(postId);

            Stage stage = (Stage) postsContainer.getScene().getWindow();
            stage.setScene(new Scene(updatePostRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goDetailsPost(int postId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detail_post.fxml"));
            Parent detailPostRoot = loader.load();

            DetailsController controller = loader.getController();
            controller.setPostId(postId);

            Stage stage = (Stage) postsContainer.getScene().getWindow();
            stage.setScene(new Scene(detailPostRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateProfile(ActionEvent actionEvent) {
        updateUser();
    }
}