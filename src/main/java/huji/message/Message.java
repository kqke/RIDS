package huji.message;

public abstract class Message<T> extends Delayable {
    final public int from;
    final public int to;

    final public boolean isClient;

    final public T body;

    public Message(int from, int to, T body, boolean isClient ) {
        super();
        this.from = from;
        this.to = to;
        this.body = body;
        this.isClient = isClient;
    }

    public Message(Message<T> other, int to) {
        super();
        this.from = other.from;
        this.body = other.body;
        this.isClient = other.isClient;
        this.to = to;
    }

    abstract public Message<T> copy(int to);

    @Override
    public String toString() {
        return "Message{ from:" + from +  ", to:" + to + ", body: " + body.toString() + "}";
    }
}
