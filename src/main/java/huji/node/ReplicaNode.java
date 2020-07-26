package huji.node;

import huji.channel.CommunicationChannel;

import huji.message.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaNode<T extends Message<R>, R> extends Node<T, R> {

    protected Queue<R> client_messages;

    public ReplicaNode(CommunicationChannel<T, R> channel){
        super(channel);
        this.client_messages = new ConcurrentLinkedQueue<>();
    }

    protected boolean handle(T msg){

        if ( msg.isClient ) {
            client_messages.add( msg.body );
            return true;
        }

        return false;
    }
}
