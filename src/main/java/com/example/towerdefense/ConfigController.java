package com.example.towerdefense;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class ConfigController {

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button startButton;

    @FXML
    private ImageView mapImageView = new ImageView();

    @FXML
    private Label mapLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<String> difficultyComboBox;

    private static Map[] maps;
    private int mapIndex = 0;
    private String playerName;
    private String difficulty;
    private AudioClip lobbyMusicConfig;

    public void initState(AudioClip lobbyMusic) {
        lobbyMusicConfig = lobbyMusic;
    }

    @FXML
    public void initialize() {
        ObservableList<String> difficultyModes =
                FXCollections.observableArrayList("Beginner", "Moderate", "Expert");
        difficultyComboBox.setItems(difficultyModes);

        String[] mapOptions = {"Forest", "Ocean", "Desert"};
        maps = new Map[mapOptions.length];
        for (int index = 0; index < mapOptions.length; index++) {
            maps[index] = new Map(mapOptions[index],
                    new Image(String.valueOf(getClass().getResource(
                            "/images/" + mapOptions[index].toLowerCase() + ".png"))));
        }

        mapLabel.setText(maps[mapIndex].name);
        mapImageView.setImage(maps[mapIndex].image);
    }

    @FXML
    public void onStartButtonClick() throws IOException {
        playerName = nameTextField.getText();
        difficulty = difficultyComboBox.getSelectionModel().getSelectedItem();
        if (isConfigValid()) {
            if (lobbyMusicConfig != null) {
                lobbyMusicConfig.stop();
            }

            Stage primaryStage = Main.getPrimaryStage();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/views/game-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            scene.getStylesheets().add(String.valueOf(getClass().getResource(
                    "/css/main.css")));

            java.util.Map<String, Object> configParams = new HashMap<>();
            configParams.put("playerName", playerName);
            configParams.put("difficulty", difficulty);
            configParams.put("mapName", maps[mapIndex].name);

            GameController gameController = fxmlLoader.getController();
            gameController.initState(configParams);

            primaryStage.setScene(scene);
        }
    }

    @FXML
    public void onMousePressed(MouseEvent mouseEvent) {
        Node focusedNode = nameTextField;
        if (!focusedNode.equals(mouseEvent.getSource())) {
            focusedNode.getParent().requestFocus();
        }
    }

    @FXML
    public void onPrevButtonClick() {
        switchMaps(true);
    }

    @FXML
    public void onNextButtonClick() {
        switchMaps(false);
    }

    /**
     * Correctly switches to the previous or next map.
     * @param left whether to switch to the left or right map
     */
    private void switchMaps(boolean left) {
        if (left) {
            mapIndex--;
            if (mapIndex < 0) {
                mapIndex = maps.length - 1;
            }
        } else {
            mapIndex++;
            if (mapIndex >= maps.length) {
                mapIndex = 0;
            }
        }
        mapLabel.setText(maps[mapIndex].name);
        mapImageView.setImage(maps[mapIndex].image);
    }

    /**
     * Check the validity of inputs such as name and difficulty.
     * @return true if the player configured/setup game properly, false otherwise
     */
    private boolean isConfigValid() {
        if (playerName == null || playerName.trim().isEmpty() || difficulty == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Input");
            if (playerName == null || playerName.trim().isEmpty()) {
                alert.setContentText("Please enter a valid name.");
            } else {
                alert.setContentText("Please select a difficulty.");
            }

            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(String.valueOf(getClass().getResource(
                    "/images/towerSpiky.png"))));

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(String.valueOf(getClass().getResource(
                    "/css/main.css")));

            alert.show();
            return false;
        }
        return true;
    }

    private static class Map {
        private final String name;
        private final Image image;

        public Map(String name, Image image) {
            this.name = name;
            this.image = image;
        }
    }
}
