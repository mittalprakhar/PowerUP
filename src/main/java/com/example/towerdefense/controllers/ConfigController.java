package com.example.towerdefense.controllers;

import com.example.towerdefense.screens.GameScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigController {
    @FXML
    protected void onConfigButtonClick(ActionEvent event) throws IOException {
        Node node = (Node) (event.getSource());
        Stage stage = (Stage) (node.getScene().getWindow());
        (new GameScreen()).start(stage);
    }
}