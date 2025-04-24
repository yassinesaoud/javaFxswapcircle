package com.example.crud.BackOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ObjetFormController implements Initializable {

    @FXML
    private BorderPane mainPane;
    @FXML
    private TextField nomField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> etatComboBox;
    @FXML
    private ComboBox<String> categorieComboBox;
    @FXML
    private TextField imageField;
    @FXML
    private Button uploadButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label nomError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label etatError;
    @FXML
    private Label categorieError;
    @FXML
    private Label imageError;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ImageView imagePreview;

    private Objet objet;
    private boolean isEditMode = false;
    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        etatComboBox.getItems().addAll("Neuf", "Bon état", "État moyen", "À réparer");
        categorieComboBox.getItems().addAll("Électronique", "Vêtements", "Meubles", "Livres", "Autres");

        // Setup animations
        setupAnimations();

        // Real-time validation
        setupRealTimeValidation();
    }

    private void setupAnimations() {
        // Fade-in animation for the form
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Button hover animations
        for (Button btn : new Button[]{saveButton, cancelButton, uploadButton}) {
            btn.setOnMouseEntered(e -> {
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), btn);
                scaleIn.setToX(1.05);
                scaleIn.setToY(1.05);
                scaleIn.play();
            });
            btn.setOnMouseExited(e -> {
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), btn);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.play();
            });
        }
    }

    private void setupRealTimeValidation() {
        nomField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                nomError.setText("Le nom est requis.");
                nomField.setStyle("-fx-border-color: #f87171;");
            } else {
                nomError.setText("");
                nomField.setStyle("-fx-border-color: #64748b;");
            }
        });

        descriptionField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                descriptionError.setText("La description est requise.");
                descriptionField.setStyle("-fx-border-color: #f87171;");
            } else {
                descriptionError.setText("");
                descriptionField.setStyle("-fx-border-color: #64748b;");
            }
        });

        etatComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                etatError.setText("L'état est requis.");
                etatComboBox.setStyle("-fx-border-color: #f87171;");
            } else {
                etatError.setText("");
                etatComboBox.setStyle("-fx-border-color: #64748b;");
            }
        });

        categorieComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                categorieError.setText("La catégorie est requise.");
                categorieComboBox.setStyle("-fx-border-color: #f87171;");
            } else {
                categorieError.setText("");
                categorieComboBox.setStyle("-fx-border-color: #64748b;");
            }
        });

        imageField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                imageError.setText("Une image est requise.");
                imageField.setStyle("-fx-border-color: #f87171;");
            } else {
                imageError.setText("");
                imageField.setStyle("-fx-border-color: #64748b;");
            }
        });
    }

    public void setObjet(Objet objet) {
        this.objet = objet;
        this.isEditMode = true;
        nomField.setText(objet.getNom());
        descriptionField.setText(objet.getDescription());
        etatComboBox.setValue(objet.getEtat());
        categorieComboBox.setValue(objet.getCategorie());
        imageField.setText(objet.getImage() != null ? objet.getImage() : "");
        if (objet.getImage() != null && !objet.getImage().isEmpty()) {
            try {
                File file = new File(objet.getImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                    imagePreview.setImage(image);
                } else {
                    imagePreview.setImage(null);
                }
            } catch (Exception e) {
                imagePreview.setImage(null);
            }
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            imageField.setText(file.getAbsolutePath());
            imageError.setText("");
            imageField.setStyle("-fx-border-color: #64748b;");
            try {
                Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                imagePreview.setImage(image);
            } catch (Exception e) {
                imagePreview.setImage(null);
                imageError.setText("Impossible de charger l'image.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showError("Erreur de validation", "Veuillez corriger les erreurs dans le formulaire.");
            return;
        }
        progressBar.setVisible(true);
        progressBar.setProgress(0.5); // Simulate progress
        try {
            if (isEditMode) {
                updateObjet();
            } else {
                createObjet();
            }
            progressBar.setProgress(1.0);
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de l'enregistrement", e.getMessage());
            progressBar.setVisible(false);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        boolean isValid = true;
        if (nomField.getText().trim().isEmpty()) {
            nomError.setText("Le nom est requis.");
            nomField.setStyle("-fx-border-color: #f87171;");
            isValid = false;
        } else {
            nomError.setText("");
            nomField.setStyle("-fx-border-color: #64748b;");
        }

        if (descriptionField.getText().trim().isEmpty()) {
            descriptionError.setText("La description est requise.");
            descriptionField.setStyle("-fx-border-color: #f87171;");
            isValid = false;
        } else {
            descriptionError.setText("");
            descriptionField.setStyle("-fx-border-color: #64748b;");
        }

        if (etatComboBox.getValue() == null) {
            etatError.setText("L'état est requis.");
            etatComboBox.setStyle("-fx-border-color: #f87171;");
            isValid = false;
        } else {
            etatError.setText("");
            etatComboBox.setStyle("-fx-border-color: #64748b;");
        }

        if (categorieComboBox.getValue() == null) {
            categorieError.setText("La catégorie est requise.");
            categorieComboBox.setStyle("-fx-border-color: #f87171;");
            isValid = false;
        } else {
            categorieError.setText("");
            categorieComboBox.setStyle("-fx-border-color: #64748b;");
        }

        if (imageField.getText().trim().isEmpty()) {
            imageError.setText("Une image est requise.");
            imageField.setStyle("-fx-border-color: #f87171;");
            isValid = false;
        } else {
            File file = new File(imageField.getText());
            if (!file.exists() || !file.isFile()) {
                imageError.setText("Le fichier image est invalide.");
                imageField.setStyle("-fx-border-color: #f87171;");
                isValid = false;
            } else {
                imageError.setText("");
                imageField.setStyle("-fx-border-color: #64748b;");
            }
        }

        return isValid;
    }

    private void createObjet() throws SQLException {
        Objet newObjet = new Objet();
        newObjet.setNom(nomField.getText().trim());
        newObjet.setDescription(descriptionField.getText().trim());
        newObjet.setEtat(etatComboBox.getValue());
        newObjet.setCategorie(categorieComboBox.getValue());
        newObjet.setDateAjout(LocalDateTime.now());
        newObjet.setImage(imageField.getText().trim());
        objetDAO.saveObjet(newObjet);
    }

    private void updateObjet() throws SQLException {
        objet.setNom(nomField.getText().trim());
        objet.setDescription(descriptionField.getText().trim());
        objet.setEtat(etatComboBox.getValue());
        objet.setCategorie(categorieComboBox.getValue());
        objet.setImage(imageField.getText().trim());
        objetDAO.updateObjet(objet);
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}