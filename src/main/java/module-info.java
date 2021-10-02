module com.example.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.example.towerdefense;
    opens com.example.towerdefense to javafx.fxml;
}