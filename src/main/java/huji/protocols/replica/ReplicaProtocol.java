package huji.protocols.replica;

import huji.events.EventType;
import huji.messages.*;
import huji.protocols.CommunicationAbleProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaProtocol extends CommunicationAbleProtocol {
    private Map<Integer,String> decided;
    private Queue<String> clients_messages;
    private boolean has_proposed_this_view;
    private int view;

    ReplicaProtocol() {
        super();

        this.decided = new HashMap<>();
        this.clients_messages = new ConcurrentLinkedQueue<>();
        this.has_proposed_this_view = false;
        this.view = 0;
    }

    // Decide

    protected void decide( String value ) {
        decided.put(view, value);
        event(EventType.DECIDE, value);
        if ( value.equals( clients_messages.peek() ) )
            clients_messages.remove();
    }

    // View Change
    protected int view() {
        return view;
    }

    void viewChange() {
        this.has_proposed_this_view = false;
    }

    boolean setView( int view ) {
        if (this.view < view) {
            this.view = view;
            return true;
        }

        return false;
    }

    void increaseView() {
        ++view;
        has_proposed_this_view = false;
    }

    // Send

    protected void send( MessageType type, int to, String body ) {
        super.send( new ViewMessage( type, id(), to, body, view ) );
    }

    void send( MessageType type, int to ) {
        send( new ViewMessage( type, id(), to, "", view ) );
    }

    void sendToAll( MessageType type, String body ) {
        super.sendToAll( new ViewMessage( type, id(), body, view ) );
    }

    void sendToAll( MessageType type ) {
        sendToAll( new ViewMessage( type, id(), "", view ) );
    }

    // Receive Messages

    @Override
    protected boolean handle(Message message) {
        if ( message.messageType == MessageType.CLIENT ) {
            clients_messages.add( message.body );
            return true;
        }

        return false;
    }

    // Shared Information

    protected int N() {
        return (Integer) getSharedInformation("N");
    }

    protected int F() {
        return (Integer) getSharedInformation("F");
    }

    // Process

    @Override
    protected void running_process() {
        if ( ! has_proposed_this_view && ! clients_messages.isEmpty() ) {
            sendToAll( new ViewMessage(MessageType.PROPOSE,id(),clients_messages.peek(),view) );
            has_proposed_this_view = true;
        }

        super.running_process();
    }
}
