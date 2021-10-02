package com.example.towerdefense.screens;

import com.example.towerdefense.controllers.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class GameScreen {
    public void start(Stage stage, Map<String, Object> configParams) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameScreen.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(),
                stage.getScene().getHeight());
        scene.getStylesheets().add("game.css");
        stage.setScene(scene);
        ((GameController) fxmlLoader.getController()).setUp(configParams);
    }
}
