import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class Client {
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    String userName;

    public Client(Socket socket, BufferedReader reader, BufferedWriter writer, String userName) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.userName = userName;
    }
}
