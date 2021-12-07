package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.scene.media.AudioClip;
import java.nio.file.Paths;
import static javafx.scene.media.AudioClip.INDEFINITE;


import java.io.IOException;

public class WelcomeController {

    private AudioClip lobbyMusic;

    @FXML
    private Button startButton;

    public WelcomeController() {
        lobbyMusic = new AudioClip(
                Paths.get("src/main/resources/music/lobby.mp3").toUri().toString());
        lobbyMusic.setCycleCount(INDEFINITE);
        lobbyMusic.play();
    }

    @FXML
    protected void onWelcomeButtonClick() throws IOException {
        Stage primaryStage = Main.getPrimaryStage();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/config-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        scene.getStylesheets().add(String.valueOf(getClass().getResource(
                "/css/main.css")));

        ConfigController configController = fxmlLoader.getController();
        configController.initState(lobbyMusic);

        primaryStage.setScene(scene);
    }
}
