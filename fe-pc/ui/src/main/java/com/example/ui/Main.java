package com.example.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.util.Objects;

import utils.FXRouter;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXRouter.bind(this, stage);
        FXRouter.when("login", "/fxml/login.fxml");
        FXRouter.when("home", "/fxml/home_view.fxml");
//        ScenicView.show(scene);
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/home_view.fxml")));
            Scene scene = new Scene(root, 1200, 800);

            stage.setTitle("LifeTips");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        FXRouter.goTo("home");
    }

//    @Override
//    public void start(Stage stage) {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/home_view.fxml")));
//            Scene scene = new Scene(root, 1200, 800);
//
//            stage.setTitle("LifeTips");
//            stage.setScene(scene);
//            stage.show();
//        } catch (NullPointerException e) {
//            System.err.println("❌ Không tìm thấy file FXML: /fxml/home_view.fxml");
//        } catch (Exception e) {
//            System.err.println("❌ Lỗi khi khởi động ứng dụng: " + e.getMessage());
//        }
//    }


    public static void main(String[] args) {
        launch(args);
    }
}
