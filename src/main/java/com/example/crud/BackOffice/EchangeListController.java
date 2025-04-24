package com.example.crud.BackOffice;

import com.example.crud.dao.EchangeDAO;
import com.example.crud.dao.ObjetDAO;
import com.example.crud.model.Echange;
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
import java.util.*;
import java.util.stream.Collectors;

public class EchangeListController implements Initializable {

    @FXML
    private VBox blogContainer;
    @FXML
    private ScrollPane blogScrollPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> statusFilter;
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

    private final EchangeDAO echangeDAO = new EchangeDAO();
    private final ObjetDAO objetDAO = new ObjetDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ObservableList<Echange> allEchanges = FXCollections.observableArrayList();
    private Map<Integer, Objet> objetCache = new HashMap<>();
    private int currentPage = 1;
    private final int itemsPerPage = 6;
    private int totalPages = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        categoryFilter.getItems().addAll("Tous", "Électronique", "Vêtements", "Meubles", "Livres", "Autres");
        statusFilter.getItems().addAll("Tous", "En attente", "Confirmé", "Terminé", "Annulé");
        categoryFilter.setValue("Tous");
        statusFilter.setValue("Tous");

        // Set up animations
        setupAnimations();

        // Load data
        loadEchangesBlog();

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

    private void loadEchangesBlog() {
        try {
            // Clear previous items
            blogContainer.getChildren().clear();

            // Get all exchanges
            allEchanges.setAll(echangeDAO.getAllEchanges());

            // Cache objects for category filtering
            cacheObjects();

            // Apply filters and sorting
            ObservableList<Echange> filteredEchanges = filterAndSortEchanges();

            // Update total items label
            totalItemsLabel.setText(filteredEchanges.size() + " échanges au total");

            // Calculate total pages
            totalPages = (int) Math.ceil((double) filteredEchanges.size() / itemsPerPage);
            if (totalPages == 0) totalPages = 1;

            // Get exchanges for the current page
            int fromIndex = (currentPage - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredEchanges.size());
            ObservableList<Echange> pageEchanges = FXCollections.observableArrayList(
                    filteredEchanges.subList(fromIndex, toIndex)
            );

            // Create blog posts
            for (Echange echange : pageEchanges) {
                VBox blogPost = createBlogPost(echange);
                blogContainer.getChildren().add(blogPost);
                setupBlogItemAnimation(blogPost);
            }

            // Update pagination
            updatePagination();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading exchanges", e.getMessage());
        }
    }

    private void cacheObjects() throws SQLException {
        objetCache.clear();
        List<Objet> objets = objetDAO.getAllObjets();
        for (Objet objet : objets) {
            objetCache.put(objet.getIdObjet(), objet);
        }
    }

    private ObservableList<Echange> filterAndSortEchanges() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        return allEchanges.stream()
                .filter(echange -> searchText.isEmpty() || echange.getNameEchange().toLowerCase().contains(searchText))
                .filter(echange -> {
                    if ("Tous".equals(selectedCategory)) return true;
                    Objet objet = objetCache.get(echange.getIdObjet());
                    return objet != null && selectedCategory.equals(objet.getCategorie());
                })
                .filter(echange -> "Tous".equals(selectedStatus) || echange.getStatut().equals(selectedStatus))
                .sorted(Comparator.comparing(Echange::getDateEchange).reversed())
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

    private VBox createBlogPost(Echange echange) {
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
        Label title = new Label(echange.getNameEchange());
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        String category = objetCache.get(echange.getIdObjet()) != null ? objetCache.get(echange.getIdObjet()).getCategorie() : "Inconnu";
        Label categoryLabel = new Label(category);
        categoryLabel.setStyle("-fx-background-color: #e0e7ff; -fx-padding: 5 10; -fx-background-radius: 15; -fx-text-fill: #4f46e5;");
        header.getChildren().addAll(title, categoryLabel);

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
            File imageFile = new File(echange.getImageEchange());
            Image image;
            if (imageFile.exists()) {
                image = new Image(imageFile.toURI().toString(), true);
            } else {
                URL resourceUrl = getClass().getResource(echange.getImageEchange());
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
        Text message = new Text(echange.getMessage());
        TextFlow messageFlow = new TextFlow(message);
        messageFlow.setMaxWidth(350);

        // Status
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle statusIndicator = new Circle(6);
        statusIndicator.setFill(getStatusColor(echange.getStatut()));
        Label statusLabel = new Label(echange.getStatut());
        statusBox.getChildren().addAll(statusIndicator, statusLabel);

        // Metadata
        HBox metadata = new HBox(20);
        metadata.setAlignment(Pos.CENTER_LEFT);
        Label id = new Label("ID: " + echange.getIdEchange());
        Label idObjet = new Label("ID Objet: " + echange.getIdObjet());
        Label date = new Label("Date: " + echange.getDateEchange().format(dateFormatter));
        metadata.getChildren().addAll(id, idObjet, statusBox, date);

        // Actions
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> handleEdit(echange));
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleDelete(echange));
        actions.getChildren().addAll(editButton, deleteButton);

        contentArea.getChildren().addAll(messageFlow, metadata);
        blogPost.getChildren().addAll(header, imageView, contentArea, actions);
        return blogPost;
    }

    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "en attente":
                return Color.web("#f59e0b");
            case "confirmé":
                return Color.web("#22c55e");
            case "terminé":
                return Color.web("#3b82f6");
            case "annulé":
                return Color.web("#ef4444");
            default:
                return Color.GRAY;
        }
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
            loadEchangesBlog();
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
            loadEchangesBlog();
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
                loadEchangesBlog();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting exchange", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        currentPage = 1;
        loadEchangesBlog();
    }

    @FXML
    private void handleFilter() {
        currentPage = 1;
        loadEchangesBlog();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        categoryFilter.setValue("Tous");
        statusFilter.setValue("Tous");
        currentPage = 1;
        loadEchangesBlog();
    }

    @FXML
    private void goToFirstPage() {
        if (currentPage != 1) {
            currentPage = 1;
            loadEchangesBlog();
        }
    }

    @FXML
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadEchangesBlog();
        }
    }

    @FXML
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadEchangesBlog();
        }
    }

    @FXML
    private void goToLastPage() {
        if (currentPage != totalPages) {
            currentPage = totalPages;
            loadEchangesBlog();
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
                loadEchangesBlog();
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