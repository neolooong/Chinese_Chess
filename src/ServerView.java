import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class ServerView {
    private static int LobbyPort = 16888;
    private static int RoomPort = 12345;
    private LobbyManager lobbyManager = new LobbyManager(); // 大廳管理者
    private GameManager gameManager = new GameManager();    // 遊戲管理者

    private HashSet<Player> players = new HashSet<>();      // 玩家清單

    public VBox playerList, roomList;

    public void initialize() throws IOException {
        lobbyManager.setGameManager(gameManager);
        gameManager.setLobbyManager(lobbyManager);

        lobbyManager.setPlayers(players);
        gameManager.setPlayers(players);

        lobbyManager.setServerView(this);

        lobbyManager.connect(LobbyPort);
        gameManager.connect(RoomPort);
    }

    public void updatePlayerList(){
        playerList.getChildren().clear();
        Iterator<String> iterator = lobbyManager.getPlayerMap().keySet().iterator();
        while (iterator.hasNext()){
            Label label = new Label(iterator.next());
            playerList.getChildren().add(label);
        }
    }

    public void updateRoomList(){
        roomList.getChildren().clear();
        Iterator<String> iterator = gameManager.getRoomMap().keySet().iterator();
        while (iterator.hasNext()){
            String buffer = iterator.next();
            String players[] = gameManager.getRoomPlayers(buffer);
            Label label = new Label("房名: " + buffer + "   房主: " + players[0] + "  房間人數: " + (players[1] == null ? "1":"2"));
            roomList.getChildren().add(label);
        }
    }
}
