package huji.interfaces;

import huji.logs.Logger;
import huji.simulator.Agent;
import huji.simulator.Simulator;

public interface Protocol extends Runnable {
    void setLogger(Logger logger);
    void addLog(Log log);

    void setChannel(Channel communication);
    void setSimulator(Simulator simulator);
    void setAgent(Agent agent);
}