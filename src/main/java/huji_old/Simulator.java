package huji_old;

import huji_old.channels.AsyncOmissionChannel;
import huji_old.environment.Environment;
import huji_old.environment.agent.AgentType;
import huji_old.events.EventType;
import huji_old.logger.Log;
import huji_old.logger.Logger;
import huji_old.messages.Message;
import huji_old.protocols.clients.DummyClientProtocol;
import huji_old.protocols.replica.PaxosProtocol;
import huji_old.secrectshare.ShamirSecretShare;

public class Simulator {
    private static final int N = 5;
    private static final int F = 2;

    private Logger logger;
    private Environment environment;
    private boolean verbose = false;

    private Environment getEnvironment() {
        return new SimulatorEnvironment();
    }

    private class SimulatorEnvironment extends Environment {
        SimulatorEnvironment(){
            super();
        }

        @Override
        public void event(EventType type, String information) {
            /*
                Event handling for the environment is implemented here.
                Currently set up for omission failure of two replicas, triggered at the beginning of the simulation.
                More complex testing scenarios can be set up here.
             */

            if ( logger == null )
                return;

            // Log the event
            Log log = new Log()
                    .parameter("type",type)
                    .parameter("info",information);
            logger.addLog(log);

            // Print the log
            if ( verbose )
                System.out.println(log);

            // In case of channel initiation set omission constraints to two replicas
            if ( type == EventType.CHANNEL_INIT ) {
                AsyncOmissionChannel<Message> channel = ((AsyncOmissionChannel<Message>)this.getCommunicationChannel());
                channel.getConstraints().setOmission(3);
                channel.getConstraints().setOmission(4);
            }

            // Shutdown the simulation on decision
//            if ( type == EventType.DECIDE)
//                shutdown();
        }

    }

    private static void testSetup(){

        /*
            Sets up the environment for testing
         */

        Simulator simulator = new Simulator();
        simulator.logger = new Logger();

        // Toggle verbose
        simulator.verbose = true;

        simulator.environment = simulator.getEnvironment();

        // Share the parameters of the protocol with the agents
        simulator.environment.share( "N", N );
        simulator.environment.share( "F", F );

        // Share a ShamirSecretShare instance with the agents
        simulator.environment.share( "generator", new ShamirSecretShare(N,F) );

        // Set the communication channel to asynchronous channel with N + 1 agents (one for DummyClient)
        simulator.environment.setCommunicationChannel( new AsyncOmissionChannel<>(N + 1) );

//        simulator.environment.setCommunicationChannel( new CommunicationChannel<>() );

        // Add agents

        // 5 replicas using the PAXOS protocol
        simulator.environment.addAgents(AgentType.Replica, PaxosProtocol::new,5);

        // A dummy client that sends 10 messages to each replica, and shuts down
        // Implementation in huji/protocols/clients/DummyClientProtocol.java
        simulator.environment.addAgent(AgentType.Client, DummyClientProtocol::new);

        // Simulate the environment
        simulator.environment.run();
    }

    public static void main(String[] args) {
        /*

            Main method of the simulator

         */

        testSetup();
    }
}