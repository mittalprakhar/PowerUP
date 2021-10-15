import com.example.towerdefense.Main;
import javafx.stage.Stage;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class M3Test extends ApplicationTest {

    private Stage myStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main main = new Main();
        main.start(primaryStage);
        myStage = primaryStage;
    }

}
