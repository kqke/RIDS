package huji.simulator;

import huji.interfaces.Factory;
import huji.interfaces.Protocol;
import huji.protocols.communication.CommunicationProtocol;

import java.util.*;


public class Simulator {
    private List<Agent> _replicas;
    private List<Agent> _clients;

    private CommunicationProtocol _communication;

    public Simulator() {
        _replicas = new ArrayList<>();
        _clients = new ArrayList<>();

    }

    private void addAgent(List<Agent> list, Factory<Protocol> factory, int times) {
        for ( int i = 0; i < times; i++ ) {
            Agent agent = new Agent();
            agent.protocol = factory.getInstance();
            agent.protocol.setCommunication();
            agent.protocol.setLogClass();

            agent.thread = new Thread( agent.protocol );

            list.add(agent);
        }
    }

    public Simulator addReplica(Factory<Protocol> factory, int times) {
        addAgent(_replicas, factory, times);
        return this;
    }

    public Simulator addClient(Factory<Protocol> factory, int times) {
        addAgent(_clients, factory, times);
        return this;
    }

    public void run() {
        for ( Agent agent : _replicas ) {
            agent.thread.start();
        }

        for ( Agent agent : _clients ) {
            agent.thread.start();
        }

        _communication.run();
    }

    private class Agent {
        Protocol protocol;
        Thread thread;
    }
}