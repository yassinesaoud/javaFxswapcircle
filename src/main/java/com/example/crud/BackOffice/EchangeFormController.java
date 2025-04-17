package com.example.crud.BackOffice;

import com.example.crud.dao.EchangeDAO;
import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Echange;
import com.example.crud.model.Objet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    private Echange echange;
    private boolean isEditMode = false;
    private final EchangeDAO echangeDAO = new EchangeDAO();
    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBox with predefined values
        statutComboBox.getItems().addAll("En attente", "Accepté", "Refusé", "Annulé");

        // Setup object ComboBox
        setupObjetComboBox();
        loadObjets();
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
                return null; // Not needed for this use case
            }
        });
    }

    private void loadObjets() {
        try {
            objetComboBox.getItems().clear();
            objetComboBox.getItems().addAll(objetDAO.getAllObjets());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading objects", e.getMessage());
        }
    }

    public void setEchange(Echange echange) {
        this.echange = echange;
        this.isEditMode = true;
        
        // Fill the form with exchange data
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
            return;
        }

        try {
            if (isEditMode) {
                updateEchange();
            } else {
                createEchange();
            }
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving exchange", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (objetComboBox.getValue() == null) {
            errors.append("Veuillez sélectionner un objet.\n");
        }
        if (nameField.getText().trim().isEmpty()) {
            errors.append("Le nom est requis.\n");
        }
        if (messageField.getText().trim().isEmpty()) {
            errors.append("Le message est requis.\n");
        }
        if (statutComboBox.getValue() == null) {
            errors.append("Le statut est requis.\n");
        }

        if (errors.length() > 0) {
            showError("Validation Error", errors.toString());
            return false;
        }
        return true;
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