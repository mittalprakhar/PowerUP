package com.example.towerdefense.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    @FXML
    private Pane container;

    private List<Tile> tiles;

    private static int ROWS = 20;
    private static int COLS = ROWS * 14/9;

    public void initialize() {
        int tileSize = (int) (Screen.getPrimary().getVisualBounds().getHeight() / ROWS);

        tiles = new ArrayList<>();
        for (int i = 0; i < ROWS * COLS; i++) {
            int x = tileSize * (i % COLS);
            int y = tileSize * (i / COLS);
            Tile tile = new Tile(x, y, false, tileSize);
            tile.setTranslateX(x);
            tile.setTranslateY(y);
            tiles.add(tile);
            container.getChildren().add(tile);
        }
    }

    private class Tile extends StackPane {
        private int x;
        private int y;
        private boolean occupied;

        public Tile(int x, int y, boolean occupied, double tileSize) {
            this.x = x;
            this.y = y;
            this.occupied = occupied;
            Rectangle border = new Rectangle(tileSize, tileSize);
            border.setFill(Color.DODGERBLUE);
            border.setStroke(Color.DARKBLUE);
            getChildren().add(border);
        }
    }
}