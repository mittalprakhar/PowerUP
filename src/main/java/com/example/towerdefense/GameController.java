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
    private double monumentMaxHealth;           // Starting monument health
    private double monumentCurHealth;           // Current monument health
    private Location monumentLocation;          // Monument center location

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

    private List<Location> spawnPoints;         // List of all spawn points in the map
    private List<Integer> spawnHeadings;        // List of headings corresponding to spawn points
    private List<Enemy> movingEnemies;          // List of all enemies still moving
    private List<Enemy> reachedEnemies;         // List of all enemies that have reached monument

    private Random rand;

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
        spawnPoints = new ArrayList<>();
        spawnHeadings = new ArrayList<>();
        movingEnemies = new ArrayList<>();
        reachedEnemies = new ArrayList<>();
        rand = new Random();

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

        // M3 Test IDs
        /*
        tiles[21].setId("tilePath");
        tiles[60].setId("tileGround1");
        tiles[63].setId("tileGround2");
        tiles[66].setId("tileGround3");
        tiles[69].setId("tileGround4");
        */

        // Initialize dependent game variables
        playerLabel.setText(String.valueOf(configParams.get("playerName")));

        // Starting difficulty
        String difficulty = String.valueOf(configParams.get("difficulty"));
        difficultyLabel.setText(difficulty);

        int costDifficultyFactor;

        switch (difficulty) {
        case "Beginner":
            money = 500;
            monumentMaxHealth = 1.0;
            costDifficultyFactor = 0;
            break;
        case "Moderate":
            money = 450;
            monumentMaxHealth = 0.9;
            costDifficultyFactor = 10;
            break;
        default:
            money = 400;
            monumentMaxHealth = 0.8;
            costDifficultyFactor = 20;
            break;
        }
        monumentCurHealth = monumentMaxHealth;
        moneyLabel.setText(money + "");

        // Initialize monument health bar
        monumentBar.setProgress(monumentMaxHealth);
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
        int x = s.nextInt() - 1;
        int y = s.nextInt() - 1;
        monumentBar.setTranslateX(TILE_SIZE * x);
        monumentBar.setTranslateY(TILE_SIZE * y);
        monumentBar.setPrefWidth(TILE_SIZE * s.nextInt());

        // Set location of monument
        monumentLocation = new Location(TILE_SIZE * (x + 2), TILE_SIZE * (y + 5));

        // Load spawn points
        int spawnPointCount = s.nextInt();
        for (int i = 0; i < spawnPointCount; i++) {
            spawnPoints.add(new Location(TILE_SIZE * (s.nextInt() - 1),
                    TILE_SIZE * (s.nextInt() - 1)));
            spawnHeadings.add(s.nextInt());
        }

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
                50 + costDifficultyFactor, TILE_SIZE * 2, 30));

        gameTowers.add(new Tower("Spiky",
                "Spikes troops when they are not looking.",
                75 + costDifficultyFactor, TILE_SIZE * 2, 40));

        gameTowers.add(new Tower("Bomber",
                "Hurls bombs and wreaks havoc upon attackers.",
                100 + costDifficultyFactor, TILE_SIZE * 3, 50));

        gameTowers.add(new Tower("Wizard",
                "Hypnotizes fighters into surrendering.",
                130 + costDifficultyFactor, TILE_SIZE * 3, 70));

        gameTowers.add(new Tower("Xbow",
                "Chips away attackers at a blistering pace.",
                160 + costDifficultyFactor, TILE_SIZE * 3, 90));

        gameTowers.add(new Tower("Electro",
                "Stuns enemies through the power of electrons.",
                200 + costDifficultyFactor, TILE_SIZE * 4, 110));

        gameTowers.add(new Tower("Drone",
                "Drops deadly artillery from the skies.",
                250 + costDifficultyFactor, TILE_SIZE * 4, 140));

        gameTowers.add(new Tower("Tank",
                "Shoots shells that will impale enemies.",
                300 + costDifficultyFactor, TILE_SIZE * 5, 170));

        gameTowers.add(new Tower("Missile",
                "Obliterates anything and everything.",
                350 + costDifficultyFactor, TILE_SIZE * 5, 200));
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
                    cell.setId("gameTower" + (gameTowers.indexOf(tower) + 1));

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
            private long lastEnemySpawned;
            private long lastEnemyMoved;

            @Override
            public void handle(long now) {
                // Every 1 second, updates timer and tower health
                if (lastTimeUpdate == 0L) {
                    lastTimeUpdate = now;
                } else {
                    long diff = now - lastTimeUpdate;
                    if (diff >= 1_000_000_000L) {
                        updateTime();
                        updateTowers();
                        updateMonument();
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

                // Every 3 seconds, spawn enemy
                if (lastEnemySpawned == 0L) {
                    lastEnemySpawned = now;
                } else {
                    long diff = now - lastEnemySpawned;
                    if (diff >= 3_000_000_000L) {
                        spawnEnemy();
                        lastEnemySpawned = now;
                    }
                }

                // Move enemies
                if (lastEnemyMoved == 0L) {
                    lastEnemyMoved = now;
                } else {
                    long diff = now - lastEnemyMoved;
                    if (diff >= 10_000_000L) {
                        moveEnemies();
                        lastEnemyMoved = now;
                    }
                }
            }
        };

        gameLoop.start();
    }

    /**
     * Updates time
     */
    public void updateTime() {
        if (time > 0) {
            time--;
            timeLabel.setText(time / 60 + ":"
                    + new DecimalFormat("00").format(time % 60));
        }
    }

    /**
     * Adds money
     */
    public void addMoney() {
        money += 20;
        moneyLabel.setText(money + "");
    }

    /**
     * Spawns enemy
     */
    public void spawnEnemy() {
        int index = rand.nextInt(spawnPoints.size());
        movingEnemies.add(new Enemy(spawnPoints.get(index), spawnHeadings.get(index), TILE_SIZE));
    }

    /**
     * Move all alive enemies
     */
    public void moveEnemies() {
        try {
            for (Enemy enemy: movingEnemies) {
                enemy.move();
            }
        } catch (ConcurrentModificationException ignored) {}
    }

    /**
     * Updates health of towers
     */
    public void updateTowers() {
        try {
            for (Tower tower: playerTowers) {
                tower.updateHealth();
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    /**
     * Updates health of monument
     */
    public void updateMonument() {
        try {
            for (Enemy enemy: reachedEnemies) {
                enemy.damageMonument();
            }
        } catch (ConcurrentModificationException ignored) {}
    }

    /**
     * Defines a single tile with x-y coordinates, a background image,
     * and a boolean to track if the tile is occupied by a path/building or not.
     *
     * Game container --> game pane --> tile stack panes
     */
    private class Tile extends StackPane {
        private final Location location;
        private final Image background;
        private final boolean isPath;

        private final Rectangle rectangle;
        private List<Tile> currentTowerTiles;
        private boolean occupied;
        private boolean canPlace;

        public Tile(Location location, boolean isPath, Image background) {
            this.location = location;
            this.isPath = isPath;
            this.occupied = isPath;
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
                    canPlace = true;
                    if (this.location.x + towerSize <= COLS * TILE_SIZE
                            && this.location.y + towerSize <= ROWS * TILE_SIZE) {
                        for (int i = 0; i < towerSize; i += TILE_SIZE) {
                            for (int j = 0; j < towerSize; j += TILE_SIZE) {
                                Tile tile = tiles[(int) ((this.location.y + i) / TILE_SIZE
                                        * COLS + (this.location.x + j) / TILE_SIZE)];
                                tile.rectangle.setOpacity(0.7);
                                if (tile.occupied) {
                                    canPlace = false;
                                }
                                currentTowerTiles.add(tile);
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
                        Tower playerTower = new Tower(selectedTower.name,
                                selectedTower.description, selectedTower.cost,
                                selectedTower.towerSize, selectedTower.maxHealth,
                                currentTowerTiles);
                        playerTower.setId("playerTower" + (playerTowers.size() + 1));
                        playerTowers.add(playerTower);
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

        @Override
        public String toString() {
            return String.format("Name: %s, Cost: %d, Max Health: %f, Cur Health: %f, "
                    + "Location: %s", name, cost, maxHealth, curHealth, location);
        }
    }

    /**
     * Defines a location object associated with a tile or a tower object.
     */
    private static class Location {
        private final double x;
        private final double y;

        public Location(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(%f, %f)", x, y);
        }
    }

    private class Enemy extends StackPane {
        private Location location;
        private final ProgressBar healthBar;

        private int heading;
        private final double speed;

        private int tileIndex;

        public Enemy(Location location, int heading, double speed) {
            this.location = location;
            this.speed = speed;
            this.heading = heading;

            Rectangle border = new Rectangle(TILE_SIZE * 2, TILE_SIZE * 2);
            border.setFill(new ImagePattern(new Image(String.valueOf(
                    getClass().getResource("/images/enemy.png")))));
            getChildren().add(border);
            this.setTranslateX(location.x);
            this.setTranslateY(location.y);
            gamePane.getChildren().add(this);

            healthBar = new ProgressBar();
            healthBar.setProgress(1);
            healthBar.setTranslateX(location.x + TILE_SIZE * 0.2);
            healthBar.setTranslateY(location.y - TILE_SIZE * 0.65);
            healthBar.setPrefWidth(TILE_SIZE * 1.6);
            healthBar.setPrefHeight(TILE_SIZE * 0.55);
            gamePane.getChildren().add(healthBar);
        }

        public void move() {
            boolean equalsMonumentX = monumentLocation.x - TILE_SIZE <= location.x
                    && location.x <= monumentLocation.x + TILE_SIZE;
            if (equalsMonumentX
                    && monumentLocation.y - TILE_SIZE <= location.y
                    && location.y <= monumentLocation.y + TILE_SIZE) {
                movingEnemies.remove(this);
                reachedEnemies.add(this);
                return;
            }

            tileIndex = (int) (location.y / TILE_SIZE)
                    * COLS + (int) (location.x / TILE_SIZE);

            List<Integer> possibleHeadings = new ArrayList<>();

            if (checkForward()) {
                possibleHeadings.add(2);
            }
            if (checkBackward()) {
                possibleHeadings.add(4);
            }
            if (checkDown()) {
                possibleHeadings.add(3);
            }
            if (checkUp()) {
                possibleHeadings.add(1);
            }

            if (possibleHeadings.contains(2) && possibleHeadings.contains(4)) {
                if (location.x < monumentLocation.x) {
                    possibleHeadings.remove(Integer.valueOf(4));
                } else {
                    possibleHeadings.remove(Integer.valueOf(2));
                }
            }
            if (equalsMonumentX
                    && possibleHeadings.contains(1) && possibleHeadings.contains(3)) {
                if (location.y < monumentLocation.y) {
                    possibleHeadings.remove(Integer.valueOf(1));
                } else {
                    possibleHeadings.remove(Integer.valueOf(3));
                }
            }
            if (possibleHeadings.size() > 1) {
                if (heading == 1 && possibleHeadings.contains(3)) {
                    possibleHeadings.remove(Integer.valueOf(3));
                } else if (heading == 3 && possibleHeadings.contains(1)) {
                    possibleHeadings.remove(Integer.valueOf(1));
                }
            }
            if (possibleHeadings.size() > 1) {
                if (heading == 2 && possibleHeadings.contains(4)) {
                    possibleHeadings.remove(Integer.valueOf(4));
                } else if (heading == 4 && possibleHeadings.contains(2)) {
                    possibleHeadings.remove(Integer.valueOf(2));
                }
            }

            heading = possibleHeadings.get(rand.nextInt(possibleHeadings.size()));
            updateLocation();
        }

        private boolean checkForward() {
            try {
                return tiles[tileIndex + 2].isPath
                        && tiles[tileIndex + 2 + COLS].isPath;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean checkBackward() {
            try {
                return tiles[tileIndex - 1].isPath
                        && tiles[tileIndex - 1 + COLS].isPath;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean checkUp() {
            try {
                return tiles[tileIndex - COLS].isPath
                        && tiles[tileIndex - COLS + 1].isPath;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean checkDown() {
            try {
                return tiles[tileIndex + (2 * COLS)].isPath
                        && tiles[tileIndex + (2 * COLS) + 1].isPath;
            } catch (Exception e) {
                return false;
            }
        }

        private void updateLocation() {
            switch (heading) {
                case 1:
                    location = new Location(location.x, location.y - speed);
                    break;
                case 2:
                    location = new Location(location.x + speed, location.y);
                    break;
                case 3:
                    location = new Location(location.x, location.y + speed);
                    break;
                case 4:
                    location = new Location(location.x - speed, location.y);
                    break;
                default:
                    break;
            }
            this.setTranslateX(location.x);
            this.setTranslateY(location.y);
            healthBar.setTranslateX(location.x + TILE_SIZE * 0.2);
            healthBar.setTranslateY(location.y - TILE_SIZE * 0.65);
        }

        public void damageMonument() {
            if (monumentCurHealth > 0) {
                monumentCurHealth -= 0.001;
                monumentBar.setProgress(monumentCurHealth / monumentMaxHealth);
            } /*else {
                this.stop();
                endButton.fire();
            }*/
        }
    }
}
