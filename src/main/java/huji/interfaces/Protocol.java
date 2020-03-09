package huji.interfaces;

import huji.logs.Logger;
import huji.simulator.Agent;

public interface Protocol extends Runnable {
    void setLogger(Logger logger);
    void addLog(Log log);

    void setChannel(Channel communication);

    void setAgent(Agent agent);
}