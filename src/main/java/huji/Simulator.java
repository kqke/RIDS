package huji;

import huji.channels.CommunicationChannel;
import huji.environment.Environment;
import huji.environment.agent.AgentType;
import huji.events.EventType;
import huji.logger.Log;
import huji.logger.Logger;
import huji.protocols.clients.DummyClientProtocol;
import huji.protocols.replica.PaxosProtocol;
import huji.generators.ShamirGenerator;

public class Simulator {
    private static final int N = 5;
    private static final int F = 0;

    private Logger logger;
    private Environment environment;

    private Environment getEnvironment() {
        return new SimulatorEnvironment();
    }

    private class SimulatorEnvironment extends Environment {
        @Override
        public void event(EventType type, String information) {
            if ( logger == null )
                return;

            Log log = new Log()
                    .parameter("type",type)
                    .parameter("info",information);
            logger.addLog(log);

            System.out.println(log);

            if ( type == EventType.DECIDE)
                shutdown();
        }
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.logger = new Logger();

        simulator.environment = simulator.getEnvironment();

        simulator.environment.share( "N", N );
        simulator.environment.share( "F", F );
        simulator.environment.share( "generator", new ShamirGenerator(N,F) );

        simulator.environment.setCommunicationChannel( new CommunicationChannel<>() );
        simulator.environment.addAgents(AgentType.Replica, PaxosProtocol::new,5);
        simulator.environment.addAgent(AgentType.Client, DummyClientProtocol::new);

        simulator.environment.run();
    }
}