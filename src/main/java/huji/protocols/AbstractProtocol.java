package huji.protocols;

import huji.interfaces.Log;
import huji.interfaces.Protocol;
import huji.logs.Logger;

public abstract class AbstractProtocol implements Protocol {
    private Logger _logger = null;

    protected AbstractProtocol() {
    }

    public void setLogger(Logger logger) {
        _logger = logger;
    }

    protected void addLog(Log log) {
        _logger.addLog(log);
    }
}
