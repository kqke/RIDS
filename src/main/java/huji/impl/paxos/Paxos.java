package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.messages.Message;
import huji.nodes.Node;

public class Paxos extends Node<PaxosMessage> {
    public Paxos(CommunicationChannel<PaxosMessage> channel) {
        super(channel);
    }

    @Override
    protected void handle(PaxosMessage msg) {

    }
}
