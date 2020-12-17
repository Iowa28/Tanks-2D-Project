package protocol;

public class Message {

    private String messageText = "";

    public String registerPacket(int x, int y) {
        messageText = "Hello" + x + "," + y;
        return messageText;
    }
    public String updatePacket(int x, int y, int id, int dir) {
        messageText ="Update" + x + "," + y + "-" + dir + "|" + id;
        return messageText;
    }
    public String shotPacket(int id) {
        messageText ="Shot" + id;
        return messageText;
    }

    public String removeClientPacket(int id) {
        messageText ="Remove" + id;
        return messageText;
    }
    public String exitMessagePacket(int id) {
        messageText = "Exit" + id;
        return messageText;
    }
}
