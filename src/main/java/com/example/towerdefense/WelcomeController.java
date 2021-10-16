package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Button startButton;

    @FXML
    protected void onWelcomeButtonClick() throws IOException {
        Stage primaryStage = Main.getPrimaryStage();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/config-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        scene.getStylesheets().add(String.valueOf(getClass().getResource(
                "/css/main.css")));

        ConfigController configController = fxmlLoader.getController();
        configController.initState();

        primaryStage.setScene(scene);
    }
}
