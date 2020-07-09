package huji.communication.messages;


import java.util.UUID;

public abstract class Message extends Delayable {
    final public UUID from;
    final public UUID to;

    final public String body;

    public Message(UUID from, UUID to, String body ) {
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
