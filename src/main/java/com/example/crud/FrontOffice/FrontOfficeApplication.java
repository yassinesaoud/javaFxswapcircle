package com.example.crud.FrontOffice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FrontOfficeApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/FrontOffice.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("SwapCircle - Échange d'Objets");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
} 