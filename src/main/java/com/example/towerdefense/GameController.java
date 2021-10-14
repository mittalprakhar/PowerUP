package com.example.towerdefense;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    private ProgressBar monumentBar;            // Monument health bar
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
    private ListView<Tower> towerMenu;          // Tower list view in side menu
    private ObservableList<Tower> gameTowers;   // List of towers in game

    private Tower selectedTower;                // Currently selected tower from the menu
    private List<Tower> playerTowers;           // List of all towers placed by player

    @FXML
    public void initialize() {
        // Divide game screen into two containers
        gameContainer.setPrefWidth(TILE_SIZE * COLS);
        sideContainer.setPrefWidth(1200 - gameContainer.getPrefWidth());

        // Initialize variables
        tiles = new Tile[ROWS * COLS];
        playerTowers = new ArrayList<>();
        monumentBar = new ProgressBar();
        gameTowers = FXCollections.observableArrayList();

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
        Set<Integer> ground = new HashSet<>(Arrays.asList(0, 5, 6));
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile(TILE_SIZE * (i % COLS), TILE_SIZE * (i / COLS),
                    !ground.contains(tileImages[i]), new Image(String.valueOf(getClass().
                    getResource("/images/tile" + tileImages[i] + ".png"))));
        }

        // Initialize dependent game variables
        playerLabel.setText(String.valueOf(configParams.get("playerName")));

        difficulty = String.valueOf(configParams.get("difficulty"));
        difficultyLabel.setText(difficulty);

        int costDifficultyFactor;

        switch (difficulty) {
        case "Beginner":
            money = 500;
            monumentHealth = 1.0;
            costDifficultyFactor = 0;
            break;
        case "Moderate":
            money = 450;
            monumentHealth = 0.9;
            costDifficultyFactor = 10;
            break;
        default:
            money = 400;
            monumentHealth = 0.8;
            costDifficultyFactor = 20;
            break;
        }
        moneyLabel.setText(money + "");

        // Initialize monument health bar
        monumentBar.setProgress(monumentHealth);
        monumentBar.setId("monumentHealth");
        gamePane.getChildren().add(monumentBar);

        // Initialize gameTowers with all available game towers
        initializeGameTowers(gameTowers, costDifficultyFactor);

        // Set up the CellFactory
        towerMenu.setCellFactory(listCell -> new ListCell<>() {
            @Override
            protected void updateItem(Tower tower, boolean empty) {
                super.updateItem(tower, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Create a HBox to hold each tower with complete info
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);

                    // Create a VBox to hold each tower's name and image
                    VBox vBox = new VBox();

                    // Scale the tower image for display in vBox
                    ImageView imageView = new ImageView(tower.background);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);

                    // Create a label with tower data to display
                    Label label = new Label(tower.name + " ($" + tower.cost + ")");
                    label.setWrapText(true);

                    // Fill the VBox with imageView and label
                    vBox.getChildren().addAll(imageView, label);
                    vBox.setAlignment(Pos.CENTER);

                    // Fill the HBox with nodes and additional tower metadata
                    hBox.getChildren().addAll(
                            vBox,
                            new Label(tower.description)
                    );

                    hBox.setSpacing(10);

                    // Set the HBox as the display
                    setGraphic(hBox);
                }
            }
        });

        // Bind our list of pieces to the ListView
        towerMenu.setItems(gameTowers);

        // Add listener to track which tower is currently selected by player
        towerMenu.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, towerOld, towerNew) -> selectedTower = towerNew);

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
     * Initializes gameTowers with all available game towers.
     * @param gameTowers the observable list to fill all game towers with
     * @param costDifficultyFactor the amount by which the cost of each tower increases
     *                             because of the difficulty
     */
    private void initializeGameTowers(ObservableList<Tower> gameTowers,
                                      int costDifficultyFactor) {
        gameTowers.add(new Tower("Tower1",
                "Description1 contains description of properties about tower1" +
                        "so player can use tower1", 50 + costDifficultyFactor,
                TILE_SIZE * 2, 50, new Image(String.valueOf(
                        getClass().getResource("/images/tower1.png")))));

        gameTowers.add(new Tower("Tower2",
                "Description2 contains description of properties about tower2" +
                        "so player can use tower2", 75 + costDifficultyFactor,
                TILE_SIZE * 2, 60, new Image(String.valueOf(
                        getClass().getResource("/images/tower2.png")))));

        gameTowers.add(new Tower("Tower3",
                "Description3 contains description of properties about tower3" +
                        "so player can use tower3", 100 + costDifficultyFactor,
                TILE_SIZE * 3, 70, new Image(String.valueOf(
                        getClass().getResource("/images/tower3.png")))));

        gameTowers.add(new Tower("Tower4",
                "Description4 contains description of properties about tower4" +
                        "so player can use tower4", 130 + costDifficultyFactor,
                TILE_SIZE * 3, 90, new Image(String.valueOf(
                        getClass().getResource("/images/tower4.png")))));

        gameTowers.add(new Tower("Tower5",
                "Description5 contains description of properties about tower5" +
                        "so player can use tower5", 160 + costDifficultyFactor,
                TILE_SIZE * 3, 110, new Image(String.valueOf(
                        getClass().getResource("/images/tower5.png")))));

        gameTowers.add(new Tower("Tower6",
                "Description6 contains description of properties about tower6" +
                        "so player can use tower6", 200 + costDifficultyFactor,
                TILE_SIZE * 4, 130, new Image(String.valueOf(
                        getClass().getResource("/images/tower6.png")))));

        gameTowers.add(new Tower("Tower7",
                "Description7 contains description of properties about tower7" +
                        "so player can use tower7", 250 + costDifficultyFactor,
                TILE_SIZE * 4, 160, new Image(String.valueOf(
                        getClass().getResource("/images/tower7.png")))));

        gameTowers.add(new Tower("Tower8",
                "Description8 contains description of properties about tower8" +
                        "so player can use tower8", 300 + costDifficultyFactor,
                TILE_SIZE * 5, 190, new Image(String.valueOf(
                        getClass().getResource("/images/tower8.png")))));

        gameTowers.add(new Tower("Tower9",
                "Description9 contains description of properties about tower9" +
                        "so player can use tower9", 350 + costDifficultyFactor,
                TILE_SIZE * 5, 220, new Image(String.valueOf(
                        getClass().getResource("/images/tower9.png")))));
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
                            for (Iterator<Tower> iterator = playerTowers.iterator();
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
     *
     * Game container --> game pane --> tile stack panes
     */
    private class Tile extends StackPane {
        private int x;
        private int y;
        private boolean occupied;
        private Image background;

        private Rectangle rectangle;
        private List<Tile> currentTowerTiles;
        private boolean canPlace;

        public Tile(int x, int y, boolean occupied, Image background) {
            this.x = x;
            this.y = y;
            this.occupied = occupied;
            this.background = background;

            rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
            rectangle.setFill(new ImagePattern(background));
            getChildren().add(rectangle);
            this.setTranslateX(x);
            this.setTranslateY(y);

            currentTowerTiles = new ArrayList<>();

            setOnMouseEntered(mouseEvent -> {
                if (selectedTower != null) {
                    int towerSize = selectedTower.towerSize;
                    if (this.x + towerSize <= COLS * TILE_SIZE
                            && this.y + towerSize <= ROWS * TILE_SIZE) {
                        for (int i = 0; i < towerSize; i += TILE_SIZE) {
                            for (int j = 0; j < towerSize; j += TILE_SIZE) {
                                currentTowerTiles.add(tiles[((this.y + i) / TILE_SIZE) * COLS
                                        + ((this.x + j) / TILE_SIZE)]);
                            }
                        }
                        canPlace = true;
                        for (Tile tile: currentTowerTiles) {
                            tile.rectangle.setOpacity(0.7);
                            if (tile.occupied) {
                                canPlace = false;
                                break;
                            }
                        }
                        if (!canPlace) {
                            for (Tile tile: currentTowerTiles) {
                                tile.rectangle.setFill(Color.RED);
                                tile.rectangle.setOpacity(0.4);
                            }
                        }
                    }
                }
            });

            setOnMouseExited(mouseEvent -> {
                for (Tile tile: currentTowerTiles) {
                    tile.rectangle.setFill(new ImagePattern(tile.background));
                    tile.rectangle.setOpacity(1.0);
                }
                currentTowerTiles = new ArrayList<>();
            });

            setOnMouseClicked(mouseEvent -> {
                if (selectedTower != null && canPlace) {
                    if (money >= selectedTower.cost) {
                        money = money - selectedTower.cost;
                        moneyLabel.setText(money + "");
                        playerTowers.add(new Tower(selectedTower.name,
                                selectedTower.description, selectedTower.cost, x, y,
                                selectedTower.towerSize, selectedTower.maxHealth,
                                selectedTower.background, currentTowerTiles));
                        for (Tile tile: currentTowerTiles) {
                            tile.occupied = true;
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Insufficient Funds");
                        alert.setContentText("You do not have the money required" +
                                "to buy this tower!");
                        alert.show();
                    }
                }
            });

            gamePane.getChildren().add(this);
        }
    }

    /**
     * Defines a single tower with x-y coordinates, a background image,
     * a given size (since towers can take up multiple tiles), and health variables.
     *
     * Game container --> game pane --> tower stack panes
     */
    private class Tower extends StackPane {
        private String name;
        private String description;
        private int cost;
        private int x;
        private int y;
        private int towerSize;
        private Image background;
        private double maxHealth;
        private double curHealth;
        private ProgressBar healthBar;
        private List<Tile> onTiles;

        public Tower(String name, String description, int cost, int towerSize,
                     double maxHealth, Image background) {
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.towerSize = towerSize;
            this.maxHealth = maxHealth;
            this.curHealth = maxHealth;
            this.background = background;
        }

        public Tower(String name, String description, int cost, int x, int y,
                     int towerSize, double maxHealth, Image background, List<Tile> onTiles) {
            this(name, description, cost, towerSize, maxHealth, background);
            this.x = x;
            this.y = y;
            this.onTiles = onTiles;

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
            playerTowers.remove(this);
            for (Tile tile: this.onTiles) {
                tile.occupied = false;
            }
        }
    }
}
