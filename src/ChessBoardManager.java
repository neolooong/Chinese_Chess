import Datas.Data;
import Datas.GameData;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

public class ChessBoardManager {
    private static int port = 12345;
    public Socket socket;
    private ClientManager clientManager;
    private HashSet<ChessBoard> chessBoards = new HashSet<>();
    private String name;

    ChessBoardManager (){}

    public void connect(String server) throws IOException{
        socket = new Socket(server, port);
        openInputStream();
        request2server(null, GameData.Behavior.Register, null, null);
    }

    public void openInputStream(){
        new Thread(() -> {
            try{
                while (true){
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    GameData data = (GameData) inputStream.readObject();
                    System.out.println("Get " + data.behavior + " data");
                    switch (data.behavior){
                        case ServerMessage:
                            Iterator<ChessBoard> iterator = chessBoards.iterator();
                            while (iterator.hasNext()){
                                ChessBoard board = iterator.next();
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        board.serverMessage.setText(data.message);
                                    });
                                    break;
                                }
                            }
                            break;
                        case CheckIn:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        board.player1Name.setText(data.players[0]);
                                        board.player2Name.setText(data.players[1]);
                                    });
                                }
                            }
                            break;
                        case CheckOut:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    board.order = 1;
                                    Platform.runLater(() -> {
                                        board.player1Name.setText(data.players[0]);
                                        board.player2Name.setText(data.players[1]);
                                        board.resetGame();
                                    });
                                }
                            }
                            break;
                        case GameStart:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        board.openNewGame();
                                    });
                                }
                            }
                            break;
                        case Move:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        board.move(data.from, data.to);
                                    });
                                }
                            }
                            break;
                        case GameEnd:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setHeaderText("");
                                        alert.setContentText("Keep it up!! Next game will be better.");
                                        alert.showAndWait();
                                        board.resetGame();
                                    });
                                }
                            }
                            break;
                        case Surrender:
                            for (ChessBoard board:chessBoards){
                                if (board.roomname.equals(data.roomName)){
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setHeaderText("");
                                        alert.setContentText("Your opponent surrendered to you.");
                                        alert.showAndWait();
                                        board.resetGame();
                                    });
                                }
                            }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void request2server(String roomname, GameData.Behavior behavior, int from[], int to[]){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            GameData data = new GameData(roomname, name, behavior);
            switch (behavior){
                case Register:
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;
                case CheckIn:
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;
                case Ready:
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;
                case Move:
                    data.from = from;
                    data.to = to;
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;
                case GameEnd:
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;
                case Surrender:
                    outputStream.writeObject(data);
                    outputStream.flush();
                    break;

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openChessBoard(String roomName, int order) throws IOException {
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
                clientManager.request2server(Data.Type.QuitRoom, null, roomName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();

        request2server(roomName, GameData.Behavior.CheckIn, null, null);
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
