package huji.listener;

import huji.interfaces.Listener;
import huji.logger.logs.Log;
import huji.simulator.Simulator;

public class BasicListener implements Listener {
    private Simulator simulator;

    @Override
    public void handle(Log log) {

    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
}
