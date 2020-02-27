package huji.messages;

public abstract class Message {
    final private int _from;
    final private int _to;

    public Message( int from, int to ) {
        _from = from;
        _to = to;
    }

    public int from() { return _from; }
    public int to() { return _to; }

    abstract public void run();

    @Override
    public String toString() {
        return "message from " + _from + ", to: " + _to;
    }
}
