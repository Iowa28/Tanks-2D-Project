import logic.ClientInfo;
import protocol.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {

    private ArrayList<ClientInfo> clients;
    private ServerSocket serverSocket;
    private int serverPort = 11111;


    private DataInputStream reader;
    private DataOutputStream writer;

    private Message message;
    private boolean running = true;

    public Server() throws SocketException {
        clients = new ArrayList<ClientInfo>();
        message = new Message();
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        Socket clientSocket = null;
        while(running) {
            try {
                clientSocket  =serverSocket.accept();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            String sentence = "";
            try {
                reader = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                sentence = reader.readUTF();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(sentence);
            if(sentence.startsWith("Hello")) {
                int pos = sentence.indexOf(',');
                int x = Integer.parseInt(sentence.substring(5, pos));
                int y = Integer.parseInt(sentence.substring(pos + 1, sentence.length()));

                try {
                    writer = new DataOutputStream(clientSocket.getOutputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                sendToClient(message.idPacket(clients.size() + 1));
                try {
                    BroadCastMessage(message.newClientPacket(x, y, 1, clients.size() + 1));
                    sendAllClients(writer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                clients.add(new ClientInfo(writer, x, y ,2));
            }

            else if (sentence.startsWith("Update")) {
                int pos1 = sentence.indexOf(',');
                int pos2 = sentence.indexOf('-');
                int pos3 = sentence.indexOf('|');
                int x = Integer.parseInt(sentence.substring(6, pos1));
                int y = Integer.parseInt(sentence.substring(pos1 + 1,pos2));
                int dir = Integer.parseInt(sentence.substring(pos2 + 1,pos3));
                int id = Integer.parseInt(sentence.substring(pos3 + 1, sentence.length()));

                if(clients.get(id - 1) != null) {
                    clients.get(id - 1).setPosX(x);
                    clients.get(id - 1).setPosY(y);
                    clients.get(id - 1).setDirection(dir);
                    try {
                        BroadCastMessage(sentence);
                    } catch (IOException ex) {
                        System.out.println("Couldn't send broadcast message!");
                    }
                }

            }
            else if (sentence.startsWith("Shot")) {
                try {
                    BroadCastMessage(sentence);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (sentence.startsWith("Remove")) {
                int id = Integer.parseInt(sentence.substring(6));

                try {
                    BroadCastMessage(sentence);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                clients.set(id - 1, null);
            }
            else if (sentence.startsWith("Exit")) {
                int id = Integer.parseInt(sentence.substring(4));

                try {
                    BroadCastMessage(sentence);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (clients.get(id - 1) != null)
                    clients.set(id - 1, null);
            }
        }

        try {
            reader.close();
            writer.close();
            serverSocket.close();
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void stopServer() throws IOException {
        running=false;
    }

    public void BroadCastMessage(String mess) throws IOException {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i) != null) {
                //System.out.println("Sending message to client №" + i + ": " + mess);
                clients.get(i).getWriterStream().writeUTF(mess);
            }
        }
    }

    public void sendToClient(String message)
    {
        if (message.equals("exit"))
            System.exit(0);
        else {
            try {
                System.out.println("Write UTF: " + message);
                writer.writeUTF(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendAllClients(DataOutputStream writer) {
        int x, y, dir;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) != null) {
                x = clients.get(i).getX();
                y = clients.get(i).getY();
                dir = clients.get(i).getDir();
                try {
                    writer.writeUTF(message.newClientPacket(x, y, dir,i + 1));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
