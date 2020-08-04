package huji.impl.dummyUser;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosValue;
import huji.message.Message;
import huji.node.Node;

public class DummyNode extends Node<PaxosValue> {

    public DummyNode(CommunicationChannel<PaxosValue> channel) {
        super(channel);
    }

    @Override
    protected boolean running_condition() {
        return true;
    }

    @Override
    protected void running_process() {
        // TODO: send to all
        shutdown();
    }

    @Override
    protected boolean handle(Message<PaxosValue> msg) {
        return false;
    }
}
