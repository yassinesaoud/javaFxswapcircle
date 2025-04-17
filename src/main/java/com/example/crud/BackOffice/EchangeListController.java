package com.example.crud.BackOffice;

import com.example.crud.dao.EchangeDAO;
import com.example.crud.model.Echange;
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

public class EchangeListController implements Initializable {
    @FXML
    private TableView<Echange> echangeTable;

    @FXML
    private TableColumn<Echange, Integer> idColumn;

    @FXML
    private TableColumn<Echange, String> imageColumn;

    @FXML
    private TableColumn<Echange, Integer> idObjetColumn;

    @FXML
    private TableColumn<Echange, String> nameColumn;

    @FXML
    private TableColumn<Echange, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Echange, String> messageColumn;

    @FXML
    private TableColumn<Echange, String> statutColumn;

    @FXML
    private TableColumn<Echange, Void> actionsColumn;

    private final EchangeDAO echangeDAO = new EchangeDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEchange"));
        idObjetColumn.setCellValueFactory(new PropertyValueFactory<>("idObjet"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameEchange"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEchange"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Set up image column
        setupImageColumn();
        
        setupActionsColumn();
        
        // Load data from database
        loadEchanges();
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
        
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageEchange"));
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
                    Echange echange = getTableView().getItems().get(getIndex());
                    handleEdit(echange);
                });
                
                deleteButton.setOnAction(event -> {
                    Echange echange = getTableView().getItems().get(getIndex());
                    handleDelete(echange);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/BackOffice/EchangeForm.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Échange");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the table after adding
            loadEchanges();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading form", e.getMessage());
        }
    }

    private void handleEdit(Echange echange) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/BackOffice/EchangeForm.fxml"));
            Parent root = loader.load();
            
            EchangeFormController controller = loader.getController();
            controller.setEchange(echange);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Échange");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the table after editing
            loadEchanges();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading form", e.getMessage());
        }
    }

    private void handleDelete(Echange echange) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'échange");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet échange ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                echangeDAO.deleteEchange(echange.getIdEchange());
                loadEchanges(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting exchange", e.getMessage());
            }
        }
    }

    private void loadEchanges() {
        try {
            ObservableList<Echange> echanges = FXCollections.observableArrayList(echangeDAO.getAllEchanges());
            echangeTable.setItems(echanges);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading exchanges", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 