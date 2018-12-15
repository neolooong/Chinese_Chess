package Datas;

import java.io.Serializable;

public class Data implements Serializable{
    public enum Type{
        Connect, ConnectStatus, CreateRoom, CreateRoomStatus, RefreshRoomList, EnterRoom, EnterRoomStatus, QuitRoom
    }

    public Type type;

    public String playerName;
    public String serverRespond;
    public String roomName;

    public Data(Type type){
        this.type = type;
    }

    public Data(Type type, String serverRespond, String roomName) {
        this.type = type;
        this.serverRespond = serverRespond;
        this.roomName = roomName;
    }
}
