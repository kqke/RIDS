package huji.protocols.clients;

import huji.logger.logs.Log;
import huji.logger.logs.Type;
import huji.messages.impl.ClientMessage;
import huji.protocols.AbstractProtocol;

public class ClientProtocol extends AbstractProtocol {

    @Override
    public void run() {
        for ( int i = 0; i < 10; ++i ) {
            getCommunication().sendMessage(
                    new ClientMessage( id(), id(), "id: " + id() + ", message: " + i)
            );
            addLog(
                    new Log( Type.NEW_MESSAGE ).parameter("client",true).parameter("id",id()).parameter("message",i)
            );
        }
    }
}
