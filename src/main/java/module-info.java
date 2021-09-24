module com.example.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.towerdefense to javafx.fxml;
    exports com.example.towerdefense;
}