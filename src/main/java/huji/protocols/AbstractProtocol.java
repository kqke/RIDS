package huji.protocols;

import huji.interfaces.Log;
import huji.interfaces.Protocol;
import huji.logs.Logger;
import huji.simulator.Simulator;

public abstract class AbstractProtocol implements Protocol {
    private boolean _is_run = true;
    private Logger _logger = null;

    protected AbstractProtocol() {
    }

    public void setLogger(Logger logger) {
        _logger = logger;
    }

    protected void addLog(Log log) {
        _logger.addLog(log);
    }

    public void shutdown() {
        _is_run = false;
    }

    public boolean isRun() {
        return _is_run;
    }
}
