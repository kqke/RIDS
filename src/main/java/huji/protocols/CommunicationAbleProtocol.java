package huji.protocols;

import huji.channels.Channel;
import huji.events.EventType;
import huji.messages.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CommunicationAbleProtocol extends Protocol {
    private ConcurrentLinkedQueue<Message> incoming_queue;

    protected CommunicationAbleProtocol() {
        super();
        incoming_queue = new ConcurrentLinkedQueue<>();
    }

    // Communication Channel

    private Channel<Message> getCommunicationChannel() {
        return getEnvironment().getCommunicationChannel();
    }

    // Send

    protected void send( Message message ) {
        getCommunicationChannel().send( message );
        event(EventType.NEW_MESSAGE,message.toString());
    }

    protected void sendToAll( Message message ) {
        for ( int id : getEnvironment().getReplicasIDs() ) {
            send( message.copy( id ) );
        }
        event(EventType.NEW_MESSAGE_TO_ALL,message.toString());
    }

    // Receive

    public void receive( Message message ) {
        incoming_queue.add( message );
        event(EventType.RECEIVE_MESSAGE,message.toString());
    }

    // Process

    @Override
    protected void running_process() {
        if ( ! incoming_queue.isEmpty() )
            handle( incoming_queue.poll() );
    }

    abstract protected boolean handle( Message message );
}
