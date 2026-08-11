import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable {
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    String userName;
    Client client;

    public static Vector<Client> clients = new Vector<>();
    
    public Server (Socket socket) {
        this.socket = socket;
    }

    public void broadcast(Client client, String message){
        for (int i=clients.size()-1;i>=0;i--){
            try {
                Client c = clients.get(i);

                if (c == client)    continue;

                c.writer.write(message);
                c.writer.write("\r\n");
                c.writer.flush();
            }
            catch (Exception e){
                clients.remove(i);
            }
        }
    }

    public void serverBroadcast(String message){
        for (int i=clients.size()-1;i>=0;i--){
            try {
                Client c = clients.get(i);

                c.writer.write(message);
                c.writer.write("\r\n");
                c.writer.flush();
            }
            catch (Exception e){
                clients.remove(i);
            }
        }
    }
    
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            userName = reader.readLine();

            client = new Client(socket, reader, writer, userName);
            
            clients.add(client);

            serverBroadcast(userName + " joined the chat");
            serverBroadcast("ONLINECOUNT-->"+clients.size());

            String data;
            
            while((data = reader.readLine()) != null) {
                data = data.trim();

                if (data.isEmpty()) { continue; }

                broadcast(client, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            String name = client.userName;
            clients.remove(client);
            serverBroadcast(name + " left the chat");
            serverBroadcast("ONLINECOUNT-->"+clients.size());
            try{
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        ServerSocket s = new ServerSocket(2005);
        while(true) {
            Socket socket = s.accept();
            Server server = new Server(socket);
            Thread thread = new Thread(server);
            thread.start();
        }
    }
}
