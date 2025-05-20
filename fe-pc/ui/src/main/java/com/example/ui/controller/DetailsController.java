package com.example.ui.controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public class DetailsController {
    private GaussianBlur blur = new GaussianBlur(10);
    public Label usernameLabel;
    public VBox imagePane;
    public ImageView imageView;
    public Button btnPrev;
    public Button btnNext;
    public VBox rightPanel;
    public Label likesLabel;
    public Label commentsCountLabel;
    public Button saveButton;
    public VBox commentList;
    public ComboBox ratingComboBox;
    public TextArea commentTextArea;
    public Button submitCommentButton;
    public Button showPopupButton;
    public StackPane popupOverlay;
    @FXML
    private VBox detailedTips;

    @FXML
    private void showMoreTips() {
        popupOverlay.setVisible(true);

        // Đặt popup bên dưới trước khi animate
        detailedTips.setTranslateY(300); // Bắt đầu từ dưới

        // Tạo hiệu ứng trượt lên
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), detailedTips);
        slideIn.setFromY(300); // từ dưới lên
        slideIn.setToY(0);     // về vị trí bình thường
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

}