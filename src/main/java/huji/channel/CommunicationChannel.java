package huji.channel;

import huji.message.Message;

import java.io.Closeable;


public interface CommunicationChannel<T> extends Runnable, Closeable {
    int register(CommunicationAble<T> party);
    void register_replica(CommunicationAble<T> party);
    void send(Message<T> msg);
    Iterable<Integer> getAll();
    Iterable<Integer> getReplicas();
}

