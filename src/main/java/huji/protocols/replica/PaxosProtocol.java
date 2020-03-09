package huji.protocols.replica;

import huji.messages.*;

public class PaxosProtocol extends ReplicaProtocol {
    private int view;

    @Override
    protected void handle(Message message) {
        if ( viewChangeIfNeeded(message) )
            return;

        switch (message.type) {
            case PROPOSE:
                proposeMessage((ProposeMessage)message);
                break;
            case ACK:
                ackMessage((AckMessage)message);
                break;
            case ELECT:
                electMessage((ElectMessage)message);
                break;
            case VOTE:
                voteMessage((VoteMessage)message);
                break;
            case VC:
                vcMessage((ViewChangeMessage)message);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.type);
        }
    }

    private void proposeMessage(ProposeMessage message) {
        outChannel(
                new AckMessage(view, id(), message.from)
        );
    }

    private int ackCounter = 5;
    private void ackMessage(AckMessage message) {
        if (0 == --ackCounter) {
            outChannelToAll(
                    new ElectMessage(view, id(), 0, 0)
            );
        }
    }

    private void electMessage(ElectMessage message) {
        return;
    }

    private void voteMessage(VoteMessage message) {
        return;
    }

    private void vcMessage(ViewChangeMessage message) {
        return;
    }

    private boolean viewChangeIfNeeded(Message message) {
        if ( view < message.view ) {
            view = message.view;
            return true;
        }

        return false;
    }
}
