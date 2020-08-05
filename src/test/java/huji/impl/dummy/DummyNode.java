package huji.impl.dummy;

import huji.channel.CommunicationChannel;
import huji.interfaces.Factory;
import huji.message.Message;
import huji.node.Node;

public class DummyNode<T> extends Node<T> {
    final Factory<T,String> factory;

    public DummyNode(CommunicationChannel<T> channel, Factory<T,String> factory) {
        super(channel);
        this.factory = factory;
    }

    @Override
    protected boolean runningCondition() {
        return true;
    }

    @Override
    protected void runningProcess() {
        // TODO: send to all
        shutdown();
    }

    @Override
    protected boolean handle(Message<T> msg) {
        return false;
    }
}
