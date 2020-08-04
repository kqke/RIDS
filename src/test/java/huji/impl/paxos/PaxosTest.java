package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.channel.impl.AsyncChannel;
import huji.impl.dummyUser.DummyNode;
import huji.impl.ViewChangeAble.messages.ViewAbleMessage;
import huji.impl.paxos.messages.PaxosValue;
import huji.logger.Log;
import huji.logger.Logger;

import java.util.*;

class PaxosTest extends Paxos {
    public static final int N = 6;
    public static final Logger logger = new Logger();

    Set<Integer> restrictions = new HashSet<>(N);

    public PaxosTest(CommunicationChannel<ViewAbleMessage, PaxosValue> channel) {
        super(channel, N);
    }

    /*
     * Omission able
     */

    @Override
    public void send(ViewAbleMessage message) {
        if ( message.to == 0 )
            synchronized (System.out) {
                System.out.println(message);
            }
        else if ( ! restrictions.contains(message.to) )
            super.send(message);
    }

    public void block(int replica) {
        restrictions.add(replica);
    }

    public void unblock(int replica) {
        restrictions.remove(replica);
    }

    /*
     * Log able
     */

    @Override
    protected boolean handle(ViewAbleMessage msg) {
        logger.add(
                new Log()
        );
        return super.handle(msg);
    }

    /*
     * Run Example
     */
    public static void main(String[] args) {
        // start channel
        AsyncChannel<ViewAbleMessage, PaxosValue> channel = new AsyncChannel<>();

        // register users
        DummyNode<ViewAbleMessage, PaxosValue> dummy = new DummyNode<>(channel);
        Map<Integer, PaxosTest> replicas = new HashMap<>(N);
        for ( int i = 0; i < N; ++i ) {
            PaxosTest replica = new PaxosTest(channel);
            replicas.put(replica.id, replica);
        }

        // run
        channel.start();
        dummy.start();
        replicas.values().forEach(Thread::start);
        new UserCommandLine(replicas).run();

        // shutdown
        dummy.shutdown();
        for ( PaxosTest replica : replicas.values() )
            replica.shutdown();
        channel.shutdown();

        System.out.println("done");
    }
}