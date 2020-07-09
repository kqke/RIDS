package huji.communication.channel;

import huji.communication.CommunicationAble;
import huji.communication.messages.Message;

import java.util.UUID;


public interface CommunicationChannel<T extends Message> extends Runnable{
    public UUID register(CommunicationAble party);
    public void send(T msg);
}

