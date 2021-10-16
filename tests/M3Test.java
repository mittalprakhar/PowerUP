import com.example.towerdefense.Main;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void testTowerMenuScroll() {
        setup("Beginner");

        verifyThat("#towerMenu", isVisible());
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.scrollTo(8);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Missile", isVisible());
    }

    @Test
    public void testTowerMenuSelection() {
        setup("Beginner");

        verifyThat("#towerMenu", isVisible());
        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(3);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isSelected(3));

        towerMenu.getSelectionModel().select(5);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isSelected(5));

        towerMenu.getSelectionModel().clearSelection();
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isEmpty());
    }

    @Test
    public void testTowerCannotPlaceOnPath() {
        setup("Beginner");
        // TODO Maniya
    }

    @Test
    public void testTowerCanPlaceOnGround() {
        setup("Beginner");
        // TODO Maniya
    }

    @Test
    public void testTowerDies() {
        setup("Beginner");
        // TODO Mehul
    }

    @Test
    public void testTowerCanPlaceOnDeadTower() {
        setup("Beginner");
        // TODO Mehul
    }

    @Test
    public void testTowerMoneyBeginner() {
        setup("Beginner");

        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(0);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isSelected(0));

        String towerParams = towerMenu.getSelectionModel().getSelectedItem().toString();
        assertTrue(towerParams.contains("Cost: 50"));
    }

    @Test
    public void testTowerMoneyModerate() {
        setup("Moderate");

        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(0);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isSelected(0));

        String towerParams = towerMenu.getSelectionModel().getSelectedItem().toString();
        assertTrue(towerParams.contains("Cost: 60"));
    }

    @Test
    public void testTowerMoneyExpert() {
        setup("Expert");

        ListView<Object> towerMenu = lookup("#towerMenu").queryListView();
        towerMenu.getSelectionModel().select(0);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(towerMenu.getSelectionModel().isSelected(0));

        String towerParams = towerMenu.getSelectionModel().getSelectedItem().toString();
        assertTrue(towerParams.contains("Cost: 70"));
    }

    @Test
    public void testTowerCostsMoney() {
        setup("Beginner");
        // TODO Navya
    }

    @Test
    public void testAlertOnInsufficientMoney() {
        setup("Beginner");
        // TODO Navya
    }
}
