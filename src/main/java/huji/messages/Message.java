package huji.messages;


import java.util.UUID;

public abstract class Message extends Delayable {
    final public int from;
    final public int to;

    final public String body;

    public Message(int from, int to, String body ) {
        super();
        this.from = from;
        this.to = to;
        this.body = ( body != null ) ? body : "";
    }

    @Override
    public String toString() {
        return "Message{ from:" + from +  ", to:" + to + ", body: " + body + "}";
    }
}
