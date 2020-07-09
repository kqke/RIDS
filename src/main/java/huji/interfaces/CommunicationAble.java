package huji.interfaces;

import huji.message.Message;

public interface CommunicationAble<T extends Message<R>, R> {
    public void receive(T message);
    public void send(T message);
}
