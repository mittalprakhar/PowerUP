import com.example.towerdefense.Main;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M5Test extends ApplicationTest {

    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

    @Before
    public void setup() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen now, time to test!
        clickOn("#gameButton");
    }

    @Test
    public void testTowerDamagesEnemy() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        clickOn("#gameTower3");
        clickOn("#tileGround3");

        sleep(10000);
        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy1").query());
    }

    @Test
    public void testKillsLabelUpdates() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        clickOn("#gameTower3");
        clickOn("#tileGround3");

        Label killsLabel = lookup("#killsLabel").query();
        assertEquals(killsLabel.getText(), "0");
        sleep(10000);
        assertNotEquals(killsLabel.getText(), "0");
    }
}
