package com.example.crud.BackOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label clientsCount;
    
    @FXML
    private Label objetsCount;
    
    @FXML
    private Label echangesCount;
    
    @FXML
    private Label ticketsCount;

    @FXML
    private TextField searchField;

    @FXML
    private HBox clientsContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateDashboardStats();
        loadClientCards();
        if (searchField != null) {
            setupSearch();
        }
    }

    private void updateDashboardStats() {
        // TODO: Fetch real data from your database
        if (clientsCount != null) clientsCount.setText("123K");
        if (objetsCount != null) objetsCount.setText("1K");
        if (echangesCount != null) echangesCount.setText("340");
        if (ticketsCount != null) ticketsCount.setText("200");
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // TODO: Implement search functionality
            System.out.println("Searching for: " + newValue);
        });
    }

    private void loadClientCards() {
        if (clientsContainer == null) return;
        
        clientsContainer.getChildren().clear();
        
        // Sample client data - replace with real data from your database
        String[][] clients = {
            {"Yassin Saoud", "yassin.saoud@esprit.tn"},
            {"Ousamma Zemzem", "oussama.zemzem@esprit.tn"},
            {"Omar Hamza", "omar.hamza@esprit.tn"},
            {"Chaima Fraj", "chaima.fraj@esprit.tn"},
            {"Cyrine Mrei", "cyrine.mrei@esprit.tn"}
        };

        for (String[] client : clients) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientCard.fxml"));
                Parent clientCard = loader.load();
                
                // Set the client data
                Label nameLabel = (Label) clientCard.lookup("#clientName");
                Label emailLabel = (Label) clientCard.lookup("#clientEmail");
                
                if (nameLabel != null) nameLabel.setText(client[0]);
                if (emailLabel != null) emailLabel.setText(client[1]);
                
                clientsContainer.getChildren().add(clientCard);
            } catch (IOException e) {
                System.err.println("Error loading client card: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 