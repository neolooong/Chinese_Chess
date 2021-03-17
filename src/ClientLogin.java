import Datas.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientLogin {
    private ClientManager manager;
    @FXML
    private TextField userNameTextField ,ipTextField , portTextField;

    public void initialize(){
        userNameTextField.setText("Player");
        ipTextField.setText("10.91.5.55");
        portTextField.setText("16888");
    }

    public void goLobbyButton() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getClass().getResource("swearWords.txt").getFile()));
        while (reader.ready()){
            if (userNameTextField.getText().contains(reader.readLine())){
                reader.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("警告");
                alert.setHeaderText("");
                alert.setContentText("名稱出現不雅字詞");
                alert.showAndWait();
                return;
            }
        }
        reader.close();
//        連線    開啟接收通道
        manager.lobbyConnect(ipTextField.getText(), 16888);
//        發送請求
        manager.lobbyRequest(Data.Type.Connect, userNameTextField.getText(), null);

        manager.setName(userNameTextField.getText());

        manager.getStage().setOnCloseRequest(event -> {
            manager.cancelConnect();
            System.exit(0);
        });
    }

    public void setClientManager(ClientManager manager) {
        this.manager = manager;
    }
}
