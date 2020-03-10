package huji.listener;

import huji.logger.logs.Log;
import huji.simulator.Simulator;

public interface Listener {
    void handle( Log log );
    void setSimulator( Simulator simulator );
}