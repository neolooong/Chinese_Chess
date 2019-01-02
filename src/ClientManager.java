import Datas.Data;
import Datas.GameData;
import Datas.GameData.Behavior;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 管理視窗
 * 玩家名稱
 * 大廳連線 、 房間連線
 * */

public class ClientManager {
    private Stage stage;                        // 主視窗
    private String server;                      // 伺服器IP
    public String name;                         // 玩家名稱
    private Socket lobbySocket;                 // 大廳連線
    public ObjectInputStream lobbyInput;
    public ObjectOutputStream lobbyOutput;
    private Socket roomSocket;                  // 房間連線
    public ObjectInputStream roomInput;
    public ObjectOutputStream roomOutput;
    private Parent loginRoot;                   // 登入版面
    private Parent lobbyRoot;                   // 大廳版面
    public Scene loginScene;
    public Scene lobbyScene;
    private ClientLogin clientLogin;            // 登入的 Controller
    private ClientLobby clientLobby;            // 大廳的 Controller
    public ArrayList<ChessBoard> chessBoards = new ArrayList<>(); // 目前所在房間

    public boolean isConnect = false;

    ClientManager() throws IOException {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
        loginRoot = loader.load();
        loginRoot.getStylesheets().add(ClientMain.class.getResource("Login.css").toExternalForm());
        loginScene = new Scene(loginRoot);
        clientLogin = loader.getController();
        clientLogin.setClientManager(this);

        loader = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
        lobbyRoot = loader.load();
        lobbyScene = new Scene(lobbyRoot);
        clientLobby = loader.getController();
        clientLobby.setClientManager(this);
    }

    public void lobbyConnect(String server, int port) throws IOException {
        this.server = server;
        lobbySocket = new Socket(server, port);
        lobbyOutput = new ObjectOutputStream(lobbySocket.getOutputStream());
        lobbyInput = new ObjectInputStream(lobbySocket.getInputStream());
        lobbyRespond();
    }

    public void roomConnect(String server) throws IOException {
        roomSocket = new Socket(server, 12345);
        roomOutput = new ObjectOutputStream(roomSocket.getOutputStream());
        roomRequest(null, Behavior.Register);
        roomInput = new ObjectInputStream(roomSocket.getInputStream());
        roomRespond();
    }

