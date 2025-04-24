package com.example.crud.BackOffice;

import com.example.crud.dao.EchangeDAO;
import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Echange;
import com.example.crud.model.Objet;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class EchangeFormController implements Initializable {

    @FXML
    private ComboBox<Objet> objetComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea messageField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label objetError;
    @FXML
    private Label nameError;
    @FXML
    private Label messageError;
    @FXML
    private Label statutError;
    @FXML
    private ProgressBar progressBar;

    private Echange echange;
    private boolean isEditMode = false;
    private final EchangeDAO echangeDAO = new EchangeDAO();
    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        statutComboBox.getItems().addAll("En attente", "Confirmé", "Terminé", "Annulé");
        setupObjetComboBox();
        loadObjets();

        // Setup animations
        setupAnimations();

        // Real-time validation
        setupRealTimeValidation();
    }

    private void setupObjetComboBox() {
        objetComboBox.setConverter(new StringConverter<Objet>() {
            @Override
            public String toString(Objet objet) {
                if (objet == null) return null;
                return objet.getNom() + " (ID: " + objet.getIdObjet() + ")";
            }

            @Override
            public Objet fromString(String string) {
                return null; // Not needed
            }
        });
    }

    private void loadObjets() {
        try {
            objetComboBox.getItems().clear();
            objetComboBox.getItems().addAll(objetDAO.getAllObjets());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des objets", e.getMessage());
        }
    }

    private void setupAnimations() {
        // Fade-in animation for the form
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), objetComboBox.getParent());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Button hover animations
        for (Button btn : new Button[]{saveButton, cancelButton}) {
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
        objetComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                objetError.setText("L'objet est requis.");
                objetComboBox.setStyle("-fx-border-color: #ef4444;");
            } else {
                objetError.setText("");
                objetComboBox.setStyle("-fx-border-color: #d1d5db;");
            }
        });

        nameField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                nameError.setText("Le nom est requis.");
                nameField.setStyle("-fx-border-color: #ef4444;");
            } else {
                nameError.setText("");
                nameField.setStyle("-fx-border-color: #d1d5db;");
            }
        });

        messageField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                messageError.setText("Le message est requis.");
                messageField.setStyle("-fx-border-color: #ef4444;");
            } else {
                messageError.setText("");
                messageField.setStyle("-fx-border-color: #d1d5db;");
            }
        });

        statutComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                statutError.setText("Le statut est requis.");
                statutComboBox.setStyle("-fx-border-color: #ef4444;");
            } else {
                statutError.setText("");
                statutComboBox.setStyle("-fx-border-color: #d1d5db;");
            }
        });
    }

    public void setEchange(Echange echange) {
        this.echange = echange;
        this.isEditMode = true;
        objetComboBox.getItems().stream()
                .filter(obj -> obj.getIdObjet() == echange.getIdObjet())
                .findFirst()
                .ifPresent(obj -> objetComboBox.setValue(obj));
        nameField.setText(echange.getNameEchange());
        messageField.setText(echange.getMessage());
        statutComboBox.setValue(echange.getStatut());
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
                updateEchange();
            } else {
                createEchange();
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
        if (objetComboBox.getValue() == null) {
            objetError.setText("L'objet est requis.");
            objetComboBox.setStyle("-fx-border-color: #ef4444;");
            isValid = false;
        } else {
            objetError.setText("");
            objetComboBox.setStyle("-fx-border-color: #d1d5db;");
        }

        if (nameField.getText().trim().isEmpty()) {
            nameError.setText("Le nom est requis.");
            nameField.setStyle("-fx-border-color: #ef4444;");
            isValid = false;
        } else {
            nameError.setText("");
            nameField.setStyle("-fx-border-color: #d1d5db;");
        }

        if (messageField.getText().trim().isEmpty()) {
            messageError.setText("Le message est requis.");
            messageField.setStyle("-fx-border-color: #ef4444;");
            isValid = false;
        } else {
            messageError.setText("");
            messageField.setStyle("-fx-border-color: #d1d5db;");
        }

        if (statutComboBox.getValue() == null) {
            statutError.setText("Le statut est requis.");
            statutComboBox.setStyle("-fx-border-color: #ef4444;");
            isValid = false;
        } else {
            statutError.setText("");
            statutComboBox.setStyle("-fx-border-color: #d1d5db;");
        }

        return isValid;
    }

    private void createEchange() throws SQLException {
        Echange newEchange = new Echange();
        newEchange.setIdObjet(objetComboBox.getValue().getIdObjet());
        newEchange.setNameEchange(nameField.getText().trim());
        newEchange.setMessage(messageField.getText().trim());
        newEchange.setStatut(statutComboBox.getValue());
        newEchange.setDateEchange(LocalDateTime.now());
        echangeDAO.saveEchange(newEchange);
    }

    private void updateEchange() throws SQLException {
        echange.setIdObjet(objetComboBox.getValue().getIdObjet());
        echange.setNameEchange(nameField.getText().trim());
        echange.setMessage(messageField.getText().trim());
        echange.setStatut(statutComboBox.getValue());
        echangeDAO.updateEchange(echange);
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