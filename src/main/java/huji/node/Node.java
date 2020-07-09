package huji.node;

import huji.channel.CommunicationChannel;
import huji.interfaces.CommunicationAble;
import huji.interfaces.Process;
import huji.message.Message;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Node<T extends Message<R>, R> extends Process implements CommunicationAble<T, R> {
    protected final int id;
    protected final CommunicationChannel<T, R> channel;

    private final Queue<T> messages;

    public Node(CommunicationChannel<T, R> channel) {
        this.channel = channel;
        this.id = channel.register(this);

        this.messages = new LinkedList<>();
    }

    @Override
    public void receive(T message) {
        messages.add(message);
        wakeup();
    }

    @Override
    public void send(T message) {
        channel.send(message);
    }

    @Override
    protected void running_process() {
        if (!messages.isEmpty()) {
            handle(messages.remove());
        } else
            while (messages.isEmpty()) {
                try {
                    Thread.currentThread().wait();
                }
                catch(Exception ignored){}
            }
    }

    protected abstract void handle(T msg);
}
