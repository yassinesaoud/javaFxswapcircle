package com.example.crud.FrontOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import com.example.crud.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.File;

public class ObjetListController implements Initializable {
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private ComboBox<String> stateFilter;

    @FXML
    private FlowPane objectsGrid;

    @FXML
    private ProgressIndicator loadingIndicator;

    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize filters
        categoryFilter.getItems().addAll("Toutes les catégories", "Électronique", "Vêtements", "Livres", "Sports", "Maison");
        stateFilter.getItems().addAll("Tous les états", "Neuf", "Très bon", "Bon", "Acceptable");

        // Add listeners for search and filters
        searchField.textProperty().addListener((obs, oldText, newText) -> filterObjects());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterObjects());
        stateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterObjects());

        // Load objects
        loadObjects();
    }

    private void loadObjects() {
        loadingIndicator.setVisible(true);
        objectsGrid.getChildren().clear();

        try {
            for (Objet objet : objetDAO.getAllObjets()) {
                objectsGrid.getChildren().add(createObjectCard(objet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading objects", e.getMessage());
        } finally {
            loadingIndicator.setVisible(false);
        }
    }

    private VBox createObjectCard(Objet objet) {
        VBox card = new VBox(10);
        card.getStyleClass().add("object-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Set default image or object image
        try {
            if (objet.getImage() != null && !objet.getImage().isEmpty()) {
                File imageFile = new File(objet.getImage());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    setDefaultImage(imageView);
                }
            } else {
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultImage(imageView);
        }

        // Object details
        Label nameLabel = new Label(objet.getNom());
        nameLabel.getStyleClass().add("object-name");

        Text descriptionText = new Text(objet.getDescription());
        descriptionText.getStyleClass().add("object-description");
        descriptionText.setWrappingWidth(200);

        Label categoryLabel = new Label(objet.getCategorie());
        categoryLabel.getStyleClass().add("object-category");

        Label stateLabel = new Label(objet.getEtat());
        stateLabel.getStyleClass().add("object-state");

        // Buttons in separate VBox for vertical alignment
        VBox buttonBox = new VBox(5);
        buttonBox.setAlignment(Pos.CENTER);

        Button exchangeButton = new Button("Proposer un échange");
        exchangeButton.getStyleClass().add("exchange-button");
        exchangeButton.setMaxWidth(Double.MAX_VALUE);
        exchangeButton.setOnAction(e -> handleExchange(objet));

        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("edit-button");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> handleEdit(objet));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> handleDelete(objet));

        buttonBox.getChildren().addAll(exchangeButton, editButton, deleteButton);

        card.getChildren().addAll(
                imageView,
                nameLabel,
                descriptionText,
                categoryLabel,
                stateLabel,
                buttonBox
        );
        return card;
    }

    private void setDefaultImage(ImageView imageView) {
        try {
            URL defaultImageUrl = getClass().getResource("/com/example/crud/icons/photo.png");
            if (defaultImageUrl != null) {
                imageView.setImage(new Image(defaultImageUrl.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterObjects() {
        String searchText = searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();
        String state = stateFilter.getValue();

        try {
            objectsGrid.getChildren().clear();
            for (Objet objet : objetDAO.getAllObjets()) {
                if (matchesFilters(objet, searchText, category, state)) {
                    objectsGrid.getChildren().add(createObjectCard(objet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error filtering objects", e.getMessage());
        }
    }

    private boolean matchesFilters(Objet objet, String searchText, String category, String state) {
        boolean matchesSearch = searchText.isEmpty() ||
                objet.getNom().toLowerCase().contains(searchText) ||
                objet.getDescription().toLowerCase().contains(searchText);

        boolean matchesCategory = category == null ||
                category.equals("Toutes les catégories") ||
                category.equals(objet.getCategorie());

        boolean matchesState = state == null ||
                state.equals("Tous les états") ||
                state.equals(objet.getEtat());

        return matchesSearch && matchesCategory && matchesState;
    }

    private void handleExchange(Objet objet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/ExchangeDialog.fxml"));
            Parent root = loader.load();

            ExchangeDialogController controller = loader.getController();
            controller.setSelectedObject(objet);

            // Since exchange is a dialog, we keep it as a new window
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Proposer un échange");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Impossible d'ouvrir le formulaire d'échange: " + e.getMessage());
        }
    }

    private void handleEdit(Objet objet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/ObjetForm.fxml"));
            Parent root = loader.load();

            ObjetFormController controller = loader.getController();
            controller.setObjet(objet);
            controller.setEditMode(true);
            controller.setOnSaveCallback(this::loadObjects);

            Scene currentScene = objectsGrid.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Impossible d'ouvrir le formulaire de modification: " + e.getMessage());
        }
    }

    private void handleDelete(Objet objet) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'objet");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet objet ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                objetDAO.deleteObjet(objet.getIdObjet());
                loadObjects(); // Refresh the list
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error", "Impossible de supprimer l'objet: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
