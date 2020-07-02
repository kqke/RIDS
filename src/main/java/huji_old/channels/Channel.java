package huji_old.channels;

import huji_old.environment.Environment;
import huji_old.interfaces.Process;
import huji_old.messages.Message;

public abstract class Channel<T extends Message> extends Process {
    private Environment environment;

    Channel() {
        environment = null;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void send ( T message ) {
        environment.getAgentProtocol( message.to ).receive( message );
    }
}
