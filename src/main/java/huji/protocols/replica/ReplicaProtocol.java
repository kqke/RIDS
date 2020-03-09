package huji.protocols.replica;

import huji.messages.Message;
import huji.protocols.AbstractProtocol;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaProtocol extends AbstractProtocol {
    private Queue<Message> _in_channel;

    ReplicaProtocol() {
        super();
        _in_channel = new ConcurrentLinkedQueue<>();
    }

    void outChannel( Message message ) {
        if ( getCommunication() != null )
            getCommunication().sendMessage(message);
    }

    public void inChannel( Message message ) {
        _in_channel.add(message);
    }

    @Override
    public void run() {
        while( isRun() ) {
            if ( ! _in_channel.isEmpty() ) {
                handle(_in_channel.poll());
            }
        }
    }

    abstract protected void handle(Message message);
}
