package huji.communication;

import huji.communication.messages.Message;

public interface CommunicationAble {
    public void receive(Message message);
    public void send(Message message);
}
