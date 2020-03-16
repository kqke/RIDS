package huji.protocols.clients;

import huji.messages.Message;
import huji.messages.MessageType;
import huji.protocols.CommunicationAbleProtocol;

public class DummyClientProtocol extends CommunicationAbleProtocol {
    public DummyClientProtocol() {
        super();
    }

    @Override
    protected void running_process() {
        final int N = N();

        for ( int i = 0; i < N; ++i )
            for ( int j = 0; j < 10; ++j )
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
