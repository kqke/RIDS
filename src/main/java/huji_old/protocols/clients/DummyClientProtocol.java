package huji_old.protocols.clients;

import huji_old.messages.Message;
import huji_old.messages.MessageType;
import huji_old.protocols.CommunicationAbleProtocol;

public class DummyClientProtocol extends CommunicationAbleProtocol {
    /*
        Dummy client that sends 10 messages to each replica
     */
    public DummyClientProtocol() {
        super();
    }

    @Override
    protected void running_process() {
        final int N = N();

        for ( int i = 0; i < N; ++i )
            for ( int j = 0; j < 1; ++j )
            send(
                    new Message(MessageType.CLIENT,id(),i,"{client: " + i + ", text: " + j + "}")
            );

       shutdown();
    }

    protected int N() {
        return (Integer) getSharedInformation("N");
    }

    @Override
    protected boolean handle(Message message) {
        return false;
    }
}
