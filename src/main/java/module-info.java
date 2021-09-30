module com.example.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.example.towerdefense.screens;
    opens com.example.towerdefense.screens to javafx.fxml;
    exports com.example.towerdefense.controllers;
    opens com.example.towerdefense.controllers to javafx.fxml;
}