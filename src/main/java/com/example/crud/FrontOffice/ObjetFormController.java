package com.example.crud.FrontOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import com.example.crud.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
    private ImageView imagePreview;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button removeImageButton;

    private File selectedImageFile;
    private String currentImagePath;
    private static final String UPLOAD_DIR = "uploads/images/objets";
    private Objet objet;
    private boolean isEditMode = false;
    private final ObjetDAO objetDAO = new ObjetDAO();
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes with predefined values
        etatComboBox.getItems().addAll("Neuf", "Très bon", "Bon", "Acceptable");
        categorieComboBox.getItems().addAll("Électronique", "Vêtements", "Livres", "Sports", "Maison");
        
        // Set default image
        setDefaultImage();
        
        // Create upload directory if it doesn't exist
        createUploadDirectory();
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
    }

    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Could not create upload directory");
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            try {
                // Preview the selected image
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                selectedImageFile = file;
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Could not load the selected image");
            }
        }
    }

    @FXML
    private void handleRemoveImage() {
        setDefaultImage();
        selectedImageFile = null;
        currentImagePath = null;
    }

    private void setDefaultImage() {
        try {
            URL defaultImageUrl = getClass().getResource("/com/example/crud/icons/photo.png");
            if (defaultImageUrl != null) {
                imagePreview.setImage(new Image(defaultImageUrl.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveImage() throws IOException {
        if (selectedImageFile == null) {
            return currentImagePath; // Return existing image path if no new image selected
        }

        String fileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
        Path targetPath = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public void setObjet(Objet objet) {
        this.objet = objet;
        this.isEditMode = true;
        
        // Fill the form with object data
        nomField.setText(objet.getNom());
        descriptionField.setText(objet.getDescription());
        etatComboBox.setValue(objet.getEtat());
        categorieComboBox.setValue(objet.getCategorie());
        
        // Load existing image if available
        if (objet.getImage() != null && !objet.getImage().isEmpty()) {
            try {
                currentImagePath = objet.getImage();
                File imageFile = new File(currentImagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imagePreview.setImage(image);
                } else {
                    setDefaultImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultImage();
            }
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            String imagePath = saveImage();
            
            if (isEditMode) {
                updateObjet(imagePath);
            } else {
                createObjet(imagePath);
            }
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            navigateToObjetList();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Error saving object", e.getMessage());
        }
    }

    private void createObjet(String imagePath) throws SQLException {
        Objet newObjet = new Objet();
        newObjet.setNom(nomField.getText().trim());
        newObjet.setDescription(descriptionField.getText().trim());
        newObjet.setEtat(etatComboBox.getValue());
        newObjet.setCategorie(categorieComboBox.getValue());
        newObjet.setImage(imagePath);
        newObjet.setDateAjout(LocalDateTime.now());
        
        objetDAO.saveObjet(newObjet);
    }

    private void updateObjet(String imagePath) throws SQLException {
        objet.setNom(nomField.getText().trim());
        objet.setDescription(descriptionField.getText().trim());
        objet.setEtat(etatComboBox.getValue());
        objet.setCategorie(categorieComboBox.getValue());
        objet.setImage(imagePath);
        
        objetDAO.updateObjet(objet);
    }

    @FXML
    private void handleCancel() {
        navigateToObjetList();
    }

    private void navigateToObjetList() {
        try {
            // Load the main layout first
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/FrontOffice.fxml"));
            Parent mainRoot = mainLoader.load();
            FrontOfficeController mainController = mainLoader.getController();
            
            // Load the object list
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/com/example/crud/FrontOffice/ObjetList.fxml"));
            Parent listRoot = listLoader.load();
            
            // Get the BorderPane from the main layout and set the object list in its center
            BorderPane root = (BorderPane) mainRoot;
            root.setCenter(listRoot);
            
       
            // Set up the stage
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not navigate to object list: " + e.getMessage());
        }
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

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 