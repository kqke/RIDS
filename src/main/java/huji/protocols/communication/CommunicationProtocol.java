package huji.protocols.communication;

import huji.messages.Message;
import huji.protocols.AbstractProtocol;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationProtocol extends AbstractProtocol {
    private Queue<Message> _communication_channel;

    public CommunicationProtocol() {
        _communication_channel = new ConcurrentLinkedQueue<>();
    }

    public void sendMessage( Message message ) {
        _communication_channel.add(message);
    }

    @Override
    public void run() {
        while( isRun() ) {

        }
    }
}
