package huji.protocols.replica;

import huji.messages.Message;
import huji.messages.VoteMessage;

public class PaxosProtocol extends ReplicaProtocol {
    @Override
    protected void handle(Message message) {
        switch (message.type) {
            case "vote":
                voteMessage((VoteMessage)message);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.type);
        }
    }

    private void voteMessage(VoteMessage message) {
        return;
    }
}
