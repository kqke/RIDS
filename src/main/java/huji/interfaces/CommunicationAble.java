package huji.interfaces;

import huji.message.Message;

public interface CommunicationAble<T extends Message<R>, R> {
    int get_id();
    void receive(T message);
    void send(T message);
    void sendToAll(T message);
    void sendToAllReplicas(T message);
}
