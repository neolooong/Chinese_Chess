import Data.Data;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ServerView {
    private static int default_port = 16888;
    private ServerSocket serverSocket;
    private HashMap<String, Socket> players = new HashMap<>();  // 玩家名稱 -> 連線物件
    private HashMap<String, String[]> rooms = new HashMap<>();  // 房間名稱 -> 玩家名稱陣列

    public VBox playerList, roomList;

    public void initialize() throws IOException {
        serverSocket = new ServerSocket(default_port);
        new Thread(() -> {
            while (players.size() < 100) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> {
                        String playerName = "";
                        while (true) {
                            try {
                                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                Data data;
                                data = (Data) inputStream.readObject();
                                switch (data.type) {
                                    case Connect:
                                        playerName = data.playerName;
                                        if (!players.containsKey(playerName)) {
                                            players.put(playerName, socket);
                                            data = new Data(Data.Type.ConnectStatus);
                                            data.connectRespond = "OK";
                                            data.rooms = rooms;
                                            outputStream.writeObject(data);
                                            outputStream.flush();
                                        } else {
                                            data = new Data(Data.Type.ConnectStatus);
                                            data.connectRespond = "名稱重複了";
                                            outputStream.writeObject(data);
                                            outputStream.flush();
                                            socket.close();
                                        }
                                        Platform.runLater(() -> ServerView.this.updatePlayerList());
                                        break;
                                    case CreateRoom:
                                        if (!rooms.containsKey(data.roomName)){
                                            rooms.put(data.roomName, new String[]{playerName, null});
                                            data = new Data(Data.Type.CreateRoomStatus);
                                            data.createRoomRespond = "OK";
                                            outputStream.writeObject(data);
                                            outputStream.flush();
                                            sendNewRoomlist();
                                            Platform.runLater(() -> ServerView.this.updateRoomList());
                                        }else {
                                            data = new Data(Data.Type.CreateRoomStatus);
                                            data.createRoomRespond = "房間名稱重複了";
                                            outputStream.writeObject(data);
                                            outputStream.flush();
                                        }
                                        break;
                                    case EnterRoom:
                                        String people[] = rooms.get(data.roomName);
                                        if (!people[0].equals(playerName)){
                                            if (people[1] == null) {
                                                people[1] = playerName;
                                                data = new Data(Data.Type.EnterRoomStatus);
                                                data.EnterRoomRespond = "OK";
                                                data.rooms = rooms;
                                                outputStream.writeObject(data);
                                                outputStream.flush();
                                            }else {
                                                data = new Data(Data.Type.EnterRoomStatus);
                                                data.EnterRoomRespond = "不玩第三";
                                                outputStream.writeObject(data);
                                                outputStream.flush();
                                            }
                                        }else {
                                            data = new Data(Data.Type.EnterRoomStatus);
                                            data.EnterRoomRespond = "沒朋友只能自己跟自己玩 可憐";
                                            outputStream.writeObject(data);
                                            outputStream.flush();
                                        }
                                        break;
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                players.remove(playerName);
                                Platform.runLater(() -> ServerView.this.updatePlayerList());
                                Platform.runLater(() -> ServerView.this.updateRoomList());
                                break;
                            }
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
        Iterator<String> iterator = players.keySet().iterator();
        while (iterator.hasNext()){
            Label label = new Label(iterator.next());
            playerList.getChildren().add(label);
        }
    }

    public void updateRoomList(){
        roomList.getChildren().clear();
        Iterator<String> iterator = rooms.keySet().iterator();
        while (iterator.hasNext()){
            String buffer = iterator.next();
            Label label = new Label("房名: " + buffer + "   房主: " + rooms.get(buffer)[0] + "  房間人數: " + (rooms.get(buffer)[1] == null ? "1":"2"));
            roomList.getChildren().add(label);
        }
    }

    public void sendNewRoomlist() throws IOException {
        for (Socket socket: players.values()){
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Data data = new Data(Data.Type.RefreshRoomList);
            data.rooms = rooms;
            outputStream.writeObject(data);
            outputStream.flush();
        }
    }
}
