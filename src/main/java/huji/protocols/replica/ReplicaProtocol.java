package huji.protocols.replica;

import huji.messages.Message;
import huji.protocols.AbstractProtocol;
import huji.channels.CommunicationChannel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaProtocol extends AbstractProtocol {
    private Queue<Message> _in_channel;
    private CommunicationChannel _communication = null;

    public ReplicaProtocol() {
        super();
        _in_channel = new ConcurrentLinkedQueue<>();
    }

    public void setCommunicationProtocol (CommunicationChannel communication_protocol) {
        this._communication = communication_protocol;
    }

    protected void outChannel( Message message ) {
        if ( _communication != null )
            _communication.sendMessage(message);
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
