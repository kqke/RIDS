package huji;

import huji.channels.CommunicationChannel;
import huji.logs.Logger;
import huji.protocols.replica.PaxosProtocol;
import huji.simulator.Simulator;
import huji.simulator.shared.ShamirGenerator;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator()
                .addReplica(PaxosProtocol::new,5)
                .addChannel(CommunicationChannel::new)
                .addLogger(Logger::new)
                .addSecretsGenerator( () -> new ShamirGenerator(5,1) );
        simulator.run();
    }
}
