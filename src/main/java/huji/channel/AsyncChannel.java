package huji.channel;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import huji.interfaces.CommunicationAble;
import huji.messages.Message;
import huji.interfaces.Process;

public class AsyncChannel<T extends Message> extends Process implements CommunicationChannel<T>{

    protected DelayQueue<T> communication_queue;
    protected Hashtable<Integer, CommunicationAble> parties;
    private Random random;
    private Iterator ids;

    public AsyncChannel() {
        this.communication_queue = new DelayQueue<>();
        this.random = new Random();
        this.ids = IntStream.generate(new AtomicInteger()::getAndIncrement).iterator();
    }

    public int register(CommunicationAble party){
        int id = getID();
        parties.put(id, party);
        return id;
    }

    protected void running_process() {
        if (!communication_queue.isEmpty()) {
            try {
                Message msg = communication_queue.take();
                parties.get(msg.to).receive(msg);
            }
            catch (Exception ignored){}
        }
    }

    public void send(T message) {
        final int rand = random.nextInt(10);
        if ( rand < 4 )
            message.setDelay(random.nextInt(500));
        if ( rand < 6 )
            message.setDelay(random.nextInt(1000));
        if ( rand < 9 )
            message.setDelay(random.nextInt(1500));
        else
            message.setDelay(random.nextInt(2000));

        communication_queue.add(message);
    }

    public int getID(){
        return (Integer) ids.next();
    }
}
