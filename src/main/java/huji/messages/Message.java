package huji.messages;

public abstract class Message {
    final public int view;
    final public int from;
    final public int to;

    final public Type type;

    public Message( Type type, int view, int from, int to ) {
        this.type = type;
        this.view = view;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "message from " + from + ", to: " + to;
    }
}
