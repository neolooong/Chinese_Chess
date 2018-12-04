import Data.Data;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientLogin {
    private Stage stage;
    @FXML
    private TextField userNameTextField ,ipTextField , portTextField;

    public void initialize(){
        userNameTextField.setText("蘇家緯");
        ipTextField.setText("10.91.1.15");
//        ipTextField.setText("192.168.1.11");
        portTextField.setText("16888");
    }

    public void goLobbyButton() throws IOException, ClassNotFoundException {
        ClientMain.socket = new Socket(ipTextField.getText(), 16888);
        ObjectOutputStream outputStream = new ObjectOutputStream(ClientMain.socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(ClientMain.socket.getInputStream());
        Data data;
        data = new Data(Data.Type.Connect);
        data.playerName = userNameTextField.getText();
        outputStream.writeObject(data);
        outputStream.flush();
        data = (Data) inputStream.readObject();
        if (data.connectRespond.equals("OK")){

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
            Parent root = loader.load();

            ClientLobby clientLobby = loader.getController();
            clientLobby.setPlayerName(userNameTextField.getText());
            clientLobby.updateRoomList(data.rooms);
            clientLobby.openInputStream();

            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        }else {
            System.out.println("Hi");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("");
            alert.setContentText(data.connectRespond);
            alert.showAndWait();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
