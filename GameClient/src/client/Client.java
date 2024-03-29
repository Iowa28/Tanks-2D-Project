package client;

import protocol.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private String hostName;
    private int serverPort;
    private DataInputStream reader;
    private DataOutputStream writer;
    private Message message;

    private static Client client;
    private Client() throws IOException {
        message = new Message();
    }

    public void register(String Ip, int port, int posX, int posY) throws IOException, IllegalArgumentException {
        this.serverPort = port;
        this.hostName = Ip;
        clientSocket = new Socket(Ip, port);
        writer = new DataOutputStream(clientSocket.getOutputStream());

        writer.writeUTF(message.registerPacket(posX, posY));


    }

    public void sendToServer(String message) {
        if(message.equals("exit"))
            System.exit(0);
        else
        {
            try {
                Socket s = new Socket(hostName,serverPort);
                writer = new DataOutputStream(s.getOutputStream());
                writer.writeUTF(message);
            } catch (IOException ex) {

            }
        }

    }

    public Socket getSocket() {
        return clientSocket;
    }
    public String getIP() {
        return hostName;
    }
    public static Client getGameClient() {
        if(client==null)
            try {
                client = new Client();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        return client;
    }
    public void closeAll() {
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException ex) {
            //TODO: cathing close exeption
        }
    }
}
