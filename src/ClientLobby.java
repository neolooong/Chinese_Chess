import Data.Data;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        Data data = new Data(Data.Type.CreateRoom);
        TextInputDialog roomname = new TextInputDialog();
        roomname.setHeaderText("");
        roomname.setContentText("房名: ");
        roomname.showAndWait().ifPresent(new Consumer<String>() {
            @Override
            public void accept(String s) {
                data.roomName = s;
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(ClientMain.socket.getOutputStream());
                    outputStream.writeObject(data);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openInputStream(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ObjectInputStream inputStream = new ObjectInputStream(ClientMain.socket.getInputStream());
                        Data data;
                        data = (Data) inputStream.readObject();
                        switch (data.type) {
                            case CreateRoomStatus:
                                if (data.createRoomRespond.equals("OK")) {
                                    String roomname = data.roomName;
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ChessBoard.fxml"));
                                    Parent root = loader.load();
                                    ChessBoard chessBoard = loader.getController();
                                    chessBoard.setRoomName(roomname);
                                    Platform.runLater(() -> {
                                        Stage stage = new Stage();
                                        stage.setTitle("象棋靈王八蛋營養大象棋");
                                        stage.getIcons().add(new Image("img/icon.png"));
                                        stage.setScene(new Scene(root));
                                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                            @Override
                                            public void handle(WindowEvent event) {
                                                try {
                                                    ObjectOutputStream outputStream = new ObjectOutputStream(ClientMain.socket.getOutputStream());
                                                    Data data = new Data(Data.Type.QuitRoom);
                                                    data.roomName = roomname;
                                                    outputStream.writeObject(data);
                                                    outputStream.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        stage.show();
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setHeaderText("");
                                        alert.setContentText(data.connectRespond);
                                        alert.showAndWait();
                                    });
                                }
                                break;
                            case EnterRoomStatus:
                                if (data.enterRoomRespond.equals("OK")){
                                    String roomname = data.roomName;
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ChessBoard.fxml"));
                                    Parent root = loader.load();
                                    ChessBoard chessBoard = loader.getController();
                                    chessBoard.setRoomName(roomname);
                                    Platform.runLater(() -> {
                                        Stage stage = new Stage();
                                        stage.setTitle("象棋靈王八蛋營養大象棋");
                                        stage.getIcons().add(new Image("img/icon.png"));
                                        stage.setScene(new Scene(root));
                                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                            @Override
                                            public void handle(WindowEvent event) {
                                                try {
                                                    ObjectOutputStream outputStream = new ObjectOutputStream(ClientMain.socket.getOutputStream());
                                                    Data data = new Data(Data.Type.QuitRoom);
                                                    data.roomName = roomname;
                                                    outputStream.writeObject(data);
                                                    outputStream.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        stage.show();
                                    });
                                }else {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setHeaderText("");
                                        alert.setContentText(data.enterRoomRespond);
                                        alert.showAndWait();
                                    });
                                }
                                break;
                            case RefreshRoomList:
                                Platform.runLater(() -> updateRoomList(data.rooms));
                                break;
                        }
                    }
                }catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }).start();
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
                        ObjectOutputStream outputStream = new ObjectOutputStream(ClientMain.socket.getOutputStream());
                        Data data = new Data(Data.Type.EnterRoom);
                        data.roomName = roomName.getText();
                        outputStream.writeObject(data);
                        outputStream.flush();
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
