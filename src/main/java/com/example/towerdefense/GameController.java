package com.example.towerdefense;

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
import java.util.*;

public class GameController {

    @FXML
    private VBox gameContainer;                 // Game container

    @FXML
    private Pane gamePane;                      // Game pane within game container
    private Tile[] tiles;                       // Array of all tiles
    private int[] tileImages;                   // Array of tile backgrounds

    private static final int ROWS = 40;         // Number of tiles across y-axis
    private static final int COLS = 60;         // Number of tiles across x-axis
    private static final int TILE_SIZE
            = 600 / ROWS;                       // Smallest grid unit - 1 tile size

    private ArrayList<Tower> towers;            // List of all towers
    private final ProgressBar monumentBar
            = new ProgressBar();                // Monument health bar
    private double monumentHealth;              // Starting monument health

    @FXML
    private VBox sideContainer;                 // Side menu container

    @FXML
    private Label playerLabel;                  // Player label in side menu

    @FXML
    private Label difficultyLabel;              // Difficulty label in side menu
    private String difficulty;                  // Starting difficulty

    @FXML
    private Label timeLabel;                    // Time label in side menu
    private int time;                           // Starting time in seconds

    @FXML
    private Label moneyLabel;                   // Money label in side menu
    private int money;                          // Starting money

    @FXML
    private Label killsLabel;                   // Kills label in side menu
    private int kills;                          // Starting kills

    @FXML
    public void initialize() {
        // Divide game screen into two containers
        gameContainer.setPrefWidth(TILE_SIZE * COLS);
        sideContainer.setPrefWidth(1200 - gameContainer.getPrefWidth());

        // Initialize tile array and towers list
        tiles = new Tile[ROWS * COLS];
        towers = new ArrayList<>();

        // Initialize independent game variables
        time = 180;
        timeLabel.setText(time / 60 + ":"
                + new DecimalFormat("00").format(time % 60));

        kills = 0;
        killsLabel.setText(kills + "");
    }

    /**
     * Sets up tiles, dependent game variables, and monument
     *
     * @param configParams config parameters such as name, difficulty, and map
     * @throws FileNotFoundException if map file is not present
     */
    public void initState(Map<String, Object> configParams) throws FileNotFoundException {
        // Get tile images from map file
        tileImages = readMap("src/main/resources/maps/"
                + configParams.get("mapName").toString().toLowerCase() + ".txt");

        // Initialize tiles
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile(TILE_SIZE * (i % COLS), TILE_SIZE * (i / COLS),
                    tileImages[i] != 0, new Image(String.valueOf(getClass().getResource(
                    "/images/tile" + tileImages[i] + ".png"))));
        }

        // Initialize dependent game variables
        playerLabel.setText(String.valueOf(configParams.get("playerName")));

        difficulty = String.valueOf(configParams.get("difficulty"));
        difficultyLabel.setText(difficulty);

        switch (difficulty) {
        case "Beginner":
            money = 500;
            monumentHealth = 1.0;
            break;
        case "Moderate":
            money = 400;
            monumentHealth = 0.9;
            break;
        default:
            money = 300;
            monumentHealth = 0.8;
            break;
        }
        moneyLabel.setText(money + "");

        // Initialize monument health bar
        monumentBar.setProgress(monumentHealth);
        monumentBar.setId("monumentHealth");
        gamePane.getChildren().add(monumentBar);

        // Initialize starting towers (only for M2 - just to show we can place towers)
        if (configParams.get("mapName").equals("Forest")) {
            towers.add(new Tower(TILE_SIZE * 23, TILE_SIZE * 15, TILE_SIZE * 3,
                    30, new Image(String.valueOf(getClass().getResource(
                    "/images/tower1.png")))));

            towers.add(new Tower(TILE_SIZE * 37, TILE_SIZE * 23, TILE_SIZE * 4,
                    60, new Image(String.valueOf(getClass().getResource(
                    "/images/tower2.png")))));

        }

