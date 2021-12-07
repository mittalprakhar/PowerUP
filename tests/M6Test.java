import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M6Test extends ApplicationTest {

    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

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
    public void testGameOverTime() {
        setup();
        verifyThat("#timeLabel", hasText("4:00"));
        sleep(10000);
        clickOn("Surrender");
        verifyThat("player1, do not lose heart for thou showed great " +
                "courage in slaying 0 enemies! You spent $0 in 10 seconds while playing the game!", isVisible());
    }

    @Test
    public void testGameOverMoney() {
        setup();
        verifyThat("#moneyLabel", hasText("500"));
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(0);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameTower1");
        clickOn("#tileNearMonument");
        clickOn("Surrender");
        verifyThat("player1, do not lose heart for thou showed great " +
                "courage in slaying 0 enemies! You spent $50 in 1 seconds while playing the game!", isVisible());
    }

    @Test
    public void testGameOverScreen() {
        setup();
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Surrender");
        sleep(5000);
        verifyThat("Game Over", isVisible());
    }
    @Test
    public void testGameNotStarted() {
        clickOn("#startButton");
        clickOn("#nameTextField").write("player1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");
        clickOn("#upgrade1");
        DialogPane alert = lookup(".alert").query();
        //assertEquals();
        assertEquals(alert.getContentText(), "You must start combat before upgrading towers!");

    }

    @Test
    public void upgradetowervisibility() {
    setup();
    sleep(2000);
    for (int i = 1; i <= 5; i++) {
        verifyThat("#upgrade" + i, isVisible());
    }
    }
    @Test
    public void upgradetowercostsmoney() {
        setup();
        verifyThat("#moneyLabel",hasText("500"));
        clickOn("#upgrade1");
        verifyThat("#moneyLabel",hasText("450"));
    }
    @Test
    public void testFinalBossSpawns() {
        setup();
        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy6FinalBoss").query());
        sleep(125000);
        verifyThat("#enemy6FinalBoss", isVisible());
    }

    @Test
    public void testFinalBossVictoryScreen() {
        setup();
        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy6FinalBoss").query());
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.scrollTo(8);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#gameTower9");
        clickOn("#tileFinalBoss1");
        sleep(50000);
        clickOn("#tileFinalBoss2");
        sleep(50000);
        clickOn("#tileFinalBoss3");
        sleep(80000);
        verifyThat("Victory", isVisible());
    }
}
