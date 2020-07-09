package huji.interfaces;

import huji.messages.Message;

public interface CommunicationAble<T extends Message> {
    public void receive(T message);
    public void send(T message);
}
