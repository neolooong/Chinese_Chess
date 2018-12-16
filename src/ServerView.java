import Datas.GameData;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerView {
    private ServerSocket lobbyServerSocket;
    private ServerSocket roomServerSocket;
    private int LobbyPort = 16888;
    private int RoomPort = 12345;

    public static ArrayList<Player> players = new ArrayList<>();      // 玩家清單
    public static ArrayList<GameRoom> rooms = new ArrayList<>();      // 房間清單

    public VBox playerList, roomList;

    public void initialize() throws IOException {
        lobbyConnect();
        roomConnect();
    }

    public void lobbyConnect() throws IOException {
        lobbyServerSocket = new ServerSocket(LobbyPort);
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Lobby: Waiting");
                    new Player(lobbyServerSocket.accept(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void roomConnect() throws IOException{
        roomServerSocket = new ServerSocket(RoomPort);
        new Thread(() -> {
            while (true){
                try {
                    System.out.println("Room : Waiting");
                    Socket socket = roomServerSocket.accept();
                    new Thread(() -> {
                        try {
                            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                            GameData data = (GameData) inputStream.readObject();
                            System.out.println("Room : get a " + data.behavior+ " data from " + data.source + ".");
                            Player player = getPlayer(data.source);
                            if (player != null){
                                player.roomSocket = socket;
                                player.roomInput = inputStream;
                                player.roomOutput = new ObjectOutputStream(socket.getOutputStream());
                                player.roomRequest();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void updatePlayerList(){
        playerList.getChildren().clear();
        for (Player player:players){
            Label label = new Label(player.name);
            playerList.getChildren().add(label);
        }
    }

    public void updateRoomList(){
        roomList.getChildren().clear();
        for (GameRoom room:rooms){
            Label label = new Label("Room: " + room.roomName + "    Host: " + room.host.name + "    NumberOfPlayer: " + room.howManyPlayerInTheRoom());
            roomList.getChildren().add(label);
        }
    }

    public static Player getPlayer(String name){
        for (Player player: players){
            if (player.name.equals(name)){
                return player;
            }
        }
        return null;
    }

    public static GameRoom getRoom(String name){
        for (GameRoom room: rooms){
            if (room.roomName.equals(name)){
                return room;
            }
        }
        return null;
    }
}
