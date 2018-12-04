package Data;

import java.io.Serializable;
import java.util.HashMap;

public class Data implements Serializable{
    public enum Type{
        Connect, ConnectStatus, CreateRoom, CreateRoomStatus, RefreshRoomList, EnterRoom, EnterRoomStatus
    }

    public Type type;

    public String playerName;
    public String connectRespond;

    public String roomName;
    public String createRoomRespond;

    public HashMap<String, String[]> rooms;

    public String EnterRoomRespond;

    public Data(Type type){
        this.type = type;
    }

}
