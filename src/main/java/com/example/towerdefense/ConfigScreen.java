package com.example.towerdefense;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigScreen {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreen.class.getResource("config-view.fxml"));
        Scene NewScene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(NewScene);
        stage.show();
    }
}
