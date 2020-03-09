package huji.simulator;

import huji.interfaces.Protocol;

public class Agent {
    int id;
    Protocol protocol;
    Thread thread;

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
