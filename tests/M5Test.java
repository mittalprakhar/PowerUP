import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

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
        clickOn("#gameButton");

        // on game screen now, time to test!
    }
    @Test
    public void testtowerhealthdecreasebyenemies() {
        clickOn("#gameTower4");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(25000);
        clickOn("#tileGroundmonument");
        sleep(10000);
        WaitForAsyncUtils.waitForFxEvents();
        assertThrows(EmptyNodeQueryException.class, () ->
                verifyThat("#playerTower1", isVisible()));

    }
}
