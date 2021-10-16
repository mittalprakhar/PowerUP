package com.example.towerdefense;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
        time = 300;
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
        // Array of tile backgrounds
        int[] tileImages = readMap("src/main/resources/maps/"
                + configParams.get("mapName").toString().toLowerCase() + ".txt");

        // Initialize tiles
        Set<Integer> ground = new HashSet<>(Arrays.asList(0, 5, 6));
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile(new Location(TILE_SIZE * (i % COLS), TILE_SIZE * (i / COLS)),
                    !ground.contains(tileImages[i]), new Image(String.valueOf(getClass().
                    getResource("/images/tile" + tileImages[i] + ".png"))));
        }

        // Initialize dependent game variables
        playerLabel.setText(String.valueOf(configParams.get("playerName")));

        // Starting difficulty
        String difficulty = String.valueOf(configParams.get("difficulty"));
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

        // Initialize towerMenu with gameTowers
        initializeTowerMenu();

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
     *
     * @param gameTowers the observable list to fill all game towers with
     * @param costDifficultyFactor the amount by which the cost of each tower increases
     *                             because of the difficulty
     */
    private void initializeGameTowers(ObservableList<Tower> gameTowers,
                                      int costDifficultyFactor) {
        gameTowers.add(new Tower("Cannon",
                "Fires cannon balls to crush enemies.",
                50 + costDifficultyFactor, TILE_SIZE * 2, 50));

        gameTowers.add(new Tower("Spiky",
                "Spikes troops when they are not looking.",
                75 + costDifficultyFactor, TILE_SIZE * 2, 60));

        gameTowers.add(new Tower("Bomber",
                "Hurls bombs and wreaks havoc upon attackers.",
                100 + costDifficultyFactor, TILE_SIZE * 3, 70));

        gameTowers.add(new Tower("Wizard",
                "Hypnotizes fighters into surrendering.",
                130 + costDifficultyFactor, TILE_SIZE * 3, 90));

        gameTowers.add(new Tower("Xbow",
                "Chips away attackers at a blistering pace.",
                160 + costDifficultyFactor, TILE_SIZE * 3, 110));

        gameTowers.add(new Tower("Electro",
                "Stuns enemies through the power of electrons.",
                200 + costDifficultyFactor, TILE_SIZE * 4, 130));

        gameTowers.add(new Tower("Drone",
                "Drops deadly artillery from the skies.",
                250 + costDifficultyFactor, TILE_SIZE * 4, 160));

        gameTowers.add(new Tower("Tank",
                "Shoots shells that will impale enemies.",
                300 + costDifficultyFactor, TILE_SIZE * 5, 190));

        gameTowers.add(new Tower("Missile",
                "Obliterates anything and everything.",
                350 + costDifficultyFactor, TILE_SIZE * 5, 220));
    }

    /**
     * Initializes tower menu with available game towers.
     */
    private void initializeTowerMenu() {
        towerMenu.setCellFactory(listCell -> new ListCell<>() {
            @Override
            protected void updateItem(Tower tower, boolean empty) {
                super.updateItem(tower, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Create cell to hold each tower with complete info
                    HBox cell = new HBox();
                    cell.setAlignment(Pos.CENTER);
                    cell.prefWidthProperty().bind(towerMenu.widthProperty().subtract(30));
                    cell.maxWidthProperty().bind(towerMenu.widthProperty().subtract(30));
                    cell.setSpacing(7);

                    // Create left box to hold each tower's name and image
                    VBox leftBox = new VBox();
                    leftBox.setAlignment(Pos.CENTER);
                    leftBox.setMinWidth(60);
                    leftBox.setPadding(new Insets(0, 0, 5, 0));
                    leftBox.setSpacing(3);

                    // Create label with tower name
                    Label towerName = new Label(tower.name);
                    towerName.setFont(new Font("System Bold", 13.0));

                    // Create tower image
                    ImageView towerImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/tower" + tower.name + ".png"))));
                    towerImage.setFitWidth(45);
                    towerImage.setFitHeight(45);
                    towerImage.setPreserveRatio(true);

                    // Fill left box with tower name and image
                    leftBox.getChildren().addAll(towerName, towerImage);

                    // Create right box to hold each tower's description and stats
                    VBox rightBox = new VBox();
                    rightBox.setAlignment(Pos.CENTER_LEFT);
                    rightBox.setPadding(new Insets(0, 3, 11, 3));
                    rightBox.setSpacing(5);
                    rightBox.prefHeightProperty().bind(leftBox.heightProperty());

                    // Create label with tower description
                    Label towerDescription = new Label(tower.description);
                    towerDescription.setWrapText(true);

                    // Create tower stats box
                    HBox towerStats = new HBox();
                    towerStats.setPrefHeight(20);
                    towerStats.setAlignment(Pos.CENTER_LEFT);

                    // Create cost image
                    ImageView costImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuMoney.png"))));
                    costImage.setFitWidth(15);
                    costImage.setFitHeight(15);
                    costImage.setPreserveRatio(true);

                    // Create cost label
                    Label costLabel = new Label(tower.cost + "");
                    costLabel.setPadding(new Insets(0, 13, 0, 3));

                    // Create health image
                    ImageView healthImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuHealth.png"))));
                    healthImage.setFitWidth(15);
                    healthImage.setFitHeight(15);
                    healthImage.setPreserveRatio(true);

                    // Create health label
                    Label healthLabel = new Label((int) tower.maxHealth + "");
                    healthLabel.setPadding(new Insets(0, 13, 0, 3));

                    // Create damage image
                    ImageView damageImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuDamage.png"))));
                    damageImage.setFitWidth(15);
                    damageImage.setFitHeight(15);
                    damageImage.setPreserveRatio(true);

                    // Create damage label
                    Label damageLabel = new Label((int) (tower.maxHealth / 20 * (tower.cost / 40))
                            + "");
                    damageLabel.setPadding(new Insets(0, 13, 0, 3));

                    // Fill tower stats box with stats
                    towerStats.getChildren().addAll(costImage, costLabel, healthImage,
                            healthLabel, damageImage, damageLabel);

                    // Fill right box with tower description and stats
                    rightBox.getChildren().addAll(towerDescription, towerStats);

                    // Fill cell with left and right boxes
                    cell.getChildren().addAll(leftBox, rightBox);

                    // Set the HBox as the display
                    setGraphic(cell);
                }
            }
        });

        // Bind our list of pieces to the ListView
        towerMenu.setItems(gameTowers);

        // Add listener to track which tower is currently selected by player
        towerMenu.setOnMouseClicked(mouseEvent -> {
            Tower tower = towerMenu.getSelectionModel().getSelectedItem();
            if (tower == selectedTower) {
                towerMenu.getSelectionModel().clearSelection();
                selectedTower = null;
            } else {
                selectedTower = tower;
            }
        });
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
                            for (Tower tower: playerTowers) {
                                tower.updateHealth();
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
        private final Location location;
        private boolean occupied;
        private final Image background;

        private final Rectangle rectangle;
        private List<Tile> currentTowerTiles;
        private boolean canPlace;

        public Tile(Location location, boolean occupied, Image background) {
            this.location = location;
            this.occupied = occupied;
            this.background = background;

            rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
            rectangle.setFill(new ImagePattern(background));
            getChildren().add(rectangle);
            this.setTranslateX(location.x);
            this.setTranslateY(location.y);

            currentTowerTiles = new ArrayList<>();

            setOnMouseEntered(mouseEvent -> {
                if (selectedTower != null) {
                    int towerSize = selectedTower.towerSize;
                    if (this.location.x + towerSize <= COLS * TILE_SIZE
                            && this.location.y + towerSize <= ROWS * TILE_SIZE) {
                        for (int i = 0; i < towerSize; i += TILE_SIZE) {
                            for (int j = 0; j < towerSize; j += TILE_SIZE) {
                                currentTowerTiles.add(tiles[
                                        ((this.location.y + i) / TILE_SIZE) * COLS
                                        + ((this.location.x + j) / TILE_SIZE)]);
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
                                selectedTower.description, selectedTower.cost,
                                selectedTower.towerSize, selectedTower.maxHealth,
                                currentTowerTiles));
                        for (Tile tile: currentTowerTiles) {
                            tile.occupied = true;
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Insufficient Funds");
                        alert.setContentText("You do not have the money required"
                                + " to buy this tower!");

                        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(new Image(String.valueOf(getClass().getResource(
                                "/images/towerSpiky.png"))));

                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(String.valueOf(getClass().getResource(
                                "/css/main.css")));

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
        private final String name;
        private final String description;
        private final int cost;
        private final int towerSize;
        private final double maxHealth;
        private List<Tile> onTiles;

        private Location location;
        private double curHealth;
        private ProgressBar healthBar;

        public Tower(String name, String description, int cost, int towerSize,
                     double maxHealth) {
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.towerSize = towerSize;
            this.maxHealth = maxHealth;
            this.curHealth = maxHealth;
        }

        public Tower(String name, String description, int cost, int towerSize,
                     double maxHealth, List<Tile> onTiles) {
            this(name, description, cost, towerSize, maxHealth);
            this.onTiles = onTiles;
            this.location = onTiles.get(0).location;

            Rectangle border = new Rectangle(towerSize, towerSize);
            border.setFill(new ImagePattern(new Image(String.valueOf(
                    getClass().getResource("/images/tower" + this.name + ".png")))));
            getChildren().add(border);
            this.setTranslateX(location.x);
            this.setTranslateY(location.y);
            gamePane.getChildren().add(this);

            healthBar = new ProgressBar();
            healthBar.setProgress(1);
            healthBar.setTranslateX(location.x);
            healthBar.setTranslateY(location.y - TILE_SIZE);
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

    /**
     * Defines a location object associated with a tile or a tower object.
     */
    private static class Location {
        private final int x;
        private final int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
