import com.example.towerdefense.Main;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class testM1 extends ApplicationTest {
    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

    @Test
    public void testWindowTitle() {
        verifyThat("Welcome To Tower Defense v1", NodeMatchers.isVisible());
    }
}
