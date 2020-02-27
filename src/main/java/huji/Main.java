package huji;

import huji.simulator.Simulator;
import huji.protocols.replica.ReplicaProtocol;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator()
                .addServer(ReplicaProtocol.getFactory(),4)
                .run();
    }
}
