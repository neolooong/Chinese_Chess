import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class ServerGame extends Thread{
    private ServerSocket serverSocket;
    private Socket player1;
    private Socket player2;
    private int port;

    ServerGame(int port){
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            player1 = serverSocket.accept();
            player2 = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Player extends Thread{
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        Player (Socket socket){
            this.socket = socket;
        }
    }
}