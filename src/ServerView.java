import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

public class ServerView {
    private static int default_port = 16888;
    private ServerSocket serverSocket;
    private PlayerManager playerManager = new PlayerManager();
    private GameManager gameManager = new GameManager();

    public VBox playerList, roomList;

    public void initialize() throws IOException {
        gameManager.setPlayerManager(playerManager);
        playerManager.setGameManager(gameManager);
        playerManager.setServerView(this);

        serverSocket = new ServerSocket(default_port);
        gameManager.connect();
        new Thread(() -> {
            while (playerManager.size() < 100) {
                try {
                    Socket socket = serverSocket.accept();
                    playerManager.playerAdd(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updatePlayerList(){
        playerList.getChildren().clear();
        Iterator<String> iterator = playerManager.getPlayerMap().keySet().iterator();
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
