import Datas.Data;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LobbyManager {
    private ServerSocket serverSocket;

    private ServerView serverView;
    private GameManager gameManager;

    private HashSet<Player> players = new HashSet<>();

    private Set<Socket> sockets = new HashSet<>();
    private HashMap<String, Socket> playerMap = new HashMap<>();

    LobbyManager(){

    }

    public void connect(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(() -> {
            while (playerMap.size() < 100) {
                try {
                    Socket socket = serverSocket.accept();
                    playerAdd(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void playerAdd(Socket socket) {
        sockets.add(socket);
        new Thread(() -> {
            String playername = "";
            try {
                ObjectInputStream inputStream;
                keepListen:while (true){
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    Data data = (Data) inputStream.readObject();
                    switch (data.type){
                        case Connect:
                            playername = data.playerName;
                            if (!playerMap.containsKey(playername)) {
                                playerMap.put(playername, socket);
                                respond2player(socket, Data.Type.ConnectStatus, "OK", null);
                                respond2player(socket, Data.Type.RefreshRoomList, null, null);
                                Platform.runLater(() -> serverView.updatePlayerList());
                            } else {
                                respond2player(socket, Data.Type.ConnectStatus, "名稱重複了", null);
                                socket.close();
                                break keepListen;
                            }
                            break;
                        case CreateRoom:
                            String roomName = data.roomName;
                            if (gameManager.createRoom(roomName, playername)) {
//                                回應
                                respond2player(socket, Data.Type.CreateRoomStatus, "OK", roomName);
//                                所有玩家更新清單
                                for (Socket s:sockets){
                                    respond2player(s, Data.Type.RefreshRoomList, null, null);
                                }
//                                Server 視窗刷新
                                Platform.runLater(() -> serverView.updateRoomList());
                            }else {
                                respond2player(socket, Data.Type.CreateRoomStatus, "房間名稱重複了", null);
                            }
                            break;
                        case EnterRoom:
                            if (gameManager.enterRoom(data.roomName, playername)) {
                                respond2player(socket, Data.Type.EnterRoomStatus, "OK", data.roomName);
//                                所有玩家更新清單
                                for (Socket s:sockets){
                                    respond2player(s, Data.Type.RefreshRoomList, null, null);
                                }
//                                Server 視窗刷新
                                Platform.runLater(() -> serverView.updateRoomList());
                            }else {
                                respond2player(socket, Data.Type.EnterRoomStatus, "房間已滿", null);
                            }
                            break;
                        case QuitRoom:
                            gameManager.quitRoom(data.roomName, playername);
//                            所有玩家更新清單
                            for (Socket s:sockets){
                                respond2player(s, Data.Type.RefreshRoomList, null, null);
                            }
//                            Server 視窗刷新
                            Platform.runLater(() -> serverView.updateRoomList());
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
                sockets.remove(socket);
                playerMap.remove(playername);
                Platform.runLater(() -> serverView.updatePlayerList());
                Platform.runLater(() -> serverView.updateRoomList());
            }

        }).start();
    }

    private void respond2player(Socket socket, Data.Type type, String message, String roomname) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        Data data = new Data(type);
        switch (type){
            case ConnectStatus:
                data.connectRespond = message;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case CreateRoomStatus:
                data.createRoomRespond = message;
                data.roomName = roomname;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case EnterRoomStatus:
                data.enterRoomRespond = message;
                data.roomName = roomname;
                outputStream.writeObject(data);
                outputStream.flush();
                break;
            case RefreshRoomList:
                data.rooms = gameManager.getRoomMap();
                outputStream.writeObject(data);
                outputStream.flush();
                break;
        }
    }

    public void setGameManager(GameManager gameManager){
        this.gameManager = gameManager;
    }

    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }

    public HashMap<String, Socket> getPlayerMap() {
        return playerMap;
    }

    public void setPlayers(HashSet<Player> players) {
        this.players = players;
    }
}
