package com.example.ui.controller;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CreatePostPopupController {
    // This class will handle the logic for creating a new post
    // It will include methods to validate input, save the post, and close the popup

    
    public Button closeButton;
    
    public void initialize() {
        // Initialization code here
        // import file css style cho popup
        
    }

    public void savePost() {
        // Logic to save the post
    }

    public void closePopup() {
        // Logic to close the popup
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // Log the action
        System.out.println("Popup closed");
        stage.close();
    }

    // Additional methods for validation and other functionalities can be added here

}
