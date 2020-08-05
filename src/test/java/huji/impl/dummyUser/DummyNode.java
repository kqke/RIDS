package huji.impl.dummyUser;

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
    protected boolean running_condition() {
        return true;
    }

    @Override
    protected void running_process() {
        // TODO: send to all
        shutdown();
    }

    @Override
    protected boolean handle(Message<T> msg) {
        return false;
    }
}
