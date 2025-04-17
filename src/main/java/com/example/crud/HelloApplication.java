package com.example.crud;

import com.example.crud.utils.NavigationUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Start with FrontOffice
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/FrontOffice.fxml"));
        BorderPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        
        // Set up navigation
        NavigationUtil.setMainLayout(root, "front");
        NavigationUtil.setScene(scene);
        
        primaryStage.setTitle("SwapCircle - Échange d'Objets");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}