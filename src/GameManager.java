import Datas.GameData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class GameManager {
    private ServerSocket serverSocket;
    private LobbyManager lobbyManager;
    private HashMap<String, Socket> playerMap = new HashMap<>();    // 玩家名稱 -> 連線
    private HashMap<String, String[]> roomMap = new HashMap<>();    // 房間名稱 -> 玩家名稱陣列

//    0: 0個準備; 1: 1個準備; 2: 2個準備、遊戲開始
    private HashMap<String, Integer> roomReadyStatus = new HashMap<>();

    GameManager(){

    }

//    連線
    public void connect(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        new Thread(() -> {
            try{
                while (playerMap.size() < 100){
                    Socket socket = serverSocket.accept();
                    openInputStream(socket);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

//    開啟接受通道
    public void openInputStream(Socket socket) {
        new Thread(() -> {
            String playername = "";
            try{
                while (true){
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    GameData data = (GameData) inputStream.readObject();
                    switch (data.behavior){
                        case Register:
                            playername = data.source;
                            playerMap.put(playername, socket);
                            System.out.println(getClass().getName() + ": " + playername + " add...");
                            break;
                        case CheckIn:
                            send2player(data.roomName, "Server", GameData.Behavior.CheckIn, null);
                            System.out.println(getClass().getName() + ": " + playername + " Entry " + data.roomName + ".");
                            break;
                        case Ready:
                            roomReadyStatus.replace(data.roomName, roomReadyStatus.get(data.roomName) + 1);
                            System.out.println(getClass().getName() + ": " + playername + " had ready.");
                            if (roomReadyStatus.get(data.roomName) == 2){
                                send2player(data.roomName, "Server", GameData.Behavior.GameStart, "遊戲開始");
                            }
                            break;
                        case Move:
                            send2player(data);
                            break;
                        case RequestUnMove:
                            send2player(data);
                            break;
                        case PermitUnMove:
                            send2player(data);
                            break;
                        case RejectUnMove:
                            send2player(data);
                            break;
                        case GameEnd:
                            send2player(data);
                            roomReadyStatus.replace(data.roomName, 0);
                            break;
                        case Surrender:
                            send2player(data);
                            roomReadyStatus.replace(data.roomName, 0);
                            break;
                    }
                }
            }catch (IOException | ClassNotFoundException e){
                playerMap.remove(playername);
                System.out.println(getClass().getName() + ": " + playername + " leave...");
//                e.printStackTrace();
            }
        }).start();
    }

//    傳送訊息 (房名, 來源, 行為, 訊息)
    public void send2player(String roomname, String source, GameData.Behavior behavior, String message) throws IOException{
        switch (behavior){
            case ServerMessage:
                for (String s:roomMap.get(roomname)){
                    if (s != null){
                        ObjectOutputStream outputStream = new ObjectOutputStream(playerMap.get(s).getOutputStream());
                        GameData data = new GameData(roomname, source, behavior);
                        data.message = message;
                        outputStream.writeObject(data);
                        outputStream.flush();
                        System.out.println(getClass().getName() + " send a " + behavior + " data to " + s + ".");
                    }
                }
                break;
            case CheckIn:
                for (String s:roomMap.get(roomname)){
                    if (s != null){
                        ObjectOutputStream outputStream = new ObjectOutputStream(playerMap.get(s).getOutputStream());
                        GameData data = new GameData(roomname, source, behavior);
                        data.players = roomMap.get(roomname);
                        outputStream.writeObject(data);
                        outputStream.flush();
                        System.out.println(getClass().getName() + " send a " + behavior + " data to " + s + ".");
                    }
                }
                break;
            case CheckOut:
                if (roomMap.size() != 0 && roomMap.containsKey(roomname)){
                    for (String s:roomMap.get(roomname)){
                        if (s != null){
                            ObjectOutputStream outputStream = new ObjectOutputStream(playerMap.get(s).getOutputStream());
                            GameData data = new GameData(roomname, source, behavior);
                            data.players = roomMap.get(roomname);
                            outputStream.writeObject(data);
                            outputStream.flush();
                            System.out.println(getClass().getName() + " send a " + behavior + " data to " + s + ".");
                        }
                    }
                }
                break;
            case GameStart:
                for (String s: roomMap.get(roomname)){
                    ObjectOutputStream outputStream = new ObjectOutputStream(playerMap.get(s).getOutputStream());
                    GameData data = new GameData(roomname, source, behavior);
                    outputStream.writeObject(data);
                    outputStream.flush();
                    System.out.println(getClass().getName() + " send a " + behavior + " data to " + s + ".");
                }
                System.out.println(getClass().getName() + ": " + roomname + " start.");
                break;
        }
    }

    public void send2player(GameData data){
        try {
            for (String s: roomMap.get(data.roomName)){
                if (!s.equals(data.source)){
                    ObjectOutputStream outputStream = new ObjectOutputStream(playerMap.get(s).getOutputStream());
                    outputStream.writeObject(data);
                    outputStream.flush();
                    System.out.println(getClass().getName() + " send a " + data.behavior + " data to " + s + ".");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    建立房間
    public boolean createRoom(String roomname, String playername) throws IOException {
        if (roomMap.containsKey(roomname)){
            return false;
        }else {
            roomMap.put(roomname, new String[]{playername, null});
            roomReadyStatus.put(roomname, 0);
            return true;
        }
    }

//    進入房間
    public synchronized boolean enterRoom(String roomname, String playername){
        System.out.println(roomMap.get(roomname)[0]);
        System.out.println(playername);
        if (roomMap.get(roomname)[1] != null || roomMap.get(roomname)[0].equals(playername)){
            return false;
        }else {
            roomMap.get(roomname)[1] = playername;
            return true;
        }
    }

//    離開房間
    public void quitRoom(String roomname, String playername) throws IOException{
        if (roomMap.get(roomname)[0].equals(playername) && roomMap.get(roomname)[1] == null){
            roomMap.remove(roomname);
        }else if (roomMap.get(roomname)[0].equals(playername) && roomMap.get(roomname)[1] != null){
            roomMap.get(roomname)[0] = roomMap.get(roomname)[1];
            roomMap.get(roomname)[1] = null;
        }else {
            roomMap.get(roomname)[1] = null;
        }
        send2player(roomname, "Server", GameData.Behavior.CheckOut, null);
        if (roomReadyStatus.get(roomname) != 0){
            roomReadyStatus.replace(roomname, 0);
        }
    }

//    取得整個房間清單
    public HashMap<String, String[]> getRoomMap() {
        return roomMap;
    }

//    搜尋房間
    public String[] getRoomPlayers(String roomname){
        return roomMap.get(roomname);
    }

    public void setLobbyManager(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }
}
