package huji.channel.impl;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import huji.channel.CommunicationChannel;
import huji.channel.CommunicationAble;
import huji.message.Message;
import huji.interfaces.Process;

public class AsyncChannel<T extends Comparable<T>> extends Process implements CommunicationChannel<T> {

    protected DelayQueue<Message<T>> communication_queue;
    protected Hashtable<Integer, CommunicationAble<T>> parties;
    protected List<Integer> replicas;
    private final Random random;
    private final Iterator<Integer> ids;

    public AsyncChannel() {
        this.communication_queue = new DelayQueue<>();
        this.random = new Random();
        this.ids = IntStream.generate(new AtomicInteger()::getAndIncrement).iterator();
        this.parties = new Hashtable<>();
        this.replicas = new LinkedList<>();
    }

    public int register(CommunicationAble<T> party){
        int id = getID();
        parties.put(id, party);
        return id;
    }

    public void registerReplica(CommunicationAble<T> party){
        replicas.add(party.getID());
    }

    @Override
    protected boolean runningCondition() {
        return !communication_queue.isEmpty();
    }

    @Override
    protected void runningProcess() {
        try {
            Message<T> msg = communication_queue.take();
            parties.get(msg.to).receive(msg);
        }
        catch (Exception ignored){}
    }

    public void send(Message<T> message) {
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
        wakeup();
    }

    @Override
    public Iterable<Integer> getAll() {
        return parties.keySet();
    }

    @Override
    public Iterable<Integer> getReplicas() {
        return replicas;
    }

    public int getID(){
        return ids.next();
    }
}
