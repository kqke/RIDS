package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.channel.impl.AsyncChannel;
import huji.impl.dummy.DummyNode;
import huji.impl.dummy.DummyValue;
import huji.interfaces.SecretShare;
import huji.logger.Log;
import huji.logger.Logger;
import huji.message.Message;
import huji.interfaces.impl.ShamirSecretShare;

import java.util.*;

class PaxosTest<T extends Comparable<T>> extends Paxos<T> {
    public static final int N = 6;
    public static final Logger logger = new Logger();
    public static final SecretShare secret_share = new ShamirSecretShare(N, N/2);

    Set<Integer> out_restrictions = new HashSet<>(N);
    Set<Integer> in_restrictions = new HashSet<>(N);

    public PaxosTest(CommunicationChannel<T> channel) {
        super(channel, N, secret_share);
    }

    /*
     * Omission able
     */

    @Override
    public void send(Message<T> message) {
        if ( message.to == 0 )
            synchronized (System.out) {
                System.out.println(message);
            }
        else if ( ! out_restrictions.contains(message.to) )
            super.send(message);
    }

    @Override
    public void receive(Message<T> message) {
        if ( ! in_restrictions.contains(message.from) )
            super.receive(message);
    }

    public void outBlock(int replica) {
        out_restrictions.add(replica);
    }

    public void outUnblock(int replica) {
        out_restrictions.remove(replica);
    }

    public void inBlock(int replica) {
        in_restrictions.add(replica);
    }

    public void inUnblock(int replica) {
        in_restrictions.remove(replica);
    }

    public void block(int replica) {
        outBlock(replica);
        inBlock(replica);
    }

    public void unblock(int replica) {
        outUnblock(replica);
        inUnblock(replica);
    }

    /*
     * Log able
     */

    @Override
    protected boolean handle(Message<T> msg) {
        logger.add(
                new Log(msg.toString())
        );
        return super.handle(msg);
    }

    /*
     * Run Example
     */
    public static void main(String[] args) {
        // start channel
        AsyncChannel<DummyValue> channel = new AsyncChannel<>();

        // register users
        DummyNode<DummyValue> dummy = new DummyNode<>(channel, DummyValue::new);
        Map<Integer, PaxosTest<DummyValue>> replicas = new HashMap<>(N);
        for ( int i = 0; i < N; ++i ) {
            PaxosTest<DummyValue> replica = new PaxosTest<>(channel);
            replicas.put(replica.id, replica);
        }

        // run
        channel.start();
        dummy.start();
        replicas.values().forEach(Thread::start);
        new UserCommandLine<>(replicas, DummyValue::new).run();

        // shutdown
        dummy.shutdown();
        for ( PaxosTest<DummyValue> replica : replicas.values() )
            replica.shutdown();
        channel.shutdown();

        System.out.println("done");
    }
}