package com.example.towerdefense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    /**
     * instance variable primaryStage shared across all controllers.
     */
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(
               getClass().getResource("/views/welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        Font.loadFont(getClass().getResourceAsStream("/css/future.otf"), 16);
        scene.getStylesheets().add(String.valueOf(getClass().getResource(
                "/css/main.css")));
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
