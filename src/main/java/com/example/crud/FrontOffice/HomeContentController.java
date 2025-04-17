package com.example.crud.FrontOffice;

import com.example.crud.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeContentController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nothing to initialize
    }

    @FXML
    private void handleStartSwapping() {
        NavigationUtil.navigateTo("/com/example/crud/FrontOffice/ObjetList.fxml");
    }
} 