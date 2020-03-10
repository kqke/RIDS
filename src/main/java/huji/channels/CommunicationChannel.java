package huji.channels;

import huji.messages.Message;

import java.util.LinkedList;
import java.util.Queue;

public class CommunicationChannel<T extends Message> extends Channel<T> {
    private Queue<T> communication_queue;

    public CommunicationChannel() {
        super();
        communication_queue = new LinkedList<>();
    }

    @Override
    protected void running_process() {
        if ( ! communication_queue.isEmpty() ) {
            super.send( communication_queue.poll() );
        }
    }

    @Override
    public void send ( T message ) {
        communication_queue.add(message);
    }
}
