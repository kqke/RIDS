package huji;

import huji.channels.CommunicationChannel;
import huji.environment.LogableEnvironment;
import huji.environment.agent.AgentType;
import huji.logger.Logger;
import huji.protocols.clients.ClientProtocol;
import huji.protocols.replica.PaxosProtocol;
import huji.generators.ShamirGenerator;

public class Simulator {
    private static final int N = 5;
    private static final int F = 0;

    private Logger logger;
    private LogableEnvironment environment;

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.logger = new Logger();

        simulator.environment = new LogableEnvironment();
        simulator.environment.setLogger( simulator.logger );
        simulator.environment.setCommunicationChannel( new CommunicationChannel<>() );
        simulator.environment.addAgents(AgentType.Replica, PaxosProtocol::new,5);
        simulator.environment.addAgent(AgentType.Client, ClientProtocol::new);

        simulator.environment.share( "N", N );
        simulator.environment.share( "F", N );
        simulator.environment.share( "generator", new ShamirGenerator(N,F) );

        simulator.environment.run();
    }
}