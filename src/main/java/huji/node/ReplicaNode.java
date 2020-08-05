package huji.node;

import huji.channel.CommunicationChannel;

import huji.message.Message;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReplicaNode<T extends Comparable<T>> extends Node<T> {
    private final Queue<T> client_messages;
    private final Map<Integer, T> committed;

    public ReplicaNode(CommunicationChannel<T> channel){
        super(channel);
        channel.registerReplica(this);
        this.client_messages = new ConcurrentLinkedQueue<>();
        this.committed = new TreeMap<>();
    }

    protected void commit(int storage, T value) {
        committed.putIfAbsent(storage, value);
        if ( value.equals( client_messages.peek() ) )
            client_messages.remove();
    }

    protected void commit(Map<Integer,T> values) {
        for ( Map.Entry<Integer,T> entry : values.entrySet() ) {
            commit(entry.getKey(), entry.getValue());
        }
    }

    public Map<Integer,T> getCommitted(int start, int end) {
        Map<Integer,T> output = new HashMap<>(end - start);
        for ( Map.Entry<Integer,T> entry : committed.entrySet() ) {
            if ( start <= entry.getKey() && ( entry.getKey() < end || end == -1) )
                output.put(entry.getKey(), entry.getValue());
        }

        return output;
    }

    public Map<Integer,T> getCommitted(int start) {
        return getCommitted(start, -1);
    }

    protected T peekClientMessage() {
        return client_messages.peek();
    }

    @Override
    protected boolean runningCondition() {
        return super.runningCondition() || !client_messages.isEmpty();
    }

    protected boolean handle(Message<T> msg) {
        if ( msg.isClient ) {
            client_messages.add( msg.body );
            return true;
        }

        return false;
    }
}
