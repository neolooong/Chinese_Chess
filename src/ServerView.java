import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerView {
    private static int default_port = 16888;
    private static ArrayList<Socket> players = new ArrayList<>();
    private ServerSocket serverSocket;

    public VBox playerList, roomList;

    private Thread acceptPlayer = new Thread(new Runnable() {
        @Override
        public void run() {
            while (players.size() < 100) {
                try {
                    Socket socket = serverSocket.accept();
                    players.add(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public void initialize() throws IOException {
        serverSocket = new ServerSocket(default_port);
        acceptPlayer.start();
    }
}