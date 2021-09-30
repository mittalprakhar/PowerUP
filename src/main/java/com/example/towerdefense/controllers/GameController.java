package com.example.towerdefense.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class GameController {
    @FXML
    private VBox container;

    @FXML
    private Pane grid;

    @FXML
    private VBox sideMenu;

    @FXML
    private Label timer;

    private int timeRemaining = 180;

    private Tile[] tiles;
    private int[] tileImages;

    private static int ROWS = 40;
    private static int COLS = 60;

    public void initialize() throws FileNotFoundException {
        int tileSize = 600 / ROWS;
        container.setPrefWidth(tileSize * COLS);
        sideMenu.setPrefWidth(1200 - container.getPrefWidth());

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
        monumentHealth.setPrefWidth(tileSize * 6);
        monumentHealth.setTranslateX(tileSize * 51);
        monumentHealth.setTranslateY(tileSize * 14);
        grid.getChildren().add(monumentHealth);
    }

    private static int[] getTileImages() throws FileNotFoundException {
        Scanner s = new Scanner(new File("src/main/resources/map1.txt"));
        int[] array = new int[ROWS * COLS];
        for (int i = 0; i < array.length; i++) {
            array[i] = s.nextInt();
        }
        return array;
    }

    public void gameOn() {
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastSecond;

            @Override
            public void handle(long now) {
                System.out.println(lastSecond);
                if (lastSecond == 0L) {
                    lastSecond = now;
                } else {
                    long diff = now - lastSecond;
                    if (diff >= 1_000_000_000L) {
                        updateTimer();
                        lastSecond = now;
                    }
                }
            }
        };
        gameLoop.start();
    }

    public void updateTimer() {
        timeRemaining--;
        timer.setText(timeRemaining / 60 + ":" + new DecimalFormat("00").format(timeRemaining % 60));
    }

    private static class Tile extends StackPane {
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
            getChildren().add(border);
        }
    }
}