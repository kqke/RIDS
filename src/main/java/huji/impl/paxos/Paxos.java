package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosValue;
import huji.node.Node;

public class Paxos extends Node<PaxosMessage, PaxosValue> {
    public Paxos(CommunicationChannel<PaxosMessage, PaxosValue> channel) {
        super(channel);
    }

    @Override
    protected void handle(PaxosMessage msg) {

    }
}
