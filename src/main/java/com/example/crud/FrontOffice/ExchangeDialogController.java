package com.example.crud.FrontOffice;

import com.example.crud.dao.EchangeDAO;
import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Echange;
import com.example.crud.model.Objet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ExchangeDialogController implements Initializable {
    @FXML
    private Text selectedObjectText;
    
    @FXML
    private Text selectedObjectDescription;
    
    @FXML
    private ImageView selectedObjectImage;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextArea messageField;
    
    @FXML
    private Button chooseImageButton;
    
    @FXML
    private Label imageNameLabel;
    
    @FXML
    private ImageView imagePreview;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button submitButton;

    private final ObjetDAO objetDAO = new ObjetDAO();
    private final EchangeDAO echangeDAO = new EchangeDAO();
    private Objet selectedObject;
    private File selectedImageFile;
    private String currentImagePath;
    private static final String UPLOAD_DIR = "uploads/images/echanges";
    private int userId = 1; // TODO: Get actual logged-in user ID

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize image preview with default image or placeholder
        setDefaultImage(imagePreview);
        createUploadDirectory();
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

    public void setSelectedObject(Objet objet) {
        this.selectedObject = objet;
        selectedObjectText.setText(objet.getNom());
        selectedObjectDescription.setText(objet.getDescription());
        
        // Load and set the image
        if (objet.getImage() != null && !objet.getImage().isEmpty()) {
            try {
                File imageFile = new File(objet.getImage());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    selectedObjectImage.setImage(image);
                } else {
                    setDefaultImage(selectedObjectImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultImage(selectedObjectImage);
            }
        } else {
            setDefaultImage(selectedObjectImage);
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
                imageNameLabel.setText(file.getName());
                selectedImageFile = file;
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Impossible de charger l'image: " + e.getMessage());
            }
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

    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        try {
            String imagePath = saveImage();
            
            Echange echange = new Echange();
            echange.setIdObjet(selectedObject.getIdObjet());
            echange.setNameEchange(nameField.getText().trim());
            echange.setMessage(messageField.getText().trim());
            echange.setDateEchange(LocalDateTime.now());
            echange.setStatut("En attente");
            echange.setImageEchange(imagePath);

            echangeDAO.saveEchange(echange);
            
            showSuccess("Échange proposé", "Votre proposition d'échange a été envoyée avec succès!");
            closeDialog();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Error", "Impossible de créer l'échange: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("Le nom de l'échange est requis.\n");
        }
        if (messageField.getText().trim().isEmpty()) {
            errors.append("Le message est requis.\n");
        }
        if (selectedImageFile == null) {
            errors.append("Veuillez sélectionner une image pour votre objet.\n");
        }

        if (errors.length() > 0) {
            showError("Validation", errors.toString());
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

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
} 