package com.example.ui;

import com.example.ui.router.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.util.Objects;

import utils.FXRouter;

import static com.example.ui.router.SceneManager.primaryStage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setPrimaryStage(stage);
        FXRouter.bind(this, stage);
        FXRouter.when("home", "/fxml/home_view.fxml", 1200, 800);
        FXRouter.when("login", "/fxml/login.fxml", 1200, 800);
        FXRouter.when("profile", "/fxml/profile.fxml", 1200, 800);
        FXRouter.when("register", "/fxml/register.fxml", 1200, 800);

//        System.out.println(getClass().getResource("/fxml/home_view.fxml"));
        FXRouter.goTo("home");

        stage.setTitle("LifeTips");
        stage.show();
    }
}



//public class Main extends Application {
//    @Override
//    public void start(Stage stage) throws Exception {
//        SceneManager.setPrimaryStage(stage);
//        FXRouter.bind(this, stage);
//        FXRouter.when("login", "/fxml/login.fxml");
//        FXRouter.when("home", "/fxml/home_view.fxml");
//        // ScenicView.show(scene);
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/home_view.fxml")));
//            Scene scene = new Scene(root, 1200, 800);
//
//            stage.setTitle("LifeTips");
//            stage.setScene(scene);
//            stage.show();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        // code tạo sẵn file ./com/example/ui/constant/localStorage.txt
//
//        // FXRouter.goTo("home");
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}


