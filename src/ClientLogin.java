import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientLogin {
    private Stage stage;
    @FXML
    private TextField userNameTextField ,ipTextField , portTextField;

    public void initialize(){
        userNameTextField.setText("蘇家緯");
        ipTextField.setText("10.91.1.15");
//        ipTextField.setText("192.168.1.11");
    }

    public void goLobbyButton() throws IOException {

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
