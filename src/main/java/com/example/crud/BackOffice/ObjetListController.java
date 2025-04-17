package com.example.crud.BackOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class ObjetListController implements Initializable {
    @FXML
    private TableView<Objet> objetTable;

    @FXML
    private TableColumn<Objet, Integer> idColumn;

    @FXML
    private TableColumn<Objet, String> imageColumn;

    @FXML
    private TableColumn<Objet, String> nomColumn;

    @FXML
    private TableColumn<Objet, String> descriptionColumn;

    @FXML
    private TableColumn<Objet, String> etatColumn;

    @FXML
    private TableColumn<Objet, LocalDateTime> dateAjoutColumn;

    @FXML
    private TableColumn<Objet, String> categorieColumn;

    @FXML
    private TableColumn<Objet, Void> actionsColumn;

    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idObjet"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        dateAjoutColumn.setCellValueFactory(new PropertyValueFactory<>("dateAjout"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        
        // Set up image column
        setupImageColumn();
        
        setupActionsColumn();
        
        // Load data from database
        loadObjets();
    }

    private void setupImageColumn() {
        imageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        // First try to load from absolute path
                        File imageFile = new File(imagePath);
                        Image image;
                        
                        if (imageFile.exists()) {
                            image = new Image(imageFile.toURI().toString());
                        } else {
                            // Try to load from resources
                            URL resourceUrl = getClass().getResource(imagePath);
                            if (resourceUrl != null) {
                                image = new Image(resourceUrl.toString());
                            } else {
                                // Load default image if the image file doesn't exist
                                URL defaultImageUrl = getClass().getResource("/com/example/crud/icons/photo.png");
                                image = new Image(defaultImageUrl.toString());
                            }
                        }
                        
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        setGraphic(null);
                    }
                }
            }
        });
        
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                
                editButton.setOnAction(event -> {
                    Objet objet = getTableView().getItems().get(getIndex());
                    handleEdit(objet);
                });
                
                deleteButton.setOnAction(event -> {
                    Objet objet = getTableView().getItems().get(getIndex());
                    handleDelete(objet);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/BackOffice/ObjetForm.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Objet");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the table after adding
            loadObjets();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading form", e.getMessage());
        }
    }

    private void handleEdit(Objet objet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/BackOffice/ObjetForm.fxml"));
            Parent root = loader.load();
            
            ObjetFormController controller = loader.getController();
            controller.setObjet(objet);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Objet");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the table after editing
            loadObjets();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading form", e.getMessage());
        }
    }

    private void handleDelete(Objet objet) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'objet");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet objet ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                objetDAO.deleteObjet(objet.getIdObjet());
                loadObjets(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting object", e.getMessage());
            }
        }
    }

    private void loadObjets() {
        try {
            ObservableList<Objet> objets = FXCollections.observableArrayList(objetDAO.getAllObjets());
            objetTable.setItems(objets);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading objects", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 