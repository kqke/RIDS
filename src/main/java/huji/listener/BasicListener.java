package huji.listener;

import huji.logger.Log;
import huji.Simulator;

public class BasicListener implements Listener {
    private Simulator simulator;

    @Override
    public void handle(Log log) {
        if ( log.type.equals( Type.DECIDE ) ) {
            simulator.shutdown();
        }
    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
}
