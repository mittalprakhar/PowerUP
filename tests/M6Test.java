import com.example.towerdefense.Main;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.Before;
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
public class M6Test extends ApplicationTest {

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
    }

    @Test
    public void t01GameOverScreen() {
        clickOn("#gameButton");

        clickOn("Surrender");
        sleep(3000);

        verifyThat("Game Over", isVisible());
    }

    @Test
    public void t02GameOverTime() {
        clickOn("#gameButton");

        verifyThat("#timeLabel", hasText("4:00"));
        sleep(10000);

        clickOn("Surrender");
        verifyThat("player1, do not lose heart for thou showed great "
                + "courage in slaying 0 enemies! You spent $0 in 10 seconds while "
                + "playing the game!", isVisible());
    }

    @Test
    public void t03GameOverMoney() {
        clickOn("#gameButton");

        verifyThat("#moneyLabel", hasText("500"));
        clickOn("#gameTower1");
        clickOn("#tileNearMonument");

        clickOn("Surrender");
        sleep(2000);
        verifyThat("#descriptionLabel", hasText("player1, do not lose heart "
                + "for thou showed great courage in slaying 0 enemies! You spent $50 "
                + "in 1 seconds while playing the game!"));
    }

    @Test
    public void t04UpgradeButtonsVisible() {
        clickOn("#gameButton");

        for (int i = 1; i <= 5; i++) {
            verifyThat("#upgradeTower" + i, isVisible());
        }

        clickOn("Surrender");
    }

    @Test
    public void t05UpgradeGameNotStarted() {
        clickOn("#upgradeTower1");

        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(),
                "You must start combat before upgrading towers!");
    }

    @Test
    public void t06UpgradeCostsMoney() {
        clickOn("#gameButton");

        verifyThat("#moneyLabel", hasText("500"));
        clickOn("#upgradeTower1");

        verifyThat("#moneyLabel", hasText("450"));

        clickOn("Surrender");
    }

    @Test
    public void t07UpgradeTowerHealth() {
        clickOn("#gameButton");

        verifyThat("#healthTower1", hasText("200"));
        clickOn("#upgradeTower1");

        verifyThat("#healthTower1", hasText("400"));

        clickOn("Surrender");
    }

    @Test
    public void t08UpgradeTowerDamage() {
        clickOn("#gameButton");

        verifyThat("#damageTower1", hasText("2"));
        clickOn("#upgradeTower1");

        verifyThat("#damageTower1", hasText("4"));

        clickOn("Surrender");
    }

    @Test
    public void t09UpgradeInsufficientMoney() {
        clickOn("#gameButton");

        clickOn("#upgradeTower1");
        clickOn("#upgradeTower2");
        clickOn("#upgradeTower3");
        clickOn("#upgradeTower4");
        clickOn("#upgradeTower5");

        DialogPane alert = lookup(".alert").query();
        assertEquals(alert.getContentText(), "You do not have the money required"
                + " to upgrade this tower!");
    }

    @Test
    public void t10FinalBossSpawns() {
        clickOn("#gameButton");

        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy6FinalBoss").query());
        sleep(125000);

        verifyThat("#enemy6FinalBoss", isVisible());

        clickOn("Surrender");
    }
}
