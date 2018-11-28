import java.io.IOException;
import java.net.Socket;

public class ClientSocket {
    public static Socket socket;
    public String name;

    ClientSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }
}
