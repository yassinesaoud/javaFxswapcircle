package com.example.crud.BackOffice;

import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Objet;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ObjetListController implements Initializable {

    @FXML
    private VBox blogContainer;
    @FXML
    private ScrollPane blogScrollPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> stateFilter;
    @FXML
    private Label totalItemsLabel;
    @FXML
    private Label paginationLabel;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button firstPageButton;
    @FXML
    private Button lastPageButton;
    @FXML
    private HBox pageButtonsContainer;
    @FXML
    private StackPane contentContainer;
    @FXML
    private Button addButton;

    private final ObjetDAO objetDAO = new ObjetDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ObservableList<Objet> allObjets = FXCollections.observableArrayList();
    private int currentPage = 1;
    private final int itemsPerPage = 3;
    private int totalPages = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        categoryFilter.getItems().addAll("Tous", "Électronique", "Vêtements", "Meubles", "Livres", "Autres");
        stateFilter.getItems().addAll("Tous", "Neuf", "Bon état", "État moyen", "À réparer");
        categoryFilter.setValue("Tous");
        stateFilter.setValue("Tous");

        // Set up animations
        setupAnimations();

        // Load data
        loadObjetsBlog();

        // Initialize pagination
        updatePagination();
    }

    private void setupAnimations() {
        // Fade in animation for the entire UI
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), contentContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Button hover animations
        addButton.setOnMouseEntered(e -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), addButton);
            scaleIn.setToX(1.05);
            scaleIn.setToY(1.05);
            scaleIn.play();
        });
        addButton.setOnMouseExited(e -> {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), addButton);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();
        });
    }

    private void loadObjetsBlog() {
        try {
            // Clear previous items
            blogContainer.getChildren().clear();

            // Get all objects
            allObjets.setAll(objetDAO.getAllObjets());

            // Apply filters and sorting
            ObservableList<Objet> filteredObjets = filterAndSortObjets();

            // Update total items label
            totalItemsLabel.setText(filteredObjets.size() + " objets au total");

            // Calculate total pages
            totalPages = (int) Math.ceil((double) filteredObjets.size() / itemsPerPage);
            if (totalPages == 0) totalPages = 1;

            // Get objects for the current page
            int fromIndex = (currentPage - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredObjets.size());
            ObservableList<Objet> pageObjets = FXCollections.observableArrayList(
                    filteredObjets.subList(fromIndex, toIndex)
            );

            // Create blog posts
            for (Objet objet : pageObjets) {
                VBox blogPost = createBlogPost(objet);
                blogContainer.getChildren().add(blogPost);
                setupBlogItemAnimation(blogPost);
            }

            // Update pagination
            updatePagination();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading objects", e.getMessage());
        }
    }

    private ObservableList<Objet> filterAndSortObjets() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        String selectedState = stateFilter.getValue();

        return allObjets.stream()
                .filter(objet -> searchText.isEmpty() || objet.getNom().toLowerCase().contains(searchText))
                .filter(objet -> "Tous".equals(selectedCategory) || objet.getCategorie().equals(selectedCategory))
                .filter(objet -> "Tous".equals(selectedState) || objet.getEtat().equals(selectedState))
                .sorted(Comparator.comparing(Objet::getDateAjout).reversed())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void setupBlogItemAnimation(VBox blogItem) {
        blogItem.setOnMouseEntered(e -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), blogItem);
            scaleIn.setToX(1.03);
            scaleIn.setToY(1.03);
            scaleIn.play();
        });
        blogItem.setOnMouseExited(e -> {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), blogItem);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();
        });
    }

    private VBox createBlogPost(Objet objet) {
        // Create blog post container
        VBox blogPost = new VBox();
        blogPost.getStyleClass().add("blog-item");
        blogPost.setSpacing(15);
        blogPost.setPadding(new Insets(20));
        blogPost.setStyle("-fx-background-color: #ffffff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5); -fx-background-radius: 10;");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);
        Label title = new Label(objet.getNom());
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label category = new Label(objet.getCategorie());
        category.setStyle("-fx-background-color: #e0e7ff; -fx-padding: 5 10; -fx-background-radius: 15; -fx-text-fill: #4f46e5;");
        header.getChildren().addAll(title, category);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        Rectangle clip = new Rectangle(200, 200);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);
        try {
            File imageFile = new File(objet.getImage());
            Image image;
            if (imageFile.exists()) {
                image = new Image(imageFile.toURI().toString(), true);
            } else {
                URL resourceUrl = getClass().getResource(objet.getImage());
                image = resourceUrl != null
                        ? new Image(resourceUrl.toString(), true)
                        : new Image(getClass().getResource("/com/example/crud/icons/photo.png").toString(), true);
            }
            imageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Content
        VBox contentArea = new VBox(12);
        Text description = new Text(objet.getDescription());
        TextFlow descriptionFlow = new TextFlow(description);
        descriptionFlow.setMaxWidth(350);

        // State
        HBox stateBox = new HBox(10);
        stateBox.setAlignment(Pos.CENTER_LEFT);
        Circle stateIndicator = new Circle(6);
        stateIndicator.setFill(getStateColor(objet.getEtat()));
        Label stateLabel = new Label(objet.getEtat());
        stateBox.getChildren().addAll(stateIndicator, stateLabel);

        // Metadata
        HBox metadata = new HBox(20);
        metadata.setAlignment(Pos.CENTER_LEFT);
        Label id = new Label("ID: " + objet.getIdObjet());
        Label date = new Label("Ajouté: " + objet.getDateAjout().format(dateFormatter));
        metadata.getChildren().addAll(id, stateBox, date);

        // Actions
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> handleEdit(objet));
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleDelete(objet));
        actions.getChildren().addAll(editButton, deleteButton);

        contentArea.getChildren().addAll(descriptionFlow, metadata);
        blogPost.getChildren().addAll(header, imageView, contentArea, actions);
        return blogPost;
    }

    private Color getStateColor(String state) {
        switch (state.toLowerCase()) {
            case "neuf":
                return Color.web("#22c55e");
            case "bon état":
                return Color.web("#3b82f6");
            case "état moyen":
                return Color.web("#f59e0b");
            case "à réparer":
                return Color.web("#ef4444");
            default:
                return Color.GRAY;
        }
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
            loadObjetsBlog();
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
            loadObjetsBlog();
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
                loadObjetsBlog();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting object", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        currentPage = 1;
        loadObjetsBlog();
    }

    @FXML
    private void handleFilter() {
        currentPage = 1;
        loadObjetsBlog();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        categoryFilter.setValue("Tous");
        stateFilter.setValue("Tous");
        currentPage = 1;
        loadObjetsBlog();
    }

    @FXML
    private void goToFirstPage() {
        if (currentPage != 1) {
            currentPage = 1;
            loadObjetsBlog();
        }
    }

    @FXML
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadObjetsBlog();
        }
    }

    @FXML
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadObjetsBlog();
        }
    }

    @FXML
    private void goToLastPage() {
        if (currentPage != totalPages) {
            currentPage = totalPages;
            loadObjetsBlog();
        }
    }

    private void updatePagination() {
        pageButtonsContainer.getChildren().clear();
        for (int i = 1; i <= totalPages; i++) {
            Button pageButton = new Button(String.valueOf(i));
            pageButton.getStyleClass().add("pagination-button");
            if (i == currentPage) {
                pageButton.getStyleClass().add("active");
            }
            int pageNum = i;
            pageButton.setOnAction(e -> {
                currentPage = pageNum;
                loadObjetsBlog();
            });
            pageButtonsContainer.getChildren().add(pageButton);
        }
        paginationLabel.setText("Page " + currentPage + " sur " + totalPages);
        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(currentPage == totalPages);
        firstPageButton.setDisable(currentPage == 1);
        lastPageButton.setDisable(currentPage == totalPages);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}