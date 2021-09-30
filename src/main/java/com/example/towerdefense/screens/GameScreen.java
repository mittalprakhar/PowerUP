package com.example.towerdefense.screens;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GameScreen extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreen.class.getResource("game-view.fxml"));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(fxmlLoader.load(), screenSize.getWidth(), screenSize.getHeight());
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}