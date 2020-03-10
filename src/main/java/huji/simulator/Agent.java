package huji.simulator;

import huji.protocols.Protocol;

public class Agent {
    final int id;
    Protocol protocol;
    Thread thread;

    Agent(int id) {
        this.id = id;

        protocol = null;
        thread = null;
    }

    public int getId() {
        return id;
    }
}
