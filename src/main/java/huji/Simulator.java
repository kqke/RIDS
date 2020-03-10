package huji;

import huji.channels.Channel;
import huji.channels.CommunicationChannel;
import huji.environment.agent.Agent;
import huji.interfaces.*;
import huji.logger.BasicLogger;
import huji.logger.Logger;
import huji.logger.logs.Log;
import huji.logger.logs.Type;
import huji.protocols.Protocol;
import huji.protocols.clients.ClientAbleProtocol;
import huji.protocols.replica.PaxosProtocol;
import huji.simulator.shared.Generator;
import huji.simulator.shared.ShamirGenerator;

import java.util.*;

public class Simulator {
    private final int N = 5;
    private final int F = 0;
    private Logger logger;

    private Generator _secrets_generator;

    private int _N;
    private int _F;

    public Simulator() {
        _replicas = new ArrayList<>();
        _clients = new ArrayList<>();

        _N = 0;
        _F = 0;
    }

    private void addAgent(List<Agent> list, Factory<Protocol> factory, int times) {
        for ( int i = 0; i < times; i++ ) {

            Agent agent = new Agent(list.size());
            list.add(agent);

            agent.protocol = factory.getInstance();
            agent.protocol.setAgent(agent);
            agent.protocol.setChannel(_communication);
            agent.protocol.setLogger(_logger);
            agent.protocol.setSimulator(this);

            agent.thread = new Thread( agent.protocol );
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

    public Simulator addChannel(Factory<Channel> factory) {
        _communication = factory.getInstance();
        _communication.setSimulator(this);
        return this;
    }

    public Simulator addLogger(Factory<Logger> factory) {
        _logger = factory.getInstance();

        return this;
    }

    public Protocol getProtocol(int replica) {
        return _replicas.get(replica).protocol;
    }

    public Iterable<Protocol> getProtocols() {
        List<Protocol> protocols = new LinkedList<>();

        for ( Agent replica : _replicas )
            protocols.add( replica.protocol );

        return protocols;
    }

    public Protocol setProtocol(int replica, Protocol protocol) {
        return _replicas.get(replica).protocol = protocol;
    }

    public int getNumReplicas() {
        return _N;
    }

    public int getNumFailures() {
        return _F;
    }

    public void setNumFailures( int i ) {
        _F = i;
    }

    public Simulator addSecretsGenerator(Factory<Generator> factory) {
        _secrets_generator = factory.getInstance();
        return this;
    }

    public int getSharedSecret( int view, int id ) {
        return _secrets_generator.encode(view,id);
    }

    public int getSecret( int view, Map<Integer, Integer> shared_secrets ) {
        return _secrets_generator.decode(view, shared_secrets);
    }

    public void run() {
        _N = _replicas.size();
        _logger.addLog(
                new Log(Type.START_RUN).parameter("replicas",_N)
        );

        for ( Agent agent : _clients ) {
            agent.thread.start();
        }

        for ( Agent agent : _replicas ) {
            agent.thread.start();
        }

        _communication.run();

        for ( Log log : _logger ) {
            System.out.println( log );
        }
    }

    public void shutdown() {
        for ( Agent agent : _replicas ) {
            agent.protocol.shutdown();
        }

        for ( Agent agent : _clients ) {
            agent.protocol.shutdown();
        }

        _communication.shutdown();
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator()
                .addLogger(BasicLogger::new)
                .addChannel(CommunicationChannel::new)
                .addReplica(PaxosProtocol::new,5)
                .addClient(ClientAbleProtocol::new,5)
                .addSecretsGenerator( () -> new ShamirGenerator(5,1) );
        simulator.run();
    }
}