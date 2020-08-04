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
        channel.register_replica(this);
        this.client_messages = new ConcurrentLinkedQueue<>();
        this.committed = new TreeMap<>();
    }

    protected void commit(int storage, T value) {
        committed.putIfAbsent(storage, value);
    }

    protected void commit(Map<Integer,T> values) {
        for ( Map.Entry<Integer,T> entry : values.entrySet() ) {
            commit(entry.getKey(), entry.getValue());
        }
    }

    public Map<Integer,T> get_committed(int start, int end) {
        Map<Integer,T> output = new HashMap<>(end - start);
        for ( Map.Entry<Integer,T> entry : committed.entrySet() ) {
            if ( start <= entry.getKey() && ( entry.getKey() < end || end == -1) )
                output.put(entry.getKey(), entry.getValue());
        }

        return output;
    }

    public Map<Integer,T> get_committed(int start) {
        return get_committed(start, -1);
    }

    protected T peek_client_message(){
        return client_messages.peek();
    }

    protected T clear_client_message(){
        return client_messages.poll();
    }

    @Override
    protected boolean running_condition() {
        return super.running_condition() || !client_messages.isEmpty();
    }

    protected boolean handle(Message<T> msg){
        if ( msg.isClient ) {
            client_messages.add( msg.body );
            return true;
        }

        return false;
    }
}
