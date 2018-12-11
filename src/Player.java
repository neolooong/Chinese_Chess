import java.net.Socket;

public class Player {
    public String name;
    public Socket lobbySocket;
    public Socket roomSocket;

    private LobbyManager lobbyManager;
    private GameManager gameManager;

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public void setLobbyManager(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