    public void lobbyRespond(){
        new Thread(() -> {
            try{
                keepListen: while (true) {
                    Data data = (Data) lobbyInput.readObject();
                    System.out.println("get a " + data.type + " data.");
                    switch (data.type) {
                        case ConnectStatus:
                            if (data.serverRespond.equals("OK")) {
                                roomConnect(server);
                                isConnect = true;
                                Platform.runLater(() -> {
                                    clientLobby.setPlayerName(name);
                                    stage.setScene(lobbyScene);
                                    stage.centerOnScreen();
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("How");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.serverRespond);
                                    alert.showAndWait();
                                });
                                lobbySocket.close();
                                lobbyInput.close();
                                lobbyOutput.close();
                                break keepListen;
                            }
                            break;
                        case CreateRoomStatus:
                            if (data.serverRespond.equals("OK")) {
                                Platform.runLater(() -> {
                                    try {
                                        openChessBoard(data.roomName, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Afu");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.serverRespond);
                                    alert.showAndWait();
                                });
                            }
                            break;
                        case EnterRoomStatus:
                            if (data.serverRespond.equals("OK")) {
                                Platform.runLater(() -> {
                                    try {
                                        openChessBoard(data.roomName, 2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("叛徒");
                                    alert.setHeaderText("");
                                    alert.setContentText(data.serverRespond);
                                    alert.showAndWait();
                                });
                            }
                            break;
                        case RefreshRoomList:
                            Platform.runLater(() -> clientLobby.updateRoomList(data.serverRespond));
                            break;
                    }
                }
            }catch (IOException | ClassNotFoundException e){
                disConnect();
                System.err.println("Catch: " + e);
            }
        }).start();
    }

    public void lobbyRequest(Data.Type type, String playername, String roomname) throws IOException {
        System.out.println("send a " + type + " data to server");
        Data data = new Data(type);
        switch (type){
            case Connect:
                data.playerName = playername;
                lobbyOutput.writeObject(data);
                lobbyOutput.flush();
                break;
            case CreateRoom:
                data.roomName = roomname;
                lobbyOutput.writeObject(data);
                lobbyOutput.flush();
                break;
            case EnterRoom:
                data.roomName = roomname;
                lobbyOutput.writeObject(data);
                lobbyOutput.flush();
                break;
            case QuitRoom:
                data.roomName = roomname;
                lobbyOutput.writeObject(data);
                lobbyOutput.flush();
                break;
        }
    }

    public void roomRespond() {
        new Thread(() -> {
            try {
                while (true) {
                    GameData data = (GameData) roomInput.readObject();
                    ChessBoard board = getChessBoard(data.roomName);
                    System.out.println("Get " + data.behavior + " data");
                    if (data.message != null)
                        board.chatArea.appendText(data.source + ":" + data.message + "\n");
                    switch (data.behavior) {
                        case Message:
                            if (data.source.equals(name))
                                board.msgInputField.setText("");
                            break;
                        case CheckIn:
                            Platform.runLater(() -> {
                                board.player1Name.setText(data.players[0]);
                                board.player2Name.setText(data.players[1]);
                            });
                            break;
                        case CheckOut:
                            board.order = 1;
                            Platform.runLater(() -> {
                                board.resetGame();
                                board.player1Name.setText(data.players[0]);
                                board.player2Name.setText(data.players[1]);
                            });
                            break;
                        case GameStart:
                            Platform.runLater(() -> {
                                board.openNewGame();
                            });
                            break;
                        case Move:
                            if (data.source.equals(name)){
                                Platform.runLater(() -> {
                                    board.move(data.from, data.to, false);
                                });
                            }else {
                                Platform.runLater(() -> {
                                    board.move(data.from, data.to, true);
                                    board.chatArea.appendText("--- Your Turn ---\n");
                                });
                            }
                            break;
                        case RequestUnMove:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setHeaderText("");
                                alert.setContentText("Your opponent request to unMove..");
                                Optional<ButtonType> optional = alert.showAndWait();
                                if (optional.isPresent() && optional.get().equals(ButtonType.OK)) {
                                    roomRequest(board.roomname, GameData.Behavior.PermitUnMove, null, null);
                                    board.unMove();
                                } else {
                                    roomRequest(board.roomname, GameData.Behavior.RejectUnMove, null, null);
                                }
                            });
                            break;
                        case PermitUnMove:
                            Platform.runLater(() -> {
                                board.unMove();
                            });
                            break;
                        case RejectUnMove:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("");
                                alert.setContentText("Your opponent reject to you..");
                                alert.showAndWait();
                            });
                            break;
                        case GameEnd:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("");
                                alert.setContentText("Keep it up!! Next game will be better.");
                                alert.showAndWait();
                                board.resetGame();
                            });
                            break;
                        case Surrender:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("");
                                alert.setContentText("Your opponent surrendered to you.");
                                alert.showAndWait();
                                board.resetGame();
                            });
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                disConnect();
                System.err.println("Catch: " + e);
            }
        }).start();
    }

    public void roomRequest(String roomName, Behavior behavior, String message, int from[], int to[]){
        try {
            GameData data = new GameData(roomName, name, behavior);
            data.message = message;
            data.from = from;
            data.to = to;
            roomOutput.writeObject(data);
            roomOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void roomRequest(String roomName, Behavior behavior, int from[], int to[]) {
        roomRequest(roomName, behavior, null, from, to);
    }

    public void roomRequest(String roomName, Behavior behavior, String message){
        roomRequest(roomName, behavior, message, null, null);
    }

    public void roomRequest(String roomName, Behavior behavior) {
        roomRequest(roomName, behavior, null, null, null);
    }

    public void cancelConnect(){
        try {
            if (lobbySocket != null)
                lobbySocket.close();
            if (lobbyInput != null)
                lobbyInput.close();
            if (lobbyOutput != null)
                lobbyOutput.close();
            if (roomSocket != null)
                roomSocket.close();
            if (roomInput != null)
                roomInput.close();
            if (roomOutput != null)
                roomOutput.close();
        } catch (IOException e) {
            System.err.println("Quit Game: Catch: " + e);
        }
    }

    public void openChessBoard(String roomName, int order) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChessBoard.fxml"));
        Parent root = loader.load();

        ChessBoard chessBoard = loader.getController();
        chessBoard.setManager(this);
        chessBoard.setRoomname(roomName);
        chessBoard.setOrder(order);
        chessBoards.add(chessBoard);

        Stage stage = new Stage();
        chessBoard.setStage(stage);

        stage.setTitle("象棋靈王八蛋營養大象棋");
        stage.getIcons().add(new Image("img/icon.png"));
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> {
            try {
                lobbyRequest(Data.Type.QuitRoom, null, roomName);
                chessBoards.remove(chessBoard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
        roomRequest(roomName, GameData.Behavior.CheckIn);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChessBoard getChessBoard(String roomName){
        for (ChessBoard board: chessBoards){
            if (board.roomname.equals(roomName)){
                return board;
            }
        }
        return null;
    }

    public synchronized void disConnect(){
        if (isConnect) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setContentText("Can not connect to Server...");
                alert.showAndWait();
                stage.setScene(loginScene);
            });
            cancelConnect();
            for (ChessBoard chessBoard : chessBoards) {
                Platform.runLater(() -> {
                    chessBoard.stage.close();
                });
            }
            isConnect = false;
        }
    }
}
