package huji.channels;

import huji.interfaces.Channel;
import huji.messages.Message;
import huji.simulator.Simulator;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationChannel implements Channel {
    private Queue<Message> _communication_channel;
    List<Simulator.Agent>

    public CommunicationChannel() {
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
