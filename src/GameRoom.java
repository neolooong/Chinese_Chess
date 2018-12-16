import Datas.GameData.Behavior;

public class GameRoom {
    public String roomName;
    public Player host;
    public Player guest;

    public boolean hostReady = false;
    public boolean guestReady = false;

    GameRoom (String roomName, Player host){
        this.roomName = roomName;
        this.host = host;
    }

    public synchronized int enterRoom(Player player){
        if (host == player)
            return 2;       // Play with self.
        if (guest == null) {
            guest = player;
            return 1;       // OK
        }
        return 3;           // Full
    }

    public synchronized void quitRoom(Player player){
        resetRoom();
        if (host == player && guest == null) {
            ServerView.rooms.remove(this);
        }else if (host == player){
            host = guest;
            guest = null;
            host.roomRespond(roomName, "Server", Behavior.CheckOut);     // todo 'tell host that guest leave'
        }else if (player == guest){
            guest = null;
            host.roomRespond(roomName, "Server", Behavior.CheckOut);     // todo 'tell host that guest leave'
        }else {
            System.out.println("Some error that not sure how to happen");
        }
    }

    public void ready(Player player){
        if (host == player)
            hostReady = true;
        if (guest == player)
            guestReady = true;
        if (hostReady && guestReady) {
            host.roomRespond(roomName, "Server", Behavior.GameStart);
            guest.roomRespond(roomName, "Server", Behavior.GameStart);
        }
    }

    public void resetRoom(){
        hostReady = false;
        guestReady = false;
    }

    public Player getOpponent(Player player){
        return host == player? guest: host;
    }

    public int  howManyPlayerInTheRoom(){
        return guest == null ? 1 : 2;
    }

    @Override
    public String toString() {      // 房名 房主 人數
        return roomName + "," + host.name + "," + (guest == null? 1: 2) + ";";
    }
}
