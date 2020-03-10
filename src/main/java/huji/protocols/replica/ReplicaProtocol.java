package huji.protocols.replica;

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

        if ( value.equals( clients_messages.peek() ) )
            clients_messages.remove();
    }

    // View Change
    protected void viewChange() {
        this.has_proposed_this_view = false;
    }

    protected boolean setView( int view ) {
        if (this.view < view) {
            this.view = view;
            return true;
        }

        return false;
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

    // Process

    @Override
    protected void running_process() {
        if ( ! has_proposed_this_view && ! clients_messages.isEmpty() ) {
            sendToAll( new ViewMessage(MessageType.PROPOSE,id(),clients_messages.peek(),view) );
        }

        super.running_process();
    }
}
