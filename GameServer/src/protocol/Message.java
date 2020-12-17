package protocol;

public class Message {
    private String messageText = "";

    public String idPacket(int id) {
        messageText = "ID" + id;
        return messageText;
    }

    public String newClientPacket(int x, int y, int dir, int id) {
        messageText = "NewClient" + x + "," + y + "-" + dir + "|" + id;
        return messageText;
    }
}
