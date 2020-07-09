package huji.channel;

import huji.interfaces.CommunicationAble;
import huji.message.Message;

import java.io.Closeable;


public interface CommunicationChannel<T extends Message<R>, R> extends Runnable, Closeable {
    public int register(CommunicationAble<T, R> party);
    public void send(T msg);
}

