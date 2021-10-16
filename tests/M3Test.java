import com.example.towerdefense.Main;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M3Test extends ApplicationTest {

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
        clickOn("#nameTextField").write("p1");
        clickOn("#difficultyComboBox");
        clickOn("Beginner");
        clickOn("#startButton");

        // on game screen now, time to test!
    }

    @Test
    public void testTowerMenuScroll() {
        verifyThat("#towerMenu", isVisible());
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.scrollTo(8);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Missile", isVisible());
    }

    @Test
    public void testTowerMenuSelection() {
        verifyThat("#towerMenu", isVisible());
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(3);
        WaitForAsyncUtils.waitForFxEvents();
        assert towerMenu.getSelectionModel().isSelected(3);

        towerMenu.getSelectionModel().select(5);
        WaitForAsyncUtils.waitForFxEvents();
        assert towerMenu.getSelectionModel().isSelected(5);

        towerMenu.getSelectionModel().clearSelection();
        WaitForAsyncUtils.waitForFxEvents();
        assert towerMenu.getSelectionModel().isEmpty();
    }
}
