package huji.protocols.clients;

import huji.messages.impl.ClientMessage;
import huji.protocols.AbstractProtocol;

public class ClientProtocol extends AbstractProtocol {

    @Override
    public void run() {
        for ( int i = 0; i < 10; ++i ) {
            getCommunication().sendMessage(
                    new ClientMessage( id(), id(), "id: " + id() + ", message: " + i)
            );
        }
    }
}
