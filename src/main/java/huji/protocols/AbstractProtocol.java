package huji.protocols;

import huji.interfaces.Channel;
import huji.interfaces.Log;
import huji.interfaces.Protocol;
import huji.logs.Logger;
import huji.messages.Message;
import huji.simulator.Agent;

public abstract class AbstractProtocol implements Protocol {
    private boolean _is_run = true;
    private Logger _logger = null;

    private Channel _communication = null;

    private Agent _agent = null;

    protected AbstractProtocol() {
    }

    public void setLogger(Logger logger) {
        _logger = logger;
    }

    public void addLog(Log log) {
        _logger.addLog(log);
    }

    public void setAgent(Agent agent) {
        _agent = agent;
    }

    protected int id() {
        return _agent.getId();
    }

    public void setChannel(Channel communication) {
        _communication = communication;
    }

    protected Channel getCommunication() {
        return _communication;
    }

    public void shutdown() {
        _is_run = false;
    }

    public boolean isRun() {
        return _is_run;
    }
}
