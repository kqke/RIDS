package huji.interfaces;

import huji.simulator.Simulator;

public interface Channel extends Runnable {
    void setSimulator(Simulator simulator);
}
