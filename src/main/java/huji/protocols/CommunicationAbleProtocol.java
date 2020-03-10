package huji.protocols;

import huji.channels.Channel;
import huji.messages.Message;
import huji.messages.MessageType;

import java.util.LinkedList;
import java.util.Queue;

public abstract class CommunicationAbleProtocol extends Protocol {
    private Queue<Message> incoming_queue;

    protected CommunicationAbleProtocol() {
        super();
        incoming_queue = new LinkedList<>();
    }

    // Communication Channel

    private Channel<Message> getCommunicationChannel() {
        return getEnvironment().getCommunicationChannel();
    }

    // Send

    protected void send( Message message ) {
        getCommunicationChannel().send( message );
    }

    protected void sendToAll( Message message ) {
        for ( int id : getEnvironment().getReplicasIDs() ) {
            send( message.copy( id ) );
        }
    }

    // Receive

    public void receive( Message message ) {
        incoming_queue.add( message );
    }

    // Process

    @Override
    protected void running_process() {
        if ( ! incoming_queue.isEmpty() )
            handle( incoming_queue.poll() );
    }

    abstract protected boolean handle( Message message );
}
