package huji.channels;

import huji.messages.Message;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class CommunicationChannel<T extends Message> extends Channel<T> {
    private DelayQueue<T> communication_queue;

    public CommunicationChannel() {
        super();
        communication_queue = new DelayQueue<>();
    }

    @Override
    protected void running_process() {
        if (!communication_queue.isEmpty()) {
            try {
//                TODO - this waits for a new message with a bound
//                T message = communication_queue.poll(1000, TimeUnit.MILLISECONDS);
//                if (message != null)
//                    super.send(message);
//                TODO - this waits until there is a message with no delay
                super.send(communication_queue.take());
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send ( T message ) {
        communication_queue.add(message);
    }
}
