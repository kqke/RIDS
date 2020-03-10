package huji.messages;

public class ViewMessage extends Message {
    final public int view;

    public ViewMessage(MessageType messageType, int from, int to, String body, int view) {
        super(messageType, from, to, body);
        this.view = view;
    }

    public ViewMessage(MessageType messageType, int from, String body, int view) {
        this(messageType,from,-1,body,view);
    }

    public ViewMessage(MessageType messageType, int from, int to, int view) {
        this(messageType,from,to,null,view);
    }

    public ViewMessage(MessageType messageType, int from, int view) {
        this(messageType,from,-1,null,view);
    }

    @Override
    public ViewMessage copy( int to ) {
        return new ViewMessage( messageType, from, to, body, view );
    }

    @Override
    public String toString() {
        return super.toString() + ".view=" + view;
    }
}