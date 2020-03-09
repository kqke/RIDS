package huji.listener;

import huji.interfaces.Listener;
import huji.logger.logs.Log;
import huji.logger.logs.Type;
import huji.simulator.Simulator;

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
