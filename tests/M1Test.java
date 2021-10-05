import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;

public class M1Test extends ApplicationTest {
    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

    @Test
    public void testConfigNameEmpty() {
        // on welcome screen
        clickOn("#startButton");
        // on config screen
        clickOn("#nameTextField").write("");
        // verify before proceeding to game screen
        clickOn("#startButton");
        // verify alert opened
        verifyThat("OK", NodeMatchers.isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigNameNull() {
        // on welcome screen
        clickOn("#startButton");
        // on config screen
        // verify before proceeding to game screen
        clickOn("#startButton");
        // verify alert opened
        verifyThat("OK", NodeMatchers.isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigNameWhitespace() {
        // on welcome screen
        clickOn("#startButton");
        // on config screen
        clickOn("#nameTextField").write("   ");
        // verify before proceeding to game screen
        clickOn("#startButton");
        // verify alert opened
        verifyThat("OK", NodeMatchers.isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please enter a valid name.");
    }

    @Test
    public void testConfigDifficultyNoSelect() {
        // on welcome screen
        clickOn("#startButton");
        // on config screen
        clickOn("#nameTextField").write("player1");
        // verify before proceeding to game screen
        clickOn("#startButton");
        // verify alert opened
        verifyThat("OK", NodeMatchers.isVisible());
        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "Please select a difficulty.");
    }

    @Test
    public void testConfigMapSelect() {
        // on welcome screen
        clickOn("#startButton");
        // on config screen
        verifyThat("Forest", NodeMatchers.isVisible());

        clickOn("#prevButton");
        verifyThat("Desert", NodeMatchers.isVisible());

        clickOn("#prevButton");
        verifyThat("Ocean", NodeMatchers.isVisible());

        clickOn("#nextButton");
        verifyThat("Desert", NodeMatchers.isVisible());

        clickOn("#nextButton");
        verifyThat("Forest", NodeMatchers.isVisible());
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
        verifyThat("#difficultyLabel", LabeledMatchers.hasText("Beginner"));
        verifyThat("#moneyLabel", LabeledMatchers.hasText("500"));
        verifyThat("#killsLabel", LabeledMatchers.hasText("0"));
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
        verifyThat("#difficultyLabel", LabeledMatchers.hasText("Moderate"));
        verifyThat("#moneyLabel", LabeledMatchers.hasText("400"));
        verifyThat("#killsLabel", LabeledMatchers.hasText("0"));
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
        verifyThat("#difficultyLabel", LabeledMatchers.hasText("Expert"));
        verifyThat("#moneyLabel", LabeledMatchers.hasText("300"));
        verifyThat("#killsLabel", LabeledMatchers.hasText("0"));
        assertEquals(0.8, ((ProgressBar) lookup("#monumentHealth").query()).getProgress());
    }
}