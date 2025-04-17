package com.example.crud.BackOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ObjetFormController implements Initializable {
    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Objet objet;
    private boolean isEditMode = false;
    private final ObjetDAO objetDAO = new ObjetDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes with predefined values
        etatComboBox.getItems().addAll("Neuf", "Bon état", "État moyen", "À réparer");
        categorieComboBox.getItems().addAll("Électronique", "Vêtements", "Meubles", "Livres", "Autres");
    }

    public void setObjet(Objet objet) {
        this.objet = objet;
        this.isEditMode = true;
        
        // Fill the form with object data
        nomField.setText(objet.getNom());
        descriptionField.setText(objet.getDescription());
        etatComboBox.setValue(objet.getEtat());
        categorieComboBox.setValue(objet.getCategorie());
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            if (isEditMode) {
                updateObjet();
            } else {
                createObjet();
            }
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving object", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("Le nom est requis.\n");
        }
        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("La description est requise.\n");
        }
        if (etatComboBox.getValue() == null) {
            errors.append("L'état est requis.\n");
        }
        if (categorieComboBox.getValue() == null) {
            errors.append("La catégorie est requise.\n");
        }

        if (errors.length() > 0) {
            showError("Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    private void createObjet() throws SQLException {
        Objet newObjet = new Objet();
        newObjet.setNom(nomField.getText().trim());
        newObjet.setDescription(descriptionField.getText().trim());
        newObjet.setEtat(etatComboBox.getValue());
        newObjet.setCategorie(categorieComboBox.getValue());
        newObjet.setDateAjout(LocalDateTime.now());
        
        objetDAO.saveObjet(newObjet);
    }

    private void updateObjet() throws SQLException {
        objet.setNom(nomField.getText().trim());
        objet.setDescription(descriptionField.getText().trim());
        objet.setEtat(etatComboBox.getValue());
        objet.setCategorie(categorieComboBox.getValue());
        
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