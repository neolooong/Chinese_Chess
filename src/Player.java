import Datas.Data;
import Datas.GameData;
import Datas.GameData.Behavior;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static Datas.Data.*;

public class Player {
    public String name;
    public Socket lobbySocket;
    public ObjectInputStream lobbyInput;
    public ObjectOutputStream lobbyOutput;
    public Socket roomSocket;
    public ObjectInputStream roomInput;
    public ObjectOutputStream roomOutput;

    public ArrayList<GameRoom> myRooms = new ArrayList<>();

    public boolean isClose = false;

    public ServerView serverView;

    public Player(Socket lobbySocket, ServerView serverView) throws IOException {
        this.lobbySocket = lobbySocket;
        this.lobbyInput = new ObjectInputStream(lobbySocket.getInputStream());
        this.lobbyOutput = new ObjectOutputStream(lobbySocket.getOutputStream());
        this.serverView = serverView;
        new Thread(() -> {
            lobbyRequest();
        }).start();
    }

    public void lobbyRequest() {
        try{
            while (true){
                Data data = (Data) lobbyInput.readObject();
                System.out.println("Lobby: get a " + data.type + " data from " + name + ".");
                GameRoom room;
                switch (data.type){
                    case Connect:   // Get connect data
                        if (ServerView.getPlayer(data.playerName) == null){     // add to 'Players'
                            ServerView.players.add(this);
                            name = data.playerName;
                            lobbyRespond(Type.ConnectStatus, "OK");
                            lobbyRespond(Type.RefreshRoomList, null);
                            Platform.runLater(() -> {
                                serverView.updatePlayerList();
                            });
                        }else {     // The name had been used
                            cancelConnect();
                            lobbyRespond(Type.ConnectStatus, "The name had been used.");
                        }
                        break;
                    case CreateRoom:
                        if (ServerView.getRoom(data.roomName) == null){       // Create GameRoom
                            room = new GameRoom(data.roomName, this);
                            ServerView.rooms.add(room);
                            myRooms.add(room);
                            lobbyRespond(Type.CreateRoomStatus, "OK", data.roomName);
                            Platform.runLater(() -> {
                                serverView.updateRoomList();
                            });
                            for (Player player: ServerView.players){
                                player.lobbyRespond(Type.RefreshRoomList, null);
                            }
                        }else {     // The room had existed
                            lobbyRespond(Type.CreateRoomStatus, "The room had existed.");
                        }
                        break;
                    case EnterRoom:
                        room = ServerView.getRoom(data.roomName);
                        if (room != null){
                            int enterResult = room.enterRoom(this);
                            if (enterResult == 1){
                                myRooms.add(room);
                                lobbyRespond(Type.EnterRoomStatus, "OK", data.roomName);
                                Platform.runLater(() -> {
                                    serverView.updateRoomList();
                                });
                                for (Player player: ServerView.players){
                                    player.lobbyRespond(Type.RefreshRoomList, null);
                                }
                            }else if (enterResult == 2){
                                lobbyRespond(Type.CreateRoomStatus, "Do not play with self.");
                            }else if (enterResult == 3){
                                lobbyRespond(Type.CreateRoomStatus, "The Room is full.");
                            }
                        } else {
                            lobbyRespond(Type.CreateRoomStatus, "The Room is not exist.");
                        }
                        break;
                    case QuitRoom:
                        room = ServerView.getRoom(data.roomName);
                        if (room != null){
                            room.quitRoom(this);
                            myRooms.remove(room);
                            Platform.runLater(() -> {
                                serverView.updateRoomList();
                            });
                            for (Player player: ServerView.players){
                                player.lobbyRespond(Type.RefreshRoomList, null);
                            }
                        }
                        break;
                }
            }
        }catch (IOException e){
            System.err.println("Catch: " + e);
            quitGame();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void lobbyRespond(Type type, String serverRespond, String roomName){
        try {
            Data data = new Data(type, serverRespond, roomName);
            if (type == Type.RefreshRoomList){
                String buffer = "";
                for (GameRoom room: ServerView.rooms){
                    buffer = buffer + room.toString();
                }
                data.serverRespond = buffer;
            }
            lobbyOutput.writeObject(data);
            lobbyOutput.flush();
            System.out.println("Lobby: send a " + type + " data to " + name + ".");
        } catch (IOException e) {
            System.err.println("Catch: " + e);
            quitGame();
        }
    }

    private void lobbyRespond(Type type, String message) {
        lobbyRespond(type, message, null);
    }

    public void roomRequest(){
        try {
            while (true){
                GameData data = (GameData) roomInput.readObject();
                System.out.println("Room : get a " + data.behavior + " data from " + data.source + ".");
                Player opponent = ServerView.getRoom(data.roomName).getOpponent(this);
                switch (data.behavior){
                    case CheckIn:
                        roomRespond(data.roomName, "Server", data.behavior);
                        if (opponent != null){
                            opponent.roomRespond(data.roomName, "Server", data.behavior);
                        }
                        break;
                    case Ready:
                        ServerView.getRoom(data.roomName).ready(this);
                        break;
                    case Move: case RequestUnMove: case PermitUnMove: case RejectUnMove:
                        opponent.roomRespond(data);
                        break;
                    case GameEnd: case Surrender:
                        opponent.roomRespond(data);
                        ServerView.getRoom(data.roomName).resetRoom();
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Catch: " + e);
            quitGame();
        }
    }

    private void roomRespond(GameData data) {
        try {
            GameRoom room = ServerView.getRoom(data.roomName);
            if (room != null){
                roomOutput.writeObject(data);
                roomOutput.flush();
                System.out.println("Room : send a " + data.behavior + " data to " + name);
            }
        } catch (IOException e) {
            System.err.println("Catch: " + e);
            quitGame();
        }
    }

    public void roomRespond(String roomName, String source, Behavior behavior, String message){
        GameData data = new GameData(roomName, source, behavior);
        if (message != null && !message.equals("")){
            data.message = message;
        }
        if (behavior == Behavior.CheckIn || behavior == Behavior.CheckOut ||behavior == Behavior.GameStart){
            GameRoom room = ServerView.getRoom(roomName);
            data.players = new String[]{room.host.name, room.guest == null? null: room.guest.name};
        }
        roomRespond(data);
    }

    public void roomRespond(String roomName, String source, Behavior behavior){
        roomRespond(roomName, source, behavior, null);
    }

    public void cancelConnect() {
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
            System.err.println("Catch: " + e);
        }
    }

    public synchronized void quitGame(){
        if (!isClose){
            isClose = true;
            cancelConnect();
            ServerView.players.remove(this);    // 將自己從玩家名單中剔除
            for (GameRoom room: myRooms){       // 離開所有房間
                room.quitRoom(this);
            }
            Platform.runLater(() -> {           // 更新視窗
                serverView.updateRoomList();
                serverView.updatePlayerList();
            });
            for (Player player: ServerView.players){    // 更新所有玩家房間資料
                player.lobbyRespond(Type.RefreshRoomList, null);
            }
        }
    }
}
