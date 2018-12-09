import Datas.Data;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientManager {
    private String server;
    private Socket socket;
    private Stage stage;
    private Parent loginRoot;
    private Parent lobbyRoot;
    private ClientLogin clientLogin;
    private ClientLobby clientLobby;
    private ChessBoardManager chessBoardManager;
    private String name;

    ClientManager() throws IOException {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
        loginRoot = loader.load();
        loginRoot.getStylesheets().add(ClientMain.class.getResource("Login.css").toExternalForm());
        clientLogin = loader.getController();
        clientLogin.setClientManager(this);

        loader = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
        lobbyRoot = loader.load();
        clientLobby = loader.getController();
        clientLobby.setClientManager(this);

        chessBoardManager = new ChessBoardManager();
        chessBoardManager.setClientManager(this);
    }

    public void connect(String server, int port) throws IOException {
        this.server = server;
        socket = new Socket(server, port);
        openInputStream();
    }

    public void openInputStream(){
        new Thread(() -> {
            try{
                keepListen:while (true) {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    Data data = (Data) inputStream.readObject();
                    switch (data.type) {
                        case ConnectStatus:
                            if (data.connectRespond.equals("OK")) {
                                chessBoardManager.connect(server);
                                Platform.runLater(() -> {
                                    clientLobby.setPlayerName(name);
                                    stage.setScene(new Scene(lobbyRoot));
                                    stage.centerOnScreen();
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("How");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.connectRespond);
                                    alert.showAndWait();
                                });
                                socket.close();
                                break keepListen;
                            }
                            break;
                        case CreateRoomStatus:
                            if (data.createRoomRespond.equals("OK")) {
                                Platform.runLater(() -> {
                                    try {
                                        chessBoardManager.openChessBoard(data.roomName, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Afu");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.createRoomRespond);
                                    alert.showAndWait();
                                });
                            }
                            break;
                        case EnterRoomStatus:
                            if (data.enterRoomRespond.equals("OK")) {
                                Platform.runLater(() -> {
                                    try {
                                        chessBoardManager.openChessBoard(data.roomName, 2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("叛徒");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.enterRoomRespond);
                                    alert.showAndWait();
                                });
                            }
                            break;
                        case RefreshRoomList:
                            Platform.runLater(() -> clientLobby.updateRoomList(data.rooms));
                            break;
                    }
                }
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }).start();
    }

    public void request2server(Data.Type type, String playername, String roomname) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        Data data = new Data(type);
        switch (type){
            case Connect:
                data.playerName = playername;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case CreateRoom:
                data.roomName = roomname;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case EnterRoom:
                data.roomName = roomname;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case QuitRoom:
                data.roomName = roomname;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
        }

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getLoginRoot() {
        return loginRoot;
    }

    public Parent getLobbyRoot() {
        return lobbyRoot;
    }

    public void setName(String name) {
        this.name = name;
        chessBoardManager.setName(name);
    }
}
