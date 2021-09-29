package com.example.towerdefense;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onWelcomeButtonClick(ActionEvent event) throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");
        Node node = (Node)(event.getSource());
        Stage stage = (Stage) (node.getScene().getWindow());
        (new ConfigScreen()).start(stage);
    }
}