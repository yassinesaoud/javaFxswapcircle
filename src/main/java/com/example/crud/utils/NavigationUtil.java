package com.example.crud.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;

public class NavigationUtil {
    private static BorderPane mainLayout;
    private static String currentSection; // "front" or "back"
    private static Scene mainScene;

    public static void setMainLayout(BorderPane layout, String section) {
        if (layout == null) {
            System.err.println("Warning: Attempting to set null layout");
            return;
        }
        
        mainLayout = layout;
        currentSection = section;
        
        // Only try to get scene if layout is already attached to scene
        if (layout.getScene() != null) {
            mainScene = layout.getScene();
        }
    }

    public static void setScene(Scene scene) {
        mainScene = scene;
    }

    public static void navigateTo(String fxmlPath) {
        System.out.println("Attempting to navigate to: " + fxmlPath);
        
        try {
            if (mainLayout == null || mainScene == null) {
                System.err.println("Error: Navigation not properly initialized");
                return;
            }

            URL resource = NavigationUtil.class.getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Error: Could not find resource at path: " + fxmlPath);
                return;
            }

            System.out.println("Loading FXML from: " + resource.toString());
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            
            if (fxmlPath.contains("/BackOffice/") && "front".equals(currentSection)) {
                mainScene.setRoot(view);
                if (view instanceof BorderPane) {
                    mainLayout = (BorderPane) view;
                    currentSection = "back";
                }
            } else if (fxmlPath.contains("/FrontOffice/") && "back".equals(currentSection)) {
                mainScene.setRoot(view);
                if (view instanceof BorderPane) {
                    mainLayout = (BorderPane) view;
                    currentSection = "front";
                }
            } else {
                mainLayout.setCenter(view);
            }
            
            System.out.println("Navigation successful to " + currentSection);
            
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static <T> T loadController(String fxmlPath, Pane targetPane) {
        try {
            URL resource = NavigationUtil.class.getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Error: Could not find resource at path: " + fxmlPath);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            targetPane.getChildren().setAll(view);
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Error loading controller: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentSection() {
        return currentSection;
    }
} 