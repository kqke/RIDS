package huji.channel;

import huji.interfaces.CommunicationAble;
import huji.message.Message;

import java.io.Closeable;


public interface CommunicationChannel<T extends Message<R>, R> extends Runnable, Closeable {
    int register(CommunicationAble<T, R> party);
    void send(T msg);
}

