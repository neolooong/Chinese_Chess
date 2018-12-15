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
    private Socket roomSocket;                  // 房間連線
    private Parent loginRoot;                   // 登入版面
    private Parent lobbyRoot;                   // 大廳版面
    private ClientLogin clientLogin;            // 登入的 Controller
    private ClientLobby clientLobby;            // 大廳的 Controller
    public ArrayList<ChessBoard> chessBoards = new ArrayList<>(); // 目前所在房間

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
    }

    public void lobbyConnect(String server, int port) throws IOException {
        this.server = server;
        lobbySocket = new Socket(server, port);
        lobbyRespond();
    }

    public void roomConnect(String server) throws IOException {
        roomSocket = new Socket(server, 12345);
        roomRespond();
        roomRequest(null, Behavior.Register);
    }

    public void lobbyRespond(){
        new Thread(() -> {
            try{
                keepListen:while (true) {
                    ObjectInputStream inputStream = new ObjectInputStream(lobbySocket.getInputStream());
                    Data data = (Data) inputStream.readObject();
                    switch (data.type) {
                        case ConnectStatus:
                            if (data.serverRespond.equals("OK")) {
                                roomConnect(server);
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
                                    alert.setContentText(data.serverRespond);
                                    alert.showAndWait();
                                });
                                lobbySocket.close();
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
                System.err.println(e);
                e.printStackTrace();
            }
        }).start();
    }

    public void lobbyRequest(Data.Type type, String playername, String roomname) throws IOException {
        System.out.println("send a " + type + " data to server");
        ObjectOutputStream outputStream = new ObjectOutputStream(lobbySocket.getOutputStream());
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

    public void roomRespond() {
        new Thread(() -> {
            try {
                while (true) {
                    ObjectInputStream inputStream = new ObjectInputStream(roomSocket.getInputStream());
                    GameData data = (GameData) inputStream.readObject();
                    ChessBoard board = getChessBoard(data.roomName);
                    System.out.println("Get " + data.behavior + " data");
                    switch (data.behavior) {
                        case ServerMessage:
                            board.serverMessage.setText(data.message);
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
                            Platform.runLater(() -> {
                                board.move(data.from, data.to);
                            });
                            break;
                        case RequestUnMove:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setHeaderText("");
                                alert.setContentText("Your opponent request to unMove..");
                                Optional<ButtonType> optional = alert.showAndWait();
                                if (optional.isPresent() && optional.get().equals(ButtonType.OK)) {
//                                    todo send permit
                                    roomRequest(board.roomname, GameData.Behavior.PermitUnMove, null, null);
                                    board.unMove();
                                } else {
//                                    todo send reject
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
                e.printStackTrace();
            }
        }).start();
    }

    public void roomRequest(String roomName, Behavior behavior, int from[], int to[]) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(roomSocket.getOutputStream());
            GameData data = new GameData(roomName, name, behavior);
            data.from = from;
            data.to = to;
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void roomRequest(String roomName, Behavior behavior) {
        roomRequest(roomName, behavior, null, null);
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

    public Parent getLoginRoot() {
        return loginRoot;
    }

    public Parent getLobbyRoot() {
        return lobbyRoot;
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
}
