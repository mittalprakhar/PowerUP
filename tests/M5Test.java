import com.example.towerdefense.Main;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M5Test extends ApplicationTest {

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

        // on game screen now, time to test!
        clickOn("#gameButton");
    }

    @Test
    public void t01DPSByTowerType() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        String param = "DPS: ";
        String info1 = lookup("#playerTower1").query().toString();
        String info2 = lookup("#playerTower2").query().toString();

        assertNotEquals(info1.substring(info1.indexOf(param) + param.length()),
                info2.substring(info2.indexOf(param) + param.length()));
    }

    @Test
    public void t02TowerDamagesEnemy() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        clickOn("#gameTower3");
        clickOn("#tileGround3");

        sleep(10000);
        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#enemy1").query());
    }

    @Test
    public void t03KillsUpdates() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        clickOn("#gameTower3");
        clickOn("#tileGround3");

        Label killsLabel = lookup("#killsLabel").query();
        assertEquals(killsLabel.getText(), "0");
        sleep(15000);
        assertNotEquals(killsLabel.getText(), "0");
    }

    @Test
    public void t04MoneyUpdatesByEnemy() {
        clickOn("#gameTower5");
        clickOn("#tileNearMonument");

        clickOn("#gameTower4");
        clickOn("#tileGround5");

        clickOn("#gameTower3");
        clickOn("#tileGround3");

        String first = lookup("#moneyLabel").query().toString();
        sleep(15000);
        assertNotEquals(first, lookup("#moneyLabel").query().toString());
    }

    @Test
    public void t05MoneyUpdatesByTime() {
        assertEquals("500", ((Label) lookup("#moneyLabel").query()).getText());
        sleep(12000);
        assertEquals("510", ((Label) lookup("#moneyLabel").query()).getText());
    }

    @Test
    public void t06SpawnByEnemyType() {
        Set<String> seen = new HashSet<>();
        for (int i = 1; seen.size() < 2; ++i) {
            sleep(4000);
            String info = lookup("#enemy" + i).query().toString();
            String param = "ID: ";
            seen.add(info.substring(info.indexOf(param) + param.length()));
        }
    }

    @Test
    public void t07DPSByEnemyType() {
        Set<String> seen = new HashSet<>();
        for (int i = 1; seen.size() < 2; ++i) {
            sleep(4000);
            String info = lookup("#enemy" + i).query().toString();
            String param = "DPS: ";
            seen.add(info.substring(info.indexOf(param) + param.length()));
        }
    }

    @Test
    public void t08HealthByEnemyType() {
        Set<String> seen = new HashSet<>();
        for (int i = 1; seen.size() < 2; ++i) {
            sleep(4000);
            String info = lookup("#enemy" + i).query().toString();
            String param = "Health: ";
            seen.add(info.substring(info.indexOf(param) + param.length()));
        }
    }

    @Test
    public void t09RangeByEnemyType() {
        Set<String> seen = new HashSet<>();
        for (int i = 1; seen.size() < 2; ++i) {
            sleep(4000);
            String info = lookup("#enemy" + i).query().toString();
            String param = "Range: ";
            seen.add(info.substring(info.indexOf(param) + param.length()));
        }
    }

    @Test
    public void t10EnemyDamagesTower() {
        clickOn("#gameTower1");
        sleep(13000);
        clickOn("#tileNearMonument");
        sleep(13000);

        assertThrows(EmptyNodeQueryException.class, () ->
                lookup("#playerTower1").query());
    }
}
