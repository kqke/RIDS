package huji.protocols;

import huji.Simulator;

public class ServerProtocol extends Protocol {

    public ServerProtocol() {
        super();
    }

    @Override
    public void run() {

    }

    static public ProtocolFactory getFactory() {
        return ServerProtocol::new;
    }
}
