package com.example.crud.FrontOffice;

import com.example.crud.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;

public class FrontOfficeController implements Initializable {
    @FXML
    private BorderPane root;
    
    @FXML
    private VBox welcomeSection;
    
    @FXML
    private HBox featuresSection;
    
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            if (root.getScene() != null) {
                NavigationUtil.setMainLayout(root, "front");
            } else {
                System.err.println("Scene is not yet available");
            }
        });
    }

    @FXML
    private void handleObjets() {
        hideWelcomeAndFeatures();
        NavigationUtil.navigateTo("/com/example/crud/FrontOffice/ObjetList.fxml");
    }

    @FXML
    private void handleAjouterObjet() {
        hideWelcomeAndFeatures();
        NavigationUtil.navigateTo("/com/example/crud/FrontOffice/ObjetForm.fxml");
    }

    @FXML
    private void handleStartSwapping() {
        handleObjets(); // Navigate to objects list
    }

    @FXML
    private void handleBackOffice() {
        System.out.println("Attempting to navigate to Back Office...");
        try {
            NavigationUtil.navigateTo("/com/example/crud/BackOffice/BackOffice.fxml");
            System.out.println("Navigation completed successfully");
        } catch (Exception e) {
            System.err.println("Error navigating to Back Office: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        // If we're in back office, navigate to front office first
        if (!"front".equals(NavigationUtil.getCurrentSection())) {
            NavigationUtil.navigateTo("/com/example/crud/FrontOffice/FrontOffice.fxml");
            return;
        }
        
        // Load home content
        NavigationUtil.navigateTo("/com/example/crud/FrontOffice/HomeContent.fxml");
    }

    public void hideWelcomeAndFeatures() {
        if (welcomeSection != null) {
            welcomeSection.setVisible(false);
            welcomeSection.setManaged(false);
        }
        if (featuresSection != null) {
            featuresSection.setVisible(false);
            featuresSection.setManaged(false);
        }
    }

    private void showWelcomeAndFeatures() {
        if (welcomeSection != null) {
            welcomeSection.setVisible(true);
            welcomeSection.setManaged(true);
        }
        if (featuresSection != null) {
            featuresSection.setVisible(true);
            featuresSection.setManaged(true);
        }
    }
} 