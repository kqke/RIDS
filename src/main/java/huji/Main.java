package huji;

import huji.protocols.ServerProtocol;

import static huji.Env.*;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator(ASYNC)
                .addServer(ServerProtocol.getFactory(),4)
                .run();
    }
}
