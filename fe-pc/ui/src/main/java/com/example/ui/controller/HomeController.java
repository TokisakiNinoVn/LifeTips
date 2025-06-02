package com.example.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

import com.example.ui.service.PostService;
import com.example.ui.service.CategoryService;

public class HomeController {
    @FXML
    private FlowPane postsContainer;

    @FXML
    private FlowPane categoryButtons;
    
    @FXML
    private TextField searchField;
    
    private String currentCategory = "All";
    private String currentSearchTerm = "";

    @FXML
    private void initialize() {
        loadCategories();
        loadPosts();
        
        // Add listener for search field
        searchField.setOnKeyReleased(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleSearch();
            }
        });
    }

    // Load categories from service
    public void loadCategories() {
        try {
            JSONObject response = CategoryService.getAllCategory();
            JSONArray categories = response.getJSONArray("data");
            
            categoryButtons.getChildren().clear();

            // Add "All" button
            Button allButton = new Button("All");
            allButton.getStyleClass().add("category-button");
            allButton.setOnAction(event -> {
                currentCategory = "All";
                filterAndSearchPosts();
            });
            categoryButtons.getChildren().add(allButton);

            // Add category buttons
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                Button categoryButton = new Button(category.getString("name"));
                categoryButton.getStyleClass().add("category-button");
                categoryButton.setOnAction(event -> {
                    currentCategory = category.getString("name");
                    filterAndSearchPosts();
                });
                categoryButtons.getChildren().add(categoryButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load all posts
    public void loadPosts() {
        try {
            JSONObject response = PostService.getPosts();
            displayPosts(response.getJSONArray("data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Display posts based on current filter and search term
    private void filterAndSearchPosts() {
        try {
            JSONObject response = PostService.getPosts();
            JSONArray allPosts = response.getJSONArray("data");
            JSONArray filteredPosts = new JSONArray();

            for (int i = 0; i < allPosts.length(); i++) {
                JSONObject post = allPosts.getJSONObject(i);
                String postCategory = post.getJSONObject("category").getString("name");
                String title = post.getString("title").toLowerCase();
                String content = post.getString("content").toLowerCase();
                
                // Check category filter
                boolean categoryMatches = currentCategory.equals("All") || 
                                        postCategory.equals(currentCategory);
                
                // Check search term
                boolean searchMatches = currentSearchTerm.isEmpty() || 
                                       title.contains(currentSearchTerm) || 
                                       content.contains(currentSearchTerm);
                
                if (categoryMatches && searchMatches) {
                    filteredPosts.put(post);
                }
            }
            
            displayPosts(filteredPosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Display posts in the UI
    private void displayPosts(JSONArray posts) {
        postsContainer.getChildren().clear();

        for (int i = 0; i < posts.length(); i++) {
            JSONObject post = posts.getJSONObject(i);
            VBox tipCard = createTipCard(post);
            postsContainer.getChildren().add(tipCard);
        }
    }

    // Create a post-card
    private VBox createTipCard(JSONObject post) {
        VBox tipCard = new VBox(10);
        tipCard.getStyleClass().add("tip-card");

        String categoryName = post.getJSONObject("category").getString("name");
        String title = post.getString("title");
        String content = post.getString("content");
        String author = post.getJSONObject("user").getString("full_name");

        Label categoryLabel = new Label(categoryName);
        categoryLabel.getStyleClass().add("tip-category");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("tip-title");

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        Label authorLabel = new Label("By " + author);
        authorLabel.getStyleClass().add("tip-author");

        HBox statsBox = new HBox(10);

        tipCard.setOnMouseClicked(event -> goDetailsPost(post.getInt("id")));
        tipCard.getChildren().addAll(categoryLabel, titleLabel, contentLabel, authorLabel, statsBox);

        return tipCard;
    }

    // Handle search button click
    @FXML
    private void handleSearch() {
        currentSearchTerm = searchField.getText().toLowerCase();
        filterAndSearchPosts();
    }

    // Navigate to post details
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

    public void handleReload() {
        // Reload categories and posts
        loadCategories();
        loadPosts();
    }
}