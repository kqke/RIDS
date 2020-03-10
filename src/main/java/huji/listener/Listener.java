package huji.listener;

import huji.logger.Log;
import huji.Simulator;

public interface Listener {
    void handle( Log log );
    void setSimulator( Simulator simulator );
}