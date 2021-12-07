module com.example.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.example.towerdefense;
    opens com.example.towerdefense to javafx.fxml;
}