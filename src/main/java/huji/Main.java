package huji;

import huji.channels.CommunicationChannel;
import huji.logger.BasicLogger;
import huji.protocols.clients.ClientProtocol;
import huji.protocols.replica.PaxosProtocol;
import huji.simulator.Simulator;
import huji.simulator.shared.ShamirGenerator;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator()
                .addReplica(PaxosProtocol::new,5)
                .addClient(ClientProtocol::new,5)
                .addChannel(CommunicationChannel::new)
                .addLogger(BasicLogger::new)
                .addSecretsGenerator( () -> new ShamirGenerator(5,1) );
        simulator.run();
    }
}
