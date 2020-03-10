package huji.environment;

import huji.channels.Channel;
import huji.environment.agent.Agent;
import huji.environment.agent.AgentType;
import huji.events.EventType;
import huji.interfaces.Factory;
import huji.messages.Message;
import huji.protocols.CommunicationAbleProtocol;

import huji.interfaces.Process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Environment extends Process {
    private Channel<Message> communication_channel;
    private List<Agent> agents;

    public Environment() {
        this.agents = new ArrayList<>();
        this.communication_channel = null;
    }

    // Communication Channel

    public void setCommunicationChannel ( Channel<Message> communication_channel ) {
        this.communication_channel = communication_channel;
    }

    public Channel<Message> getCommunicationChannel() {
        return communication_channel;
    }

    // Agents

    public void addAgent (AgentType type, Factory<CommunicationAbleProtocol> factory ) {
        Agent agent = new Agent( agents.size(), type );

        agent.protocol = factory.getInstance();
        agent.protocol.setIdentity( agent.id );
        agent.protocol.setEnvironment( this );

        agent.thread = new Thread( agent.protocol );

        agents.add( agent );
    }

    public void addAgents ( AgentType type, Factory<CommunicationAbleProtocol> factory, int times ) {
        for ( int i = 0; i < times; ++i )
            addAgent(type, factory);
    }

    public Iterable<Integer> getReplicasIDs() {
        List<Integer> result = new LinkedList<>();

        for ( Agent agent : agents ) {
            if ( agent.type == AgentType.Replica )
                result.add( agent.id );
        }

        return result;
    }

    public CommunicationAbleProtocol getAgentProtocol( int id ) {
        return (CommunicationAbleProtocol) agents.get( id ).protocol;
    }

    // Events

    abstract public void event( EventType type, String information );

    // Process

    protected void running_process() {
        for ( Agent agent : agents )
            agent.thread.start();

        communication_channel.run();
    }

    @Override
    public void shutdown() {
        super.shutdown();

        communication_channel.shutdown();
        for ( Agent agent : agents )
            agent.protocol.shutdown();

        try {
            for ( Agent agent : agents )
                agent.thread.join();
        } catch ( Exception ignored){}

        event( EventType.ENVIRONMENT_CLOSED, "" );
    }
}
