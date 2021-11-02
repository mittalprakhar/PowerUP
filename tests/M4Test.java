import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
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

    public void setup(String difficulty) {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn(difficulty);
        clickOn("#startButton");

        // on game screen now, time to test!
    }

    @Test
    public void testRestartButton() {
        setup("Beginner");

        doubleClickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#restartButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Start Game!", isVisible());
    }

    @Test
    public void testExitButton() {
        setup("Beginner");

        doubleClickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#exitButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(myStage.isShowing());
    }

    @Test
    public void testSurrenderButtonVisibleAfterStart() {
        setup("Beginner");

        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Surrender", isVisible());
    }

    @Test
    public void testTowersNotPlacedBeforeStart() {
        setup("Beginner");

        clickOn("#gameTower1");
        WaitForAsyncUtils.waitForFxEvents();
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(),
                "You must start combat before buying towers!");
    }

    @Test
    public void testTimeNotUpdatedBeforeStart() {
        setup("Beginner");

        verifyThat("#timeLabel", hasText("5:00"));
        sleep(5000);
        verifyThat("#timeLabel", hasText("5:00"));
    }

    @Test
    public void testMoneyNotUpdatedBeforeStart() {
        setup("Beginner");

        verifyThat("#moneyLabel", hasText("500"));
        sleep(12000);
        verifyThat("#moneyLabel", hasText("500"));
    }

    @Test
    public void testNavya1() {

    }

    @Test
    public void testNavya2() {

    }

    @Test
    public void testManiya1() {

    }

    @Test
    public void testManiya2() {

    }
}
