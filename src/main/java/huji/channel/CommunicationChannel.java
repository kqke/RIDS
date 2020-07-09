package huji.channel;

import huji.interfaces.CommunicationAble;
import huji.messages.Message;


public interface CommunicationChannel<T extends Message> extends Runnable{
    public int register(CommunicationAble party);
    public void send(T msg);
}

