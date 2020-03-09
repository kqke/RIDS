package huji.messages;

public class ElectMessage extends Message{

    final int share;

    public ElectMessage(int view, int from, int to, int share) {
        super(Type.ELECT, view, from, to);
        this.share = share;
    }


}