        gameOn();
    }

    /**
     * Loads map file from path
     *
     * @param path path of map file
     * @return tile images array
     * @throws FileNotFoundException if map file is not present
     */
    private int[] readMap(String path) throws FileNotFoundException {
        Scanner s = new Scanner(new File(path));
        int[] array = new int[ROWS * COLS];
        for (int i = 0; i < array.length; i++) {
            array[i] = s.nextInt();
        }

        // Set location of monument health bar
        monumentBar.setTranslateY(TILE_SIZE * (s.nextInt() - 1));
        monumentBar.setTranslateX(TILE_SIZE * (s.nextInt() - 1));
        monumentBar.setPrefWidth(TILE_SIZE * s.nextInt());

        return array;
    }

    /**
     * Handles gameplay logic
     * Has an animation timer that calls other gameplay methods when required
     */
    public void gameOn() {
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTimeUpdate;
            private long lastMoneyUpdate;

            @Override
            public void handle(long now) {
                // Every 1 second, updates timer and tower health
                if (lastTimeUpdate == 0L) {
                    lastTimeUpdate = now;
                } else {
                    long diff = now - lastTimeUpdate;
                    if (diff >= 1_000_000_000L) {
                        updateTime();
                        try {
                            for (Iterator<Tower> iterator = towers.iterator();
                                 iterator.hasNext();) {
                                Tower t = iterator.next();
                                t.updateHealth();
                            }
                        } catch (ConcurrentModificationException ignored) {
                        }
                        lastTimeUpdate = now;
                    }
                }

                // Every 20 seconds, adds money
                if (lastMoneyUpdate == 0L) {
                    lastMoneyUpdate = now;
                } else {
                    long diff = now - lastMoneyUpdate;
                    if (diff >= 20_000_000_000L) {
                        addMoney();
                        lastMoneyUpdate = now;
                    }
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Updates time every 1 second
     */
    public void updateTime() {
        if (time > 0) {
            time--;
            timeLabel.setText(time / 60 + ":"
                    + new DecimalFormat("00").format(time % 60));
        }
    }

    /**
     * Adds money every 20 seconds
     */
    public void addMoney() {
        money += 20;
        moneyLabel.setText(money + "");
    }

    /**
     * Defines a single tile with x-y coordinates, a background image,
     * and a boolean to track if the tile is occupied by a path/building or not.
     * <p>
     * Game container --> game pane --> tile stack panes
     */
    private class Tile extends StackPane {
        private int x;
        private int y;
        private boolean occupied;
        private Image background;

        public Tile(int x, int y, boolean occupied, Image background) {
            this.x = x;
            this.y = y;
            this.occupied = occupied;

            Rectangle border = new Rectangle(TILE_SIZE, TILE_SIZE);
            border.setFill(new ImagePattern(background));
            getChildren().add(border);
            this.setTranslateX(x);
            this.setTranslateY(y);
            gamePane.getChildren().add(this);
        }
    }

    /**
     * Defines a single tower with x-y coordinates, a background image,
     * a given size (since towers can take up multiple tiles), and health variables.
     * <p>
     * Game container --> game pane --> tower stack panes
     * <p>
     * Yet to implement placing towers on non-occupied tiles.
     */
    private class Tower extends StackPane {
        private int x;
        private int y;
        private double towerSize;
        private Image background;
        private double maxHealth;
        private double curHealth;
        private ProgressBar healthBar;

        public Tower(int x, int y, double towerSize, double maxHealth, Image background) {
            this.x = x;
            this.y = y;
            this.towerSize = towerSize;
            this.maxHealth = maxHealth;
            this.curHealth = maxHealth;

            Rectangle border = new Rectangle(towerSize, towerSize);
            border.setFill(new ImagePattern(background));
            getChildren().add(border);
            this.setTranslateX(x);
            this.setTranslateY(y);
            gamePane.getChildren().add(this);

            healthBar = new ProgressBar();
            healthBar.setProgress(1);
            healthBar.setTranslateX(x);
            healthBar.setTranslateY(y - TILE_SIZE);
            healthBar.setPrefWidth(towerSize);
            healthBar.setPrefHeight(TILE_SIZE * 0.8);
            gamePane.getChildren().add(healthBar);
        }

        public void updateHealth() {
            if (curHealth > 0) {
                curHealth -= 1;
                healthBar.setProgress(curHealth / maxHealth);
            } else {
                destroy();
            }
        }

        public void destroy() {
            gamePane.getChildren().remove(this);
            gamePane.getChildren().remove(healthBar);
            towers.remove(this);
        }
    }
}
