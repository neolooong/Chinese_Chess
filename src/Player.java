import Datas.Data;
import Datas.GameData;
import Datas.GameData.Behavior;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static Datas.Data.*;

public class Player {
    public String name;
    public Socket lobbySocket;
    public Socket roomSocket;

    public ServerView serverView;

    public Player(Socket lobbySocket, ServerView serverView) {
        this.lobbySocket = lobbySocket;
        this.serverView = serverView;
        new Thread(() -> {
            lobbyRequest();
        }).start();
    }

    public void lobbyRequest() {
        try{
            while (true){
                ObjectInputStream inputStream = new ObjectInputStream(lobbySocket.getInputStream());
                Data data = (Data) inputStream.readObject();
                System.out.println("lobby: get a " + data.type + " data.");
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
                        System.out.println("Done");
                        break;
                    case CreateRoom:
                        if (ServerView.getRoom(data.roomName) == null){       // Create GameRoom
                            room = new GameRoom(data.roomName, this);
                            ServerView.rooms.add(room);
                            lobbyRespond(Type.CreateRoomStatus, "OK", data.roomName);
                            for (Player player: ServerView.players){
                                player.lobbyRespond(Type.RefreshRoomList, null);
                            }
                            Platform.runLater(() -> {
                                serverView.updateRoomList();
                            });
                        }else {     // The room had existed
                            lobbyRespond(Type.CreateRoomStatus, "The room had existed.");
                        }
                        break;
                    case EnterRoom:
                        room = ServerView.getRoom(data.roomName);
                        if (room != null){
                            int enterResult = room.enterRoom(this);
                            if (enterResult == 1){
                                lobbyRespond(Type.EnterRoomStatus, "OK", data.roomName);
                                for (Player player: ServerView.players){
                                    player.lobbyRespond(Type.RefreshRoomList, null);
                                }
                                Platform.runLater(() -> {
                                    serverView.updateRoomList();
                                });
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
                            int quitResult = room.quitRoom(this);
                            if (quitResult == 0){
                                ServerView.rooms.remove(room);
                            }else if (quitResult == -1){
                                System.out.println("!!!!!!!");
                            }
                            for (Player player: ServerView.players){
                                player.lobbyRespond(Type.RefreshRoomList, null);
                            }
                            Platform.runLater(() -> {
                                serverView.updateRoomList();
                            });
                        }
                        break;
                }
            }
        }catch (IOException e){
//            e.printStackTrace();
            cancelConnect();
            ServerView.players.remove(this);
            Platform.runLater(() -> {
                serverView.updatePlayerList();
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void lobbyRespond(Type type, String serverRespond, String roomName){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(lobbySocket.getOutputStream());
            Data data = new Data(type, serverRespond, roomName);
            if (type == Type.RefreshRoomList){
                String buffer = "";
                for (GameRoom room: ServerView.rooms){
                    buffer = buffer + room.toString();
                }
                data.serverRespond = buffer;
            }
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lobbyRespond(Type type, String message) {
        lobbyRespond(type, message, null);
    }

    public void roomRequest(){
        try {
            while (true){
                ObjectInputStream inputStream = new ObjectInputStream(roomSocket.getInputStream());
                GameData data = (GameData) inputStream.readObject();
                System.out.println("room: get a " + data.behavior + " data.");
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
            e.printStackTrace();
        }
    }

    private void roomRespond(GameData data) {
        try {
            GameRoom room = ServerView.getRoom(data.roomName);
            if (room != null){
                ObjectOutputStream outputStream = new ObjectOutputStream(roomSocket.getOutputStream());
                outputStream.writeObject(data);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            lobbySocket.close();
            if (roomSocket != null)
                roomSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
