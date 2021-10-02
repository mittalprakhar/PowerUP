package com.example.towerdefense.screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigScreen {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConfigScreen.class.getResource("config-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(),
                stage.getScene().getHeight());
        stage.setScene(scene);
    }
}
