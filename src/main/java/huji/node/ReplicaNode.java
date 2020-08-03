package huji.node;

import huji.channel.CommunicationChannel;

import huji.message.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaNode<T extends Message<R>, R> extends Node<T, R> {
    protected Queue<R> client_messages;

    public ReplicaNode(CommunicationChannel<T, R> channel){
        super(channel);
        channel.register_replica(this);
        this.client_messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected boolean running_condition() {
        return super.running_condition() || !client_messages.isEmpty();
    }

    protected boolean handle(T msg){
        if ( msg.isClient ) {
            client_messages.add( msg.body );
            return true;
        }

        return false;
    }
}
