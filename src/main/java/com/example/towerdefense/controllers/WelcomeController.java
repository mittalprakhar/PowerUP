package com.example.towerdefense.controllers;

import com.example.towerdefense.screens.ConfigScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    public Label welcomeText;

    @FXML
    protected void onWelcomeButtonClick(ActionEvent event) throws IOException {
        Node node = (Node) (event.getSource());
        Stage stage = (Stage) (node.getScene().getWindow());
        (new ConfigScreen()).start(stage);
    }
}