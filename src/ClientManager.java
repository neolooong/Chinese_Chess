import Data.Data;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.*;
import java.net.Socket;

public class ClientManager {
    private Socket socket;
    public Parent loginRoot;
    public Parent lobbyRoot;
    private ClientLogin clientLogin;
    private ClientLobby clientLobby;

    ClientManager() throws IOException {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
        loginRoot = loader.load();
        clientLogin = loader.getController();
        clientLogin.setClientManager(this);

        loader = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
        lobbyRoot = loader.load();
        clientLobby = loader.getController();
        clientLobby.setClientManager(this);
    }

    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
    }

    public void openInputStream(){
        new Thread(() -> {
            while (true){
                try{
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    Data data = (Data) inputStream.readObject();

                }catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }
}
