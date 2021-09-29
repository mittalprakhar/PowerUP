package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfigController {
    @FXML
    private Label configText;

    @FXML
    protected void onConfigButtonClick() {
        configText.setText("Welcome to Config Screen!");
    }
}