package huji.protocols;

import huji.environment.Environment;
import huji.events.EventType;
import huji.interfaces.Process;

public abstract class Protocol extends Process {
    private int id;

    private Environment environment;

    Protocol() {
        super();
        environment = null;
    }

    // Identity

    public void setIdentity( int id ) {
        this.id = id;
    }

    protected int id() {
        return id;
    }

    // Environment

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected Object getSharedInformation( String key ) {
        return environment.getSharedInformation(key);
    }

    // Events

    public void event ( EventType type, String information ) {
        environment.event( type, information );
    }
}