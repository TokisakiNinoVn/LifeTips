package com.example.ui;

import com.example.ui.router.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import com.example.ui.utils.FXRouter;


public class Main extends Application {
    @Override
    public void start(Stage stage) {
        System.out.println("App starting...");
        try {
            System.out.println("Setting up FXRouter...");
            SceneManager.setPrimaryStage(stage);
            FXRouter.bind(this, stage);

            System.out.println("Routing setup...");
            FXRouter.when("home", "/fxml/home_view.fxml", 1200, 800);
            FXRouter.when("login", "/fxml/login.fxml", 1200, 800);
            FXRouter.when("profile", "/fxml/profile.fxml", 1200, 800);
            FXRouter.when("register", "/fxml/register.fxml", 1200, 800);
            FXRouter.when("noti", "/fxml/list_notification_component.fxml", 1200, 800);

            System.out.println("Going to home...");
            FXRouter.goTo("home");
            System.out.println("Done goTo!");

            stage.setTitle("LifeTips");
            stage.show();
            System.out.println("Stage shown.");
        } catch (Exception e) {
            e.printStackTrace();
            // Thêm dialog để hiện lỗi
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Application Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }

}

