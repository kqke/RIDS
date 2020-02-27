package huji;

import huji.channels.CommunicationChannel;
import huji.logs.Logger;
import huji.protocols.replica.PaxosProtocol;
import huji.simulator.Simulator;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator()
                .addReplica(PaxosProtocol::new,1)
                .addChannel(CommunicationChannel::new)
                .addLogger(Logger::new);
        simulator.run();
    }
}
