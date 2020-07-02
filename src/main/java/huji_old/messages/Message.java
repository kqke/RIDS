package huji_old.messages;

import huji_old.interfaces.Delayable;

public class Message extends Delayable {
    final public int from;
    final public int to;

    final public MessageType messageType;
    final public String body;

    public Message( MessageType messageType, int from, int to, String body ) {
        super();

        this.messageType = messageType;
        this.from = from;
        this.to = to;
        this.body = ( body != null ) ? body : "";
    }

    public Message(MessageType messageType, int from, int to ) {
        this(messageType,from, to, "");
    }

    public Message(MessageType messageType, int from, String body ) {
        this(messageType,from, -1, body);
    }

    public Message(MessageType messageType, int from ) {
        this(messageType,from, -1, "");
    }

    public Message copy( int to ) {
        return new Message( messageType, from, to, body );
    }

    @Override
    public String toString() {
        return "Message{ from:" + from +  ", to:" + to + ", messageType:" + messageType + ", body: " + body + "}";
    }
}
