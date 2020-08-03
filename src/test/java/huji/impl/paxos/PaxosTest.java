package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.channel.impl.AsyncChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosValue;
import huji.logger.Logger;

class PaxosTest extends Paxos {
    Logger logger;

    public PaxosTest(CommunicationChannel<PaxosMessage, PaxosValue> channel, Logger logger) {
        super(channel);
        this.logger = logger;
    }

    @Override
    protected boolean handle(PaxosMessage msg) {
        return super.handle(msg);
    }




    /*
     * Run Example
     */
    public static void main(String[] args) {
        // logger
        Logger logger = new Logger();

        // start channel
        AsyncChannel<PaxosMessage, PaxosValue> channel = new AsyncChannel<>();
        channel.start();

        // register users


        // register replicas


        // run
        new UserCommandLine().run();

        channel.shutdown();
        System.out.println("done");
    }
}