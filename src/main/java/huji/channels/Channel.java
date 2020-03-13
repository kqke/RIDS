package huji.channels;

import huji.environment.Environment;
import huji.interfaces.Process;
import huji.messages.Message;
import huji.messages.MessageType;

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
