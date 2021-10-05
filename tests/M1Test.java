import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

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
}
