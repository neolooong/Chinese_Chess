import Datas.Data;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ClientLobby {
    private ClientManager manager;
    @FXML
    private Label playerName;
    @FXML
    private GridPane roomList;

    public void initialize(){ }

    public void createRoom(ActionEvent event) throws IOException {
        TextInputDialog roomname = new TextInputDialog();
        roomname.setHeaderText("");
        roomname.setContentText("房名: ");
        roomname.showAndWait().ifPresent(new Consumer<String>() {
            @Override
            public void accept(String s) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(getClass().getResource("swearWords.txt").getFile()));
                    while (reader.ready()){
                        if (s.contains(reader.readLine())){
                            reader.close();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("警告");
                            alert.setHeaderText("");
                            alert.setContentText("房間名稱出現不雅字詞");
                            alert.showAndWait();
                            return;
                        }
                    }
                    reader.close();
                    manager.lobbyRequest(Data.Type.CreateRoom, null, s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setPlayerName(String name){
        playerName.setText("Your Name: " + name);
    }

    public HashSet<Node> roomItem = new HashSet<>();
    public void updateRoomList(String info){
        roomList.getRowConstraints().remove(1, roomList.getRowConstraints().size() - 1);
        roomList.getChildren().removeAll(roomItem);
        String roomInfo[] = info.split(";");
//        Iterator<String> iterator = info.keySet().iterator();
        int rowIndex = 0;
        for (String string: roomInfo){
            rowIndex++;
            String str[] = string.split(",");
            if (str.length == 3) {
                Label roomName = new Label(str[0]);
                Label roomHost = new Label(str[1]);
                Label roomSeat = new Label(str[2].equals("1") ? "1/2" : "2/2");
                Button entryBtn = new Button("加入房間");
                entryBtn.setDisable(str[2].equals("2"));
                entryBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            manager.lobbyRequest(Data.Type.EnterRoom, null, roomName.getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                roomItem.addAll(Arrays.asList(roomName, roomHost, roomSeat, entryBtn));

                roomList.add(roomName, 0, rowIndex);
                roomList.add(roomHost, 1, rowIndex);
                roomList.add(roomSeat, 2, rowIndex);
                roomList.add(entryBtn, 3, rowIndex);
            }
        }
    }

    public void setClientManager(ClientManager manager) {
        this.manager = manager;
    }
}
