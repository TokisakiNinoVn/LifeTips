package com.example.ui.controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.ui.service.PostService;
import com.example.ui.constant.constant;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsController {
    @FXML
    private Label usernameLabel;
    @FXML
    private VBox imagePane;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;
    @FXML
    private VBox rightPanel;
    @FXML
    private Label likesLabel;
    @FXML
    private Label commentsCountLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button shareButton;
    @FXML
    private VBox commentList;
    @FXML
    private ComboBox<String> ratingComboBox;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button submitCommentButton;
    @FXML
    private Button showPopupButton;
    @FXML
    private StackPane popupOverlay;
    @FXML
    private VBox detailedTips;

    private int postId;
    private List<String> imageUrls = new ArrayList<>();
    private int currentImageIndex = 0;

    // Danh sách đánh giá value - key // Măc định là 5 Sao
    private JSONObject ratingValues = new JSONObject()
            .put("1 Sao", 1)
            .put("2 Sao", 2)
            .put("3 Sao", 3)
            .put("4 Sao", 4)
            .put("5 Sao", 5);

    // hàm xử lý format lại cái link ảnh
    private String formatImageUrl(String url) {
        String cleanedUrl = url.replace("\\", "/").replaceFirst("^/?public/?", "");
        return constant.FILE_URL + "/" + cleanedUrl;
    }

    // Nhận postId từ HomeController
    public void setPostId(int postId) {
        this.postId = postId;
        loadPostDetails();
    }

    @FXML
    private void initialize() {
        btnPrev.setOnAction(event -> showPreviousImage());
        btnNext.setOnAction(event -> showNextImage());
        submitCommentButton.setOnAction(event -> submitComment());
        saveButton.setOnAction(event -> savePost());
        shareButton.setOnAction(event -> sharePost());

        ObservableList<String> ratings = FXCollections.observableArrayList(
                new ArrayList<>(ratingValues.keySet()));
        ratingComboBox.setItems(ratings);
        ratingComboBox.setValue("5 Sao");
    }

    // Tải chi tiết bài viết
    private void loadPostDetails() {
        try {
            JSONObject response = PostService.getPostById(postId);
            JSONObject post = response.getJSONObject("data");
            System.out.println("Data post: " + post.toString());

            usernameLabel.setText("By " + post.getJSONObject("user").getString("full_name"));
            likesLabel.setText("♥ " + post.getInt("total_like"));
            commentsCountLabel.setText("💬 " + post.getJSONArray("comments").length());
            updatePopupContent(post);
            loadImages(post.getJSONArray("filesNormal"));
            loadComments(post.getJSONArray("comments"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật nội dung của popup với thông tin chi tiết bài viết
    @FXML
    private void updatePopupContent(JSONObject post) {
        for (var node : detailedTips.getChildren()) {
            if (node instanceof Label && ((Label) node).getText().startsWith("📘")) {
                ((Label) node).setText("📘 " + post.getString("title"));
            } else if (node instanceof Text) {
                String categoryInfo = "";
                if (post.has("inforCategoryOfPost") && !post.isNull("inforCategoryOfPost")) {
                    categoryInfo = "\n\nDanh mục: " + post.getJSONObject("inforCategoryOfPost").getString("name");
                }
                ((Text) node).setText(post.getString("content") + categoryInfo);
            }
        }
    }

    // Tải danh sách hình ảnh
    private void loadImages(JSONArray files) {
        imageUrls.clear();
        for (int i = 0; i < files.length(); i++) {
            String url = files.getJSONObject(i).getString("url");
            url = formatImageUrl(url);
            imageUrls.add(url);
        }

        if (!imageUrls.isEmpty()) {
            imageView.setImage(new Image(imageUrls.get(0)));
            currentImageIndex = 0;
        } else {
            imageView.setImage(null);
        }

        // Vô hiệu hóa nút điều hướng nếu không cần
        btnPrev.setDisable(imageUrls.size() <= 1);
        btnNext.setDisable(imageUrls.size() <= 1);
    }

    // Hiển thị hình ảnh trước đó
    private void showPreviousImage() {
        if (imageUrls.size() > 1) {
            currentImageIndex = (currentImageIndex - 1 + imageUrls.size()) % imageUrls.size();
            imageView.setImage(new Image(imageUrls.get(currentImageIndex)));
        }
    }

    // Hiển thị hình ảnh tiếp theo
    private void showNextImage() {
        if (imageUrls.size() > 1) {
            currentImageIndex = (currentImageIndex + 1) % imageUrls.size();
            imageView.setImage(new Image(imageUrls.get(currentImageIndex)));
        }
    }

    // Xử lý gửi bình luận
    private void submitComment() {
        String comment = commentTextArea.getText().trim();
        String selectedRating = ratingComboBox.getValue();
        int ratingValue = ratingValues.getInt(selectedRating);

        if (!comment.isEmpty() && ratingValue > 0) {
            try {
                JSONObject body = new JSONObject();
                body.put("content", comment);
                body.put("rate", ratingValue);

                // Gọi API để gửi bình luận
                JSONObject response = PostService.createComment(postId, body);
                if (response.getString("status").equals("Success")) {
                    loadPostDetails();
                    commentTextArea.clear();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText("Bình luận đã được gửi");
                    alert.setContentText("Cảm ơn bạn đã gửi bình luận!");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể gửi bình luận");
                alert.setContentText("Đã xảy ra lỗi khi gửi bình luận. Vui lòng thử lại.");
                alert.showAndWait();
            }
        } else {
            // Hiển thị thông báo nếu thiếu thông tin
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thiếu thông tin");
            alert.setHeaderText("Vui lòng nhập đầy đủ thông tin");
            alert.setContentText("Bạn cần nhập nội dung bình luận và chọn đánh giá.");
            alert.showAndWait();
        }
    }

    private void loadComments(JSONArray comments) {
        commentList.getChildren().clear(); // Xóa các bình luận cũ

        for (int i = 0; i < comments.length(); i++) {
            JSONObject comment = comments.getJSONObject(i);

            // Tạo container cho mỗi bình luận
            VBox commentBox = new VBox(5);
            commentBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-border-radius: 5;");

            // Thông tin người bình luận
            HBox userInfo = new HBox(10);
            ImageView avatarView = new ImageView();
            try {
                // Giả sử avatar là URL hình ảnh
                avatarView.setImage(
                        new Image(constant.FILE_URL + "\\avatars\\" + comment.getString("avatar") + ".png"));
            } catch (Exception e) {
                avatarView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/img.png"))));
                System.out.println("Avatar not found, using default image." + e);
            }
            avatarView.setFitHeight(30);
            avatarView.setFitWidth(30);

            Label userName = new Label(comment.getString("full_name"));
            userName.setStyle("-fx-text-fill: black;");

            Label rating = new Label("⭐".repeat(comment.getInt("rate")));
            rating.setStyle("-fx-text-fill: black;");

            userInfo.getChildren().addAll(avatarView, userName, rating);

// Nội dung bình luận
            Label content = new Label(comment.getString("content"));
            content.setStyle("-fx-font-size: 14px; -fx-wrap-text: true; -fx-text-fill: black;");

// Thời gian bình luận
            Label time = new Label(comment.getString("created_at"));
            time.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

            commentBox.getChildren().addAll(userInfo, content, time);
            commentList.getChildren().add(commentBox);

        }
    }

    // Xử lý lưu bài viết
    private void savePost() {
        try {
            JSONObject body = new JSONObject();
            body.put("postId", postId);

            // Gọi API để lưu bài viết
            JSONObject response = PostService.savePost(body);
            if (response.getString("status").equals("success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Bài viết đã được lưu thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể lưu bài viết. Vui lòng thử lại.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Đã xảy ra lỗi khi lưu bài viết. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }

    @FXML
    private void showMoreTips() {
        popupOverlay.setVisible(true);

        // Đặt popup bên dưới trước khi animate
        detailedTips.setTranslateY(300);

        // Tạo hiệu ứng trượt lên
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), detailedTips);
        slideIn.setFromY(300); // từ dưới lên
        slideIn.setToY(0); // về vị trí bình thường
        slideIn.setInterpolator(Interpolator.EASE_OUT);
        slideIn.play();
    }

    @FXML
    private void hideTips() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), detailedTips);
        slideOut.setFromY(0);
        slideOut.setToY(300);
        slideOut.setInterpolator(Interpolator.EASE_IN);
        slideOut.setOnFinished(event -> popupOverlay.setVisible(false));
        slideOut.play();
    }

    // Hàm xử lý khi nhấn shareButton - copy link bài viết vào clipboard
    @FXML
    private void sharePost() {
        String postUrl = constant.BASE_URL + "/public/post/" + postId; // Giả sử đây là URL bài viết
        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(postUrl);
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chia sẻ thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã sao chép liên kết bài viết vào clipboard!");
        alert.showAndWait();
    }
}