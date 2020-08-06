package huji.impl.dummy;

import huji.channel.CommunicationChannel;
import huji.interfaces.Factory;
import huji.message.Message;
import huji.node.Node;

public class DummyNode<T extends Comparable<T>> extends Node<T> {
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
        for ( int replica : channel.getReplicas() )
            for (int i = 0; i < 20; i++)
                send(new Message<>(
                        0,
                        replica,
                        factory.get( "r" + replica + "i" + i ),
                        true
                ));
        shutdown();
    }

    @Override
    protected boolean handle(Message<T> msg) {
        return false;
    }
}
