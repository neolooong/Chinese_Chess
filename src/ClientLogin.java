import Datas.Data;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ClientLogin {
    private ClientManager manager;
    @FXML
    private TextField userNameTextField ,ipTextField , portTextField;

    public void initialize(){
        userNameTextField.setText("蘇家緯");
        ipTextField.setText("10.91.1.15");
//        ipTextField.setText("10.51.3.203");
        portTextField.setText("16888");
    }

    public void goLobbyButton() throws IOException, ClassNotFoundException, InterruptedException {
//        連線    開啟接收通道
        manager.connect(ipTextField.getText(), 16888);
//        發送請求
        manager.request2server(Data.Type.Connect, userNameTextField.getText(), null);

        manager.setName(userNameTextField.getText());
    }

    public void setClientManager(ClientManager manager) {
        this.manager = manager;
    }
}
