import Datas.Data;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
                    manager.request2server(Data.Type.CreateRoom, null, s);
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
    public void updateRoomList(HashMap<String, String[]> list){
        roomList.getRowConstraints().remove(1, roomList.getRowConstraints().size() - 1);
        roomList.getChildren().removeAll(roomItem);
        Iterator<String> iterator = list.keySet().iterator();
        int rowIndex = 0;
        while (iterator.hasNext()){
            rowIndex++;
            String buffer = iterator.next();
            Label roomName = new Label(buffer);
            Label roomHost = new Label(list.get(buffer)[0]);
            Label roomSeat = new Label(list.get(buffer)[1] == null ? "1/2" : "2/2");
            Button entryBtn = new Button("加入房間");
            entryBtn.setDisable(list.get(buffer)[1] != null);
            entryBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        manager.request2server(Data.Type.EnterRoom, null, roomName.getText());
                    }catch (IOException e){
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

    public void setClientManager(ClientManager manager) {
        this.manager = manager;
    }
}
