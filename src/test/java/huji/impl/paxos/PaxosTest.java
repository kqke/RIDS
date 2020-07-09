package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.channel.impl.AsyncChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosValue;

class PaxosTest extends Paxos {

    public PaxosTest(CommunicationChannel<PaxosMessage, PaxosValue> channel) {
        super(channel);
    }

    public static void main(String[] args) {
        AsyncChannel<PaxosMessage, PaxosValue> channel = new AsyncChannel<>();
        channel.start();

        new UserCommands().run();

        channel.shutdown();
        System.out.println("done");
    }
}