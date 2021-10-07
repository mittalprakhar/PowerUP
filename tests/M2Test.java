import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M2Test extends ApplicationTest {

    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

    @Test
    public void testWelcomeScreenLaunch() {
        // on welcome screen
        verifyThat("Start Game!", isVisible());
    }

    @Test
    public void testWelcomeStartButton() {
        // on welcome screen
        verifyThat("#startButton", isVisible());
    }

    @Test
    public void testWelcomeTitle() {
        // on welcome screen
        assertEquals(myStage.getTitle(), "Tower Defense");
    }

    @Test
    public void testConfigScreenLaunch() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        verifyThat("Tower Defense", isVisible());
    }

    @Test
    public void testConfigDifficultyDisplay() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#difficultyComboBox");
        verifyThat("Beginner", isVisible());
        verifyThat("Moderate", isVisible());
        verifyThat("Expert", isVisible());
    }

    @Test
    public void testConfigStartButton() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        verifyThat("#startButton", isVisible());
    }

    @Test
    public void testConfigNameEmpty() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("");
        clickOn("#startButton");
        verifyThat("OK", isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigNameNull() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#startButton");
        verifyThat("OK", isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigNameWhitespace() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("   ");
        clickOn("#startButton");
        verifyThat("OK", isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigDifficultyNoSelect() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#startButton");
        verifyThat("OK", isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please select a difficulty.");
    }

    @Test
    public void testConfigMapSelect() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        verifyThat("Forest", isVisible());
        clickOn("#prevButton");
        verifyThat("Desert", isVisible());
        clickOn("#prevButton");
        verifyThat("Ocean", isVisible());
        clickOn("#nextButton");
        verifyThat("Desert", isVisible());
        clickOn("#nextButton");
        verifyThat("Forest", isVisible());
    }

    @Test
    public void testGamePlayerName() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen
        verifyThat("#playerLabel", isVisible());
    }

    @Test
    public void testGameParamsBeginner() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen
        verifyThat("#difficultyLabel", hasText("Beginner"));
        verifyThat("#moneyLabel", hasText("500"));
        verifyThat("#killsLabel", hasText("0"));
        assertEquals(1.0, ((ProgressBar) lookup("#monumentHealth").query()).getProgress());
    }

    @Test
    public void testGameParamsModerate() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Moderate");
        clickOn("#startButton");

        // on game screen
        verifyThat("#difficultyLabel", hasText("Moderate"));
        verifyThat("#moneyLabel", hasText("400"));
        verifyThat("#killsLabel", hasText("0"));
        assertEquals(0.9, ((ProgressBar) lookup("#monumentHealth").query()).getProgress());
    }

    @Test
    public void testGameParamsExpert() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Expert");
        clickOn("#startButton");

        // on game screen
        verifyThat("#difficultyLabel", hasText("Expert"));
        verifyThat("#moneyLabel", hasText("300"));
        verifyThat("#killsLabel", hasText("0"));
        assertEquals(0.8, ((ProgressBar) lookup("#monumentHealth").query()).getProgress());
    }

    @Test
    public void testGameMoneyIncrease() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen
        verifyThat("#moneyLabel", isVisible());
        verifyThat("#moneyLabel", hasText("500"));
        sleep(20000);
        verifyThat("#moneyLabel", hasText("520"));
    }

    @Test
    public void testGameTowerDestroy() {
        // on welcome screen
        clickOn("#startButton");

        // on config screen
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen
        verifyThat("#towerHealth1", isVisible());
        verifyThat("#tower1", isVisible());
        sleep(32000);
        assertThrows(EmptyNodeQueryException.class, () ->
                verifyThat("#tower1", isVisible())
        );
        assertThrows(EmptyNodeQueryException.class, () ->
            verifyThat("#towerHealth1", isVisible())
        );
    }
}
