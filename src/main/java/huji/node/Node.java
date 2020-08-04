package huji.node;

import huji.channel.CommunicationChannel;
import huji.interfaces.CommunicationAble;
import huji.interfaces.Process;
import huji.message.Message;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Node<T> extends Process implements CommunicationAble<T> {
    protected final int id;
    protected final CommunicationChannel<T> channel;

    private final Queue<Message<T>> messages;

    public Node(CommunicationChannel<T> channel) {
        this.channel = channel;
        this.id = channel.register(this);
        this.messages = new LinkedList<>();
    }

    @Override
    public int get_id() {
        return id;
    }

    @Override
    public void receive(Message<T> message) {
        messages.add(message);
        wakeup();
    }

    @Override
    public void send(Message<T> message) {
        if (message.to == id)
            receive(message);
        else
            channel.send(message);
    }

    @Override
    public void sendToAll(Message<T> message) {
        for (int party : channel.getAll())
            send(message.copy(party));
    }

    @Override
    public void sendToReplicas(Message<T> message) {
        for (int party : channel.getReplicas())
            send(message.copy(party));
    }

    @Override
    protected boolean running_condition() {
        return !messages.isEmpty();
    }

    @Override
    protected void running_process() {
        handle(messages.remove());
    }

    protected abstract boolean handle(Message<T> msg);
}
