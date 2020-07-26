package huji.interfaces;

import huji.message.Message;

public interface CommunicationAble<T extends Message<R>, R> {
    void receive(T message);
    void send(T message);
    void sendToAll(T message);
}
