package huji.channel;

import huji.message.Message;

public interface CommunicationAble<T> {
    int getID();
    void receive(Message<T> message);
    void send(Message<T> message);
    void sendToAll(Message<T> message);
    void sendToReplicas(Message<T> message);
}
