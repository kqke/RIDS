package huji.messages;

public class AckMessage extends Message {

    public AckMessage(int view, int from, int to) {
        super(Type.ACK, view, from, to);
    }
}
