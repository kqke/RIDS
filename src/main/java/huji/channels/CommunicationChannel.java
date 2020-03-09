package huji.channels;

import huji.interfaces.Channel;
import huji.interfaces.Protocol;
import huji.messages.Message;
import huji.protocols.replica.ReplicaProtocol;
import huji.simulator.Simulator;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationChannel implements Channel {
    private boolean _is_run = true;

    private Queue<Message> _communication_channel;
    private Simulator _simulator;

    public CommunicationChannel() {
        _communication_channel = new ConcurrentLinkedQueue<>();
    }

    public void setSimulator(Simulator simulator) {
        _simulator = simulator;
    }

    public void sendMessage(Message message ) {
        _communication_channel.add(message);
    }

    @Override
    public void run() {
        Message message = null;
        while( _is_run ) {
            if ( ! _communication_channel.isEmpty() ) {
                message = _communication_channel.poll();
                if ( message.to == -1 )
                    for (Protocol protocol : _simulator.getProtocols()) {
                        ((ReplicaProtocol)protocol).inChannel( message );
                    }
                else
                    ((ReplicaProtocol)_simulator.getProtocol( message.to )).inChannel( message );
            }
        }
    }
}
