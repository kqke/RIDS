package huji.nodes;

import huji.channel.CommunicationChannel;
import huji.interfaces.CommunicationAble;
import huji.interfaces.Process;
import huji.messages.Message;

public class Node<T extends Message> extends Process implements CommunicationAble<T> {
    protected final int id;
    protected final CommunicationChannel<T> channel;

    public Node(CommunicationChannel<T> channel) {
        this.channel = channel;
        this.id = channel.register(this);
    }

    @Override
    public void receive(Message message) {

    }

    @Override
    public void send(T message) {
        channel.send(message);
    }

    @Override
    protected void running_process() {

    }
}
