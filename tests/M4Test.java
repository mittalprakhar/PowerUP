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
public class M4Test extends ApplicationTest {

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
    }

    @Test
    public void t01TowersNotPlacedBeforeStart() {
        clickOn("#gameTower1");
        WaitForAsyncUtils.waitForFxEvents();
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(),
                "You must start combat before buying towers!");
    }

    @Test
    public void t02TimeNotUpdatedBeforeStart() {
        verifyThat("#timeLabel", hasText("5:00"));
        sleep(1000);
        verifyThat("#timeLabel", hasText("5:00"));
    }

    @Test
    public void t03MoneyNotUpdatedBeforeStart() {
        verifyThat("#moneyLabel", hasText("500"));
        sleep(10000);
        verifyThat("#moneyLabel", hasText("500"));
    }

    @Test
    public void t04SurrenderButtonVisibleAfterStart() {
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Surrender", isVisible());
    }

    @Test
    public void t05RestartButton() {
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#restartButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Start Game!", isVisible());
    }

    @Test
    public void t06ExitButton() {
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#exitButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(myStage.isShowing());
    }

    @Test
    public void t07EnemySpawns() {
        clickOn("#gameButton");
        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy1").query());
        sleep(3500);
        verifyThat("#enemy1", isVisible());
    }

    @Test
    public void t08EnemyMoves() {
        clickOn("#gameButton");
        sleep(3500);
        String initial = lookup("#enemy1").query().toString();
        sleep(100);
        assertNotEquals(initial, lookup("#enemy1").query().toString());
    }

    @Test
    public void t09EnemyReachesMonument() {
        clickOn("#gameButton");
        sleep(8000);
        verifyThat("#enemy1reached", isVisible());
    }

    @Test
    public void t10EnemyDamagesMonument() {
        clickOn("#gameButton");
        ProgressBar monumentBar = lookup("#monumentHealth").query();
        assertEquals(1.0, monumentBar.getProgress());
        sleep(8000);
        verifyThat("#enemy1reached", isVisible());
        assertNotEquals(1.0, monumentBar.getProgress());
    }
}
