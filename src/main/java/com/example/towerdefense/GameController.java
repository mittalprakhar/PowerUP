package com.example.towerdefense;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import java.io.IOException;
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
    private int time = 240;                     // Starting time in seconds

    @FXML
    private Label moneyLabel;                   // Money label in side menu
    private int money;                          // Starting money
    private int moneyused;                      // Money used during the game

    @FXML
    private Label killsLabel;                   // Kills label in side menu
    private int kills = 0;                      // Starting kills

    @FXML
    private ListView<Tower> towerMenu;          // Tower list view in side menu
    private ObservableList<Tower> gameTowers;   // List of towers in game

    private Tower selectedTower;                // Currently selected tower from the menu
    private List<Tower> playerTowers;           // List of all towers placed by player

    private List<Location> spawnPoints;         // List of all spawn points in the map
    private List<Integer> spawnHeadings;        // List of headings corresponding to spawn points
    private List<Enemy> movingEnemies;          // List of all enemies still moving
    private List<Enemy> reachedEnemies;         // List of all enemies that have reached monument

    private List<Enemy> gameEnemies;

    private Random rand;                        // Random object

    @FXML
    private Button gameButton;                  // Button to start combat or surrender
    private boolean isStarted = false;          // Game started or not
    private boolean won = false;

    private AnimationTimer gameLoop;            // Game loop animation timer

    private int enemyCounter = 1;
    private boolean spawnedFinalBoss = false;

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
        gameEnemies = new ArrayList<>();
        rand = new Random();

        // Set time and kills labels
        timeLabel.setText(time / 60 + ":"
                + new DecimalFormat("00").format(time % 60));
        killsLabel.setText(kills + "");
        moneyused = 0;
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

        // Test IDs
        tiles[21].setId("tilePath");
        tiles[60].setId("tileGround1");
        tiles[63].setId("tileGround2");
        tiles[66].setId("tileGround3");
        tiles[69].setId("tileGround4");
        tiles[1000].setId("tileNearMonument");
        tiles[2000].setId("tileGround5");

        // Initialize dependent game variables
        playerLabel.setText(String.valueOf(configParams.get("playerName")));

        // Starting difficulty
        String difficulty = String.valueOf(configParams.get("difficulty"));
        difficultyLabel.setText(difficulty);

        int costDifficultyFactor;
        switch (difficulty) {
        case "Beginner":
            money = 500;
            monumentMaxHealth = 3000;
            costDifficultyFactor = 0;
            break;
        case "Moderate":
            money = 450;
            monumentMaxHealth = 2000;
            costDifficultyFactor = 10;
            break;
        default:
            money = 400;
            monumentMaxHealth = 1000;
            costDifficultyFactor = 20;
            break;
        }
        monumentCurHealth = monumentMaxHealth;
        moneyLabel.setText(money + "");

        // Initialize monument health bar
        monumentBar.setProgress(monumentMaxHealth / 800);
        monumentBar.setId("monumentHealth");
        gamePane.getChildren().add(monumentBar);

        // Initialize gameTowers with all available game towers
        initializeGameTowers(gameTowers, costDifficultyFactor);

        // Initialize gameEnemies with all available game enemies
        initializeGameEnemies(gameEnemies);

        // Initialize towerMenu with gameTowers
        initializeTowerMenu();
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
     * Initializes gameEnemies with all available game enemies.
     * @param gameEnemies list of enemies
     */
    private void initializeGameEnemies(List<Enemy> gameEnemies) {
        int index = rand.nextInt(spawnPoints.size());
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                0.5, 10, 1, 1));
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                0.5, 15, 2, 2));
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                0.5, 20, 3, 3));
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                0.5, 30, 4, 4));
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                0.5, 50, 5, 5));
        gameEnemies.add(new Enemy(spawnHeadings.get(index),
                1.0, 500, 15, 6));
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
                50 + costDifficultyFactor, TILE_SIZE * 2, 30, 2));

        gameTowers.add(new Tower("Spiky",
                "Spikes troops when they are not looking.",
                75 + costDifficultyFactor, TILE_SIZE * 2, 40, 4));

        gameTowers.add(new Tower("Bomber",
                "Hurls bombs and wreaks havoc upon attackers.",
                100 + costDifficultyFactor, TILE_SIZE * 3, 50, 6));

        gameTowers.add(new Tower("Wizard",
                "Hypnotizes fighters into surrendering.",
                130 + costDifficultyFactor, TILE_SIZE * 3, 70, 8));

        gameTowers.add(new Tower("Xbow",
                "Chips away attackers at a blistering pace.",
                160 + costDifficultyFactor, TILE_SIZE * 3, 90, 10));

        gameTowers.add(new Tower("Electro",
                "Stuns enemies through the power of electrons.",
                200 + costDifficultyFactor, TILE_SIZE * 4, 110, 12));

        gameTowers.add(new Tower("Drone",
                "Drops deadly artillery from the skies.",
                250 + costDifficultyFactor, TILE_SIZE * 4, 140, 14));

        gameTowers.add(new Tower("Tank",
                "Shoots shells that will impale enemies.",
                300 + costDifficultyFactor, TILE_SIZE * 5, 170, 18));

        gameTowers.add(new Tower("Missile",
                "Obliterates anything and everything.",
                350 + costDifficultyFactor, TILE_SIZE * 5, 200, 20));
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
                    cell.setSpacing(5);
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
                    towerImage.setFitWidth(43);
                    towerImage.setFitHeight(43);
                    towerImage.setPreserveRatio(true);

                    // Fill left box with tower name and image
                    leftBox.getChildren().addAll(towerName, towerImage);

                    // Create right box to hold each tower's description and stats
                    VBox rightBox = new VBox();
                    rightBox.setAlignment(Pos.CENTER_LEFT);
                    rightBox.setPadding(new Insets(3, 0, 8, 0));
                    rightBox.setSpacing(3);

                    // Create label with tower description
                    Label towerDescription = new Label(tower.description);
                    towerDescription.setWrapText(true);

                    // Create tower stats box
                    HBox towerStats = new HBox();
                    towerStats.setPrefHeight(25);
                    towerStats.setAlignment(Pos.CENTER_LEFT);

                    // Create cost image
                    ImageView costImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuMoney.png"))));
                    costImage.setFitWidth(13);
                    costImage.setFitHeight(13);
                    costImage.setPreserveRatio(true);

                    // Create cost label
                    Label costLabel = new Label(tower.cost + "");
                    costLabel.setPadding(new Insets(0, 11, 0, 2));

                    // Create health image
                    ImageView healthImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuHealth.png"))));
                    healthImage.setFitWidth(13);
                    healthImage.setFitHeight(13);
                    healthImage.setPreserveRatio(true);

                    // Create health label
                    Label healthLabel = new Label((int) tower.maxHealth + "");
                    healthLabel.setPadding(new Insets(0, 11, 0, 2));

                    // Create damage image
                    ImageView damageImage = new ImageView(new Image(String.valueOf(
                            getClass().getResource("/images/menuDamage.png"))));
                    damageImage.setFitWidth(13);
                    damageImage.setFitHeight(13);
                    damageImage.setPreserveRatio(true);

                    // Create damage label
                    Label damageLabel = new Label((int) tower.damagePerSecond + "");
                    damageLabel.setPadding(new Insets(0, 11, 0, 2));

                    Button upgradeButton = new Button();
                    upgradeButton.setId("upgrade" + (gameTowers.indexOf(tower) + 1));
                    upgradeButton.setAlignment(Pos.CENTER);
                    upgradeButton.getStyleClass().add("upgradeButton");
                    upgradeButton.setText("⬆");
                    upgradeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if (money >= tower.cost) {
                                money = money - tower.cost;
                                moneyused = moneyused + tower.cost;
                                moneyLabel.setText(String.valueOf(money));
                                tower.maxHealth = tower.maxHealth * 2;
                                healthLabel.setText((int) tower.maxHealth + "");
                                tower.damagePerSecond = tower.damagePerSecond * 2;
                                damageLabel.setText((int) tower.damagePerSecond + "");
                                upgradeButton.setText("✓");
                                upgradeButton.setDisable(true);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setHeaderText("Not enough money!");
                                alert.setContentText("You cannot upgrade the tower! Earn more Money!");

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

                    // Fill tower stats box with stats
                    towerStats.getChildren().addAll(costImage, costLabel, healthImage,
                            healthLabel, damageImage, damageLabel, upgradeButton);

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
            if (isStarted) {
                Tower tower = towerMenu.getSelectionModel().getSelectedItem();
                if (tower == selectedTower) {
                    towerMenu.getSelectionModel().clearSelection();
                    selectedTower = null;
                } else {
                    selectedTower = tower;
                }
            } else {
                // Show alert if the game is not yet started
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Game Not Started");
                alert.setContentText("You must start combat before buying towers!");

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(String.valueOf(getClass().getResource(
                        "/images/towerSpiky.png"))));

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(String.valueOf(getClass().getResource(
                        "/css/main.css")));

                alert.show();
            }
        });
    }

    /**
     * Handles gameplay logic
     * Has an animation timer that calls other gameplay methods when required
     */
    public void gameOn() {
        gameLoop = new AnimationTimer() {
            private long lastTimeUpdate;
            private long lastMoneyUpdate;
            private long lastEnemySpawned;
            private long lastEnemyMoved;

            @Override
            public void handle(long now) {
                // Every 1 second, update timer, tower, enemy, and monument health
                if (lastTimeUpdate == 0L) {
                    lastTimeUpdate = now;
                } else {
                    long diff = now - lastTimeUpdate;
                    if (diff >= 1_000_000_000L) {
                        updateTime();
                        updateTowers();
                        updateEnemies();
                        updateMonument();
                        checkGameStatus();
                        lastTimeUpdate = now;
                    }
                }

                // Every 10 seconds, add money
                if (lastMoneyUpdate == 0L) {
                    lastMoneyUpdate = now;
                } else {
                    long diff = now - lastMoneyUpdate;
                    if (diff >= 10_000_000_000L) {
                        addMoney();
                        lastMoneyUpdate = now;
                    }
                }

                if (time >= 120) {
                    // Every 5 seconds, spawn enemy
                    if (lastEnemySpawned == 0L) {
                        lastEnemySpawned = now;
                    } else {
                        long diff = now - lastEnemySpawned;
                        if (diff >= 5_000_000_000L) {
                            spawnEnemy();
                            lastEnemySpawned = now;
                        }
                    }
                } else if (!spawnedFinalBoss) {
                    spawnedFinalBoss = true;
                    spawnFinalBoss();
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
     * Checks status of the game
     */
    private void checkGameStatus() {
        if (time <= 0 || monumentCurHealth < 0.01) {
            gameButton.fire();
        }
        if (spawnedFinalBoss && movingEnemies.isEmpty() && reachedEnemies.isEmpty()) {
            won = true;
            gameButton.fire();
        }
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
     * Decreases health of towers
     */
    public void updateTowers() {
        try {
            for (Enemy enemy: movingEnemies) {
                for (Tower tower : playerTowers) {
                    if (tower.location.x >= enemy.location.x - enemy.range
                            && tower.location.x <= enemy.location.x + enemy.range
                            && tower.location.y >= enemy.location.y - enemy.range
                            && tower.location.y <= enemy.location.y + enemy.range) {
                        enemy.damageTower(tower);
                    }
                }
            }
            for (Enemy enemy: reachedEnemies) {
                for (Tower tower: playerTowers) {
                    if (tower.location.x >= enemy.location.x - enemy.range
                            && tower.location.x <= enemy.location.x + enemy.range
                            && tower.location.y >= enemy.location.y - enemy.range
                            && tower.location.y <= enemy.location.y + enemy.range) {
                        enemy.damageTower(tower);
                    }
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    /**
     * For every tower, decreases health of all enemies in range of tower
     */
    public void updateEnemies() {
        try {
            for (Tower tower: playerTowers) {
                double towerCenterX = tower.location.x + tower.towerSize / 2;
                double towerCenterY = tower.location.y + tower.towerSize / 2;
                for (Enemy enemy: movingEnemies) {
                    if (enemy.location.x >= towerCenterX - tower.range
                            && enemy.location.x <= towerCenterX + tower.range
                            && enemy.location.y >= towerCenterY - tower.range
                            && enemy.location.y <= towerCenterY + tower.range) {
                        tower.damageEnemy(enemy);
                    }
                }
                for (Enemy enemy: reachedEnemies) {
                    if (enemy.location.x >= towerCenterX - tower.range
                            && enemy.location.x <= towerCenterX + tower.range
                            && enemy.location.y >= towerCenterY - tower.range
                            && enemy.location.y <= towerCenterY + tower.range) {
                        tower.damageEnemy(enemy);
                    }
                }
            }
        } catch (ConcurrentModificationException ignored) { }
    }

    /**
     * For every reached enemy, decreases health of monument
     */
    public void updateMonument() {
        try {
            for (Enemy enemy: reachedEnemies) {
                enemy.damageMonument();
            }
        } catch (ConcurrentModificationException ignored) { }
    }

    /**
     * Adds money
     */
    public void addMoney() {
        String difficulty = difficultyLabel.getText();
        int moneyIncrement;
        switch (difficulty) {
        case "Beginner":
            moneyIncrement = 50;
            break;
        case "Moderate":
            moneyIncrement = 40;
            break;
        default:
            moneyIncrement = 20;
            break;
        }
        money += moneyIncrement;
        moneyLabel.setText(money + "");
    }

    /**
     * Spawns enemy
     */
    public void spawnEnemy() {
        int randomEnemyType = rand.nextInt(101);
        if (randomEnemyType < 45) {
            randomEnemyType = 0;
        } else if (randomEnemyType < 75) {
            randomEnemyType = 1;
        } else if (randomEnemyType < 90) {
            randomEnemyType = 2;
        } else if (randomEnemyType < 97) {
            randomEnemyType = 3;
        } else {
            randomEnemyType = 4;
        }
        int randomSpawnPoint = rand.nextInt(spawnPoints.size());
        Enemy tmp = gameEnemies.get(randomEnemyType);
        movingEnemies.add(new Enemy(spawnPoints.get(randomSpawnPoint),
                spawnHeadings.get(randomSpawnPoint),
                tmp.speed, tmp.maxHealth, tmp.damagePerSecond, randomEnemyType + 1));
    }

    /**
     * Spawns final boss
     */
    public void spawnFinalBoss() {
        int randomEnemyType = 5;
        int randomSpawnPoint = rand.nextInt(spawnPoints.size());
        Enemy tmp = gameEnemies.get(randomEnemyType);
        movingEnemies.add(new Enemy(spawnPoints.get(randomSpawnPoint),
                spawnHeadings.get(randomSpawnPoint),
                tmp.speed, tmp.maxHealth, tmp.damagePerSecond, randomEnemyType + 1));
    }

    /**
     * Move all alive enemies
     */
    public void moveEnemies() {
        try {
            for (Enemy enemy: movingEnemies) {
                enemy.move();
            }
        } catch (ConcurrentModificationException ignored) { }
    }

    /**
     * Handler for start game / surrender button
     *
     * @throws IOException if fxml file is not present
     */
    @FXML
    public void onGameButtonClick() throws IOException {
        if (!isStarted) {
            isStarted = true;
            gameOn();
            gameButton.setText("Surrender");
        } else {
            gameLoop.stop();

            Stage primaryStage = Main.getPrimaryStage();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/views/game-over-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            scene.getStylesheets().add(String.valueOf(getClass().getResource(
                    "/css/main.css")));

            java.util.Map<String, Object> gameParams = new HashMap<>();
            gameParams.put("playerName", playerLabel.getText());
            gameParams.put("kills", killsLabel.getText());
            gameParams.put("MoneyUsed", String.valueOf(moneyused));
            gameParams.put("result", won);
            gameParams.put("time",String.valueOf(240-time));

            GameOverController gameOverController = fxmlLoader.getController();
            gameOverController.initState(gameParams);

            primaryStage.setScene(scene);
        }
    }

    /**
     * Defines a tile object
     */
    private class Tile extends StackPane {
        private final Location location;
        private final Image background;
        private final Rectangle rectangle;

        private List<Tile> currentTowerTiles;

        private final boolean isPath;
        private boolean isOccupied;
        private boolean canPlace;

        public Tile(Location location, boolean isPath, Image background) {
            this.location = location;
            this.isPath = isPath;
            this.isOccupied = isPath;
            this.background = background;

            rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
            rectangle.setFill(new ImagePattern(background));
            this.getChildren().add(rectangle);
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
                                if (tile.isOccupied) {
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
                        moneyused = moneyused + selectedTower.cost;
                        moneyLabel.setText(money + "");
                        Tower playerTower = new Tower(selectedTower.name,
                                selectedTower.description, selectedTower.cost,
                                selectedTower.towerSize, selectedTower.maxHealth,
                                selectedTower.damagePerSecond, currentTowerTiles);
                        playerTower.setId("playerTower" + (playerTowers.size() + 1));
                        playerTowers.add(playerTower);
                        for (Tile tile: currentTowerTiles) {
                            tile.isOccupied = true;
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

        public String toString() {
            return String.format("Location: %s, Path: %s, Occupied: %s",
                    location, isPath ? "True" : "False", isOccupied ? "True" : "False");
        }
    }

    /**
     * Defines a tower object
     */
    private class Tower extends StackPane {
        private final String name;
        private final String description;
        private final int cost;
        private double damagePerSecond;

        private final int towerSize;
        private Location location;
        private List<Tile> onTiles;

        private double maxHealth;
        private double curHealth;
        private ProgressBar healthBar;
        private boolean towerUpgrade;

        private final double range;

        public Tower(String name, String description, int cost, int towerSize,
                     double maxHealth, double damagePerSecond) {
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.towerSize = towerSize;
            this.maxHealth = maxHealth;
            this.curHealth = maxHealth;
            this.damagePerSecond = damagePerSecond;
            this.range = towerSize * 5;
        }

        public Tower(String name, String description, int cost, int towerSize,
                     double maxHealth, double damagePerSecond, List<Tile> onTiles) {
            this(name, description, cost, towerSize, maxHealth, damagePerSecond);
            this.onTiles = onTiles;
            this.location = onTiles.get(0).location;

            Rectangle border = new Rectangle(towerSize, towerSize);
            border.setFill(new ImagePattern(new Image(String.valueOf(
                    getClass().getResource("/images/tower" + this.name + ".png")))));
            this.getChildren().add(border);
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

        public void decreaseHealth(double change) {
            if (curHealth > 0) {
                curHealth -= change;
                healthBar.setProgress(Math.max(0, curHealth / maxHealth));
            } else {
                destroy();
            }
        }

        public void damageEnemy(Enemy enemy) {
            enemy.decreaseHealth(damagePerSecond);
        }

        private void destroy() {
            gamePane.getChildren().remove(this);
            gamePane.getChildren().remove(healthBar);
            playerTowers.remove(this);
            for (Tile tile: this.onTiles) {
                tile.isOccupied = false;
            }
        }

        @Override
        public String toString() {
            return String.format("Name: %s, Cost: %d, Max Health: %f, Cur Health: %f,"
                    + "Location: %s, DPS: %f, Range: %f", name, cost, maxHealth,
                    curHealth, location, damagePerSecond, range);
        }
    }

    /**
     * Defines a location object
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

    /**
     * Defines an enemy object
     */
    private class Enemy extends StackPane {
        private Location location;
        private int tileIndex;

        private int heading;
        private final double speed;

        private final double maxHealth;
        private double curHealth;
        private final ProgressBar healthBar;
        private final double damagePerSecond;

        private final int type;
        private final double range;

        public Enemy(int heading, double speed, double maxHealth,
                     double damagePerSecond, int type) {
            this.heading = heading;
            this.speed = speed;
            this.maxHealth = maxHealth;
            this.curHealth = maxHealth;
            this.damagePerSecond = damagePerSecond;
            this.type = type;
            this.range = 10 * TILE_SIZE * 2 + (0.2 * type * TILE_SIZE);
            healthBar = new ProgressBar();
        }

        public Enemy(Location location, int heading, double speed,
                     double maxHealth, double damagePerSecond, int type) {
            this(heading, speed, maxHealth, damagePerSecond, type);
            this.location = location;

            Rectangle border = new Rectangle(TILE_SIZE * 2, TILE_SIZE * 2);
            border.setFill(new ImagePattern(new Image(String.valueOf(
                    getClass().getResource("/images/enemy" + this.type + ".png")))));
            this.getChildren().add(border);
            this.setTranslateX(location.x);
            this.setTranslateY(location.y);
            this.setId("enemy" + enemyCounter++);
            gamePane.getChildren().add(this);

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
                this.setId(getId() + "reached");
                return;
            }

            if (location.y % TILE_SIZE == 0 && location.x % TILE_SIZE == 0) {
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
            }

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

        public void decreaseHealth(double change) {
            if (curHealth > 0) {
                curHealth -= change;
                healthBar.setProgress(Math.max(0, curHealth / maxHealth));
            } else {
                destroy();
            }
        }

        public void damageMonument() {
            if (monumentCurHealth > 0) {
                monumentCurHealth -= damagePerSecond;
                monumentBar.setProgress(monumentCurHealth / monumentMaxHealth);
            }
        }

        private void destroy() {
            gamePane.getChildren().remove(this);
            gamePane.getChildren().remove(healthBar);
            movingEnemies.remove(this);
            reachedEnemies.remove(this);

            kills += 1;
            killsLabel.setText(kills + "");

            money += 5;
            moneyLabel.setText(money + "");
        }

        @Override
        public String toString() {
            return String.format("Location: %s, Heading: %d, Speed: %f,"
                            + "Health: %f, DPS: %f, ID: %d, Range: %f",
                    location, heading, speed, maxHealth, damagePerSecond, type, range);
        }

        public void damageTower(Tower tower) {
            tower.decreaseHealth(damagePerSecond);
        }
    }
}
