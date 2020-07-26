package huji.impl.dummyUser;

import huji.channel.CommunicationChannel;
import huji.message.Message;
import huji.node.Node;

public class DummyNode<T extends Message<R>, R> extends Node<T,R> {
    public DummyNode(CommunicationChannel<T, R> channel) {
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
    protected boolean handle(T msg) {
        return false;
    }
}
