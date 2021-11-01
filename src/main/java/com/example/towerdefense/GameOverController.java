package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class GameOverController {
    @FXML
    private Label playerLabel;

    @FXML
    public void initialize() {

    }

    public void initState(Map<String, Object> gameParams) {
        playerLabel.setText(gameParams.get("playerName")
                + ", do not lose heart for thou showed great courage in slaying "
                + gameParams.get("kills") + " enemies!");
    }

    @FXML
    private void onRestartButtonClick() throws IOException {
        Stage primaryStage = Main.getPrimaryStage();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        Font.loadFont(getClass().getResourceAsStream("/css/futureTimeSplitters.otf"), 16);
        scene.getStylesheets().add(String.valueOf(getClass().getResource(
                "/css/main.css")));
        primaryStage.setTitle("Tower Defense");
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource(
                "/images/towerSpiky.png"))));
        primaryStage.setScene(scene);
    }

    @FXML
    private void onExitButtonClick() {
        Main.getPrimaryStage().close();
    }
}
