package com.example.towerdefense.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameController {
    @FXML
    private VBox container;

    @FXML
    private Pane grid;

    private Tile[] tiles;
    private int[] tileImages;

    private static int ROWS = 40;
    private static int COLS = 60;

    public void initialize() throws FileNotFoundException {
        int tileSize = (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.96 / ROWS);
        container.setPrefWidth(tileSize * COLS);

        tiles = new Tile[ROWS * COLS];
        tileImages = getTileImages();

        for (int i = 0; i < tiles.length; i++) {
            int x = tileSize * (i % COLS);
            int y = tileSize * (i / COLS);
            Tile tile = new Tile(x, y, tileImages[i] != 0,
                    tileSize, new Image("/" + tileImages[i] + ".jpg"));
            tiles[i] = tile;
            tile.setTranslateX(x);
            tile.setTranslateY(y);
            grid.getChildren().add(tile);
        }

        ProgressBar monumentHealth = new ProgressBar();
        monumentHealth.setProgress(1);
        monumentHealth.setTranslateX(tileSize * 51);
        monumentHealth.setTranslateY(tileSize * 14);
        monumentHealth.setStyle("-fx-accent: #c90c22; -fx-background-insets: 0");
        grid.getChildren().add(monumentHealth);
    }

    private int[] getTileImages() throws FileNotFoundException {
        System.out.println(System.getProperty("user.dir"));
        Scanner s = new Scanner(new File("src/main/resources/map1.txt"));
        int[] array = new int[ROWS * COLS];
        for (int i = 0; i < array.length; i++) {
            array[i] = s.nextInt();
        }
        return array;
    }

    private class Tile extends StackPane {
        private int x;
        private int y;
        private boolean occupied;
        private Image background;

        public Tile(int x, int y, boolean occupied, double tileSize, Image background) {
            this.x = x;
            this.y = y;
            this.occupied = occupied;
            Rectangle border = new Rectangle(tileSize, tileSize);
            border.setFill(new ImagePattern(background));
            /*border.setStroke(Color.rgb(16, 16, 16));
            border.setStrokeWidth(0.25);*/
            getChildren().add(border);
        }
    }
}