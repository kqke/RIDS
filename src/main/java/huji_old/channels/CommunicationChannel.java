package huji_old.channels;

import huji_old.messages.Message;

import java.util.concurrent.DelayQueue;

public class CommunicationChannel<T extends Message> extends Channel<T> {

    public CommunicationChannel() {
        super();
        communication_queue = new DelayQueue<>();
    }

    @Override
    protected void running_process() {
        if (!communication_queue.isEmpty()) {
            try {
                super.send(communication_queue.take());
            }
            catch (Exception ignored){}
        }
    }

    @Override
    public void send ( T message ) {
        communication_queue.add(message);
    }
}
