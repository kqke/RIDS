package huji.protocols.replica;

import huji.messages.impl.ClientMessage;
import huji.messages.Message;
import huji.protocols.AbstractProtocol;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaProtocol extends AbstractProtocol {
    private Queue<Message> _in_channel;
    private Queue<String> _clients_messages;

    ReplicaProtocol() {
        super();
        _in_channel = new ConcurrentLinkedQueue<>();
        _clients_messages = new ConcurrentLinkedQueue<>();
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
        viewChange();
        offer();
        while( isRun() ) {
            if ( ! _in_channel.isEmpty() ) {
                handle(_in_channel.poll());
            }
        }
    }

    String getClientMessage() {
        return _clients_messages.peek();
    }

    void deleteClientMessage() {
        _clients_messages.remove();
    }

    abstract protected void offer();

    abstract protected void handle(Message message);

    void clientMessage(ClientMessage message) {
        _clients_messages.add(message.value);
    }

    abstract void viewChange();
}
