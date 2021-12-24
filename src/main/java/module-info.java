module com.powerup {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.powerup;
    opens com.powerup to javafx.fxml;
}