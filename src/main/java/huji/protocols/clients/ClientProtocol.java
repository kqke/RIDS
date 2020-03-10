package huji.protocols.clients;

import huji.logger.Log;
import huji.messages.impl.ClientMessage;
import huji.protocols.CommunicationAbleProtocol;

public class ClientProtocol extends CommunicationAbleProtocol {

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