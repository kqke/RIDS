package huji.messages;

public class ClientMessage extends Message {
    final public String value;

    public ClientMessage(int to, String value) {
        super(Type.CLIENT, -1, -1, to);
        this.value = value;
    }
}
