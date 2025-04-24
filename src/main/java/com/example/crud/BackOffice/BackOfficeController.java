package com.example.crud.BackOffice;

import com.example.crud.utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;

public class BackOfficeController implements Initializable {
    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set this layout as the main layout for navigation
        NavigationUtil.setMainLayout(root, "back");
        // Load dashboard by default
        handleDashboard();
    }

    @FXML
    private void handleDashboard() {
        NavigationUtil.navigateTo("/com/example/crud/BackOffice/Dashboard.fxml");
    }

    @FXML
    private void handleObjets() {
        NavigationUtil.navigateTo("/com/example/crud/BackOffice/ObjetList.fxml");
    }

    @FXML
    private void handleEchanges() {
        NavigationUtil.navigateTo("/com/example/crud/BackOffice/EchangeList.fxml");
    }

    @FXML
    private void handleFrontOffice() {
        NavigationUtil.navigateTo("/com/example/crud/FrontOffice/FrontOffice.fxml");
    }

    public void handleReports(ActionEvent actionEvent) {
    }

    public void handleSettings(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {

    }
} 