import com.example.towerdefense.Main;
import javafx.scene.control.ButtonType;
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
/*
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


*/
    @Test
    public void teststartbuttontimeandmoney() {
        setup("Beginner");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#moneyLabel", hasText("500"));
        sleep(21000);
        verifyThat("#moneyLabel", hasText("500"));
        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(21000);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#moneyLabel", hasText("520"));
        // verifyThat("#timeLabel", hasText("4:40"));
    }
    @Test
    public void testtowernotplacedwhengamenotstarted() {
        setup("Beginner");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameTower1");
        WaitForAsyncUtils.waitForFxEvents();
        DialogPane alert = lookup(".alert").query();
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(alert.getContentText(),
                "You must start combat before buying towers!");
        clickOn(alert.lookupButton(ButtonType.OK));
       /*
        sleep(10000);

        clickOn("#gameButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameTower1");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#tileGround1");
        verifyThat("#playerTower1", isVisible());

        */

    }



}
