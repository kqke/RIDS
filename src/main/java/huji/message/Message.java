package huji.message;

public abstract class Message<T> extends Delayable {
    final public int from;
    final public int to;

    final public T body;

    public Message(int from, int to, T body ) {
        super();
        this.from = from;
        this.to = to;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{ from:" + from +  ", to:" + to + ", body: " + body.toString() + "}";
    }
}
