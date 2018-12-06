import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class GameManager {
    private static int port = 12345;
    private ServerSocket serverSocket;
    private PlayerManager playerManager;
    private HashMap<String, String[]> roomMap = new HashMap<>();  // 房間名稱 -> 玩家名稱陣列

    GameManager(){
//        serverSocket = new ServerSocket(port);
    }

//    建立房間
    public boolean createRoom(String roomname, String playername) throws IOException {
        if (roomMap.containsKey(roomname)){
            return false;
        }else {
            roomMap.put(roomname, new String[]{playername, null});
            return true;
        }
    }

//    進入房間
    public synchronized boolean enterRoom(String roomname, String playername){
        if (roomMap.get(roomname)[1] != null){
            return false;
        }else {
            roomMap.get(roomname)[1] = playername;
            return true;
        }
    }

//    離開房間
    public void quitRoom(String roomname, String playername){
        if (roomMap.get(roomname)[0].equals(playername) && roomMap.get(roomname)[1] == null){
            roomMap.remove(roomname);
        }else if (roomMap.get(roomname)[0].equals(playername) && roomMap.get(roomname)[1] != null){
            roomMap.get(roomname)[0] = roomMap.get(roomname)[1];
            roomMap.get(roomname)[1] = null;
        }else {
            roomMap.get(roomname)[1] = null;
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

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
}
