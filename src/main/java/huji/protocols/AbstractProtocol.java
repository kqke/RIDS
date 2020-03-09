package huji.protocols;

import huji.interfaces.Channel;
import huji.interfaces.Log;
import huji.interfaces.Protocol;
import huji.logs.Logger;
import huji.simulator.Agent;
import huji.simulator.Simulator;

public abstract class AbstractProtocol implements Protocol {
    private boolean _is_run = true;
    private Logger _logger;
    private Channel _communication;
    private Agent _agent;
    private Simulator _simulator;

    protected AbstractProtocol() {
        _logger = null;
        _communication = null;
        _agent = null;
        _simulator = null;
    }

    @Override
    public void setLogger(Logger logger) {
        _logger = logger;
    }

    @Override
    public void addLog(Log log) {
        _logger.addLog(log);
    }

    @Override
    public void setAgent(Agent agent) {
        _agent = agent;
    }

    protected int id() {
        return _agent.getId();
    }

    @Override
    public void setChannel(Channel communication) {
        _communication = communication;
    }

    protected Channel getCommunication() {
        return _communication;
    }

    @Override
    public void setSimulator(Simulator simulator) {
        _simulator = simulator;
    }

    protected int N() {
        return _simulator.getNumReplicas();
    }

    protected int F() {
        return _simulator.getNumReplicas();
    }

    public void shutdown() {
        _is_run = false;
    }

    public boolean isRun() {
        return _is_run;
    }
}
