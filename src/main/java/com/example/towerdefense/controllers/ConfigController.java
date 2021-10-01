package com.example.towerdefense.controllers;

import com.example.towerdefense.screens.GameScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> difficultyModes =
                FXCollections.observableArrayList("Beginner", "Moderate", "Expert");
        difficultyComboBox.setItems(difficultyModes);

        String[] mapOptions = {"Garden", "Map", "Maze"};
        maps = new Map[mapOptions.length];
        for (int index = 0; index < mapOptions.length; index++) {
            maps[index] = new Map(mapOptions[index],
                    new Image("/" + mapOptions[index].toLowerCase() + ".png"));
        }

        mapLabel.setText(maps[mapIndex].name);
        mapImageView.setImage(maps[mapIndex].image);
    }

    @FXML
    public void onStartButtonClick(ActionEvent actionEvent) throws IOException {
        playerName = nameTextField.getText();
        difficulty = difficultyComboBox.getSelectionModel().getSelectedItem();
        if (isConfigValid()) {
            Node node = (Node) (actionEvent.getSource());
            Stage stage = (Stage) (node.getScene().getWindow());
            java.util.Map<String, Object> configParams = new HashMap<>();
            configParams.put("playerName", playerName);
            configParams.put("difficulty", difficulty);
            configParams.put("mapName", maps[mapIndex].name);
            (new GameScreen()).start(stage, configParams);
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