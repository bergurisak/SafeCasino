package com.example.safecasino;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SafeCasinoController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}