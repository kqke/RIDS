package huji.protocols.replica;

import huji.environment.Environment;
import huji.events.EventType;
import huji.generators.Generator;
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

//        TODO - Can't access n, f before setting environment.
//         ViewResources are currently initiated when the environment is set - shouldn't cause a problem.
//        this.resources = new ViewResources(N(), F());

        this.counters = new DecisionCounters();
    }

    public void setEnvironment(Environment environment){
        super.setEnvironment(environment);
        this.resources = new ViewResources(N(), F());
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

        return true;
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
            sendToAll(MessageType.ELECT, getShareableSecret() );
    }

    private void electMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        resources.add( message.from, Integer.parseInt(message.body) );
        if ( resources.countdownELECT() ) {
            int elected = getSecret();

            event(EventType.ELECTED,"view: " + view() + ", elected: " + elected);
            if ( resources.contains( elected ) )
                sendToAll( MessageType.VOTE, resources.get(elected));
            else
                sendToAll(MessageType.VC);
        }
    }

    private void voteMessage(ViewMessage message) {
        viewChangeIfNeeded(message);

        if ( counters.voteCountdown( view(), N() - F() ) ) {
            decide(message.body);
            increaseView();
            viewChange();
        }
    }

    private void vcMessage(ViewMessage message) {
        viewChangeIfNeeded(message);

        if ( counters.vcCountdown( view(), N() - F() ) ) {
            increaseView();
            viewChange();
        }
    }

    // View Change

    @Override
    protected void viewChange() {
        resources = new ViewResources(N(), F());
        super.viewChange();
    }

    private boolean viewChangeIfNeeded(ViewMessage message) {
        if ( setView(message.view) ) {
            viewChange();
            return true;
        }

        return false;
    }

    // Shamir

    private Generator getShamirEncoderDecoder() {
        return (Generator) getSharedInformation("generator");
    }

    private String getShareableSecret() {
        return Integer.toString( getShamirEncoderDecoder().encode( view(), id() ) );
    }

    private int getSecret() {
        return getShamirEncoderDecoder().decode( view(), resources.getShared() );
    }
}
