package huji.protocols.replica;

import huji.messages.Message;
import huji.messages.MessageType;
import huji.messages.ViewMessage;
import huji.protocols.replica.view.DecisionCounters;
import huji.protocols.replica.view.ViewResources;

public class PaxosProtocol extends ReplicaProtocol {
    private ViewResources resources;
    private DecisionCounters counters;

    public PaxosProtocol() {
        super();
        this.resources = new ViewResources();
        this.counters = new DecisionCounters();
    }

    @Override
    protected boolean handle(Message message) {
        if ( super.handle(message) )
            return true;

        ViewMessage view_message = (ViewMessage) message;

        switch (message.messageType) {
            case PROPOSE:
                proposeMessage(view_message);
                break;
            case ACK:
                ackMessage(view_message);
                break;
            case ELECT:
                electMessage(view_message);
                break;
            case VOTE:
                voteMessage(view_message);
                break;
            case VC:
                vcMessage(view_message);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.messageType);
        }
    }

    private void proposeMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        resources.add(message.from,message.body);
        send( MessageType.ACK, message.from );
    }

    private void ackMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        if ( resources.countdownACK() )
            sendToAll(MessageType.ELECT, getShareSecret(view));
    }

    private void electMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        resources.add( message.from, Integer.parseInt(message.body) );
        if ( resources.countdownELECT() ) {
            int elected = getSecret( view, _secrets );

            if ( resources.contains( elected ) )
                sendToAll( MessageType.VOTE, resources.get(elected));
            else
                sendToAll(MessageType.VC);
        }
    }

    private void voteMessage(ViewMessage message) {
        viewChangeIfNeeded(message);

        if ( counters.voteCountdown( view() ) ) {
            decide(message.body);
            increaseView();
            viewChange();
        }
    }

    private void vcMessage(ViewMessage message) {
        viewChangeIfNeeded(message);

        if ( counters.vcCountdown( view() ) ) {
            increaseView();
            viewChange();
        }
    }

    // View Change

    @Override
    protected void viewChange() {
        resources = new ViewResources();
        super.viewChange();
    }

    private boolean viewChangeIfNeeded(ViewMessage message) {
        if ( setView(message.view) ) {
            viewChange();
            return true;
        }

        return false;
    }
}
