import com.example.towerdefense.Main;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;


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
