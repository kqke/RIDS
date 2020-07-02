package huji_old.environment.agent;

import huji_old.protocols.Protocol;

public class Agent {
    final public AgentType type;
    final public int id;
    public Protocol protocol;
    public Thread thread;

    public Agent(int id, AgentType type) {
        this.type = type;
        this.id = id;

        this.protocol = null;
        this.thread = null;
    }
}
