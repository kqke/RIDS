package huji.protocols.replica;

import huji.environment.Environment;
import huji.events.EventType;
import huji.secrectshare.SecretShare;
import huji.messages.Message;
import huji.messages.MessageType;
import huji.messages.ViewMessage;
import huji.protocols.replica.view.ViewResources;

public class PaxosProtocol extends ReplicaProtocol {
    private ViewResources resources;

    public PaxosProtocol() {
        super();
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
        setViewIfNeeded(view_message);
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
            case COMMIT:
                commitMessage(view_message);
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
        if ( isFromOlderView(message) )
            return;

        resources.add(message.from,message.body);
        send( MessageType.ACK, message.from );
    }

    private void ackMessage(ViewMessage message) {
        if ( isFromOlderView(message) )
            return;

        if ( resources.countdownACK() )
            sendToAll(MessageType.ELECT, getShareableSecret() );
    }

    private void electMessage(ViewMessage message) {
        if ( isFromOlderView(message) ) {
            return;
        }

        resources.add( message.from, Integer.parseInt(message.body) );
        if ( resources.countdownELECT() ) {
            int elected = getSecret();

            //event(EventType.ELECTED,"id: " + id() + ", view: " + view() + ", elected: " + elected);
            if ( resources.contains( elected ) )
                sendToAll( MessageType.COMMIT, resources.get(elected));
            else
                sendToAll(MessageType.VC);
        }
    }

    private void commitMessage(ViewMessage message) {
        if ( decide(message.view,message.body) ) {
            sendToAll( MessageType.COMMIT, message.body, message.view);

            viewChange();
            if ( ! isFromOlderView(message) )
                increaseView();
        }
    }

    private void vcMessage(ViewMessage message) {
        if ( isFromOlderView(message) )
            return;

        if ( resources.countdownVC() ) {
            event(EventType.VC,"id: " + id() + ", view: " + view());
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

    private void setViewIfNeeded(ViewMessage message) {
        if ( setView(message.view) ) {
            viewChange();
        }
    }

    private boolean isFromOlderView(ViewMessage message) {
        return message.view < view();
    }

    // ShamirSecretShare

    private SecretShare getShamirEncoderDecoder() {
        return (SecretShare) getSharedInformation("generator");
    }

    private String getShareableSecret() {
        return Integer.toString( getShamirEncoderDecoder().encode( view(), id() ) );
    }

    private int getSecret() {
        return getShamirEncoderDecoder().decode( view(), resources.getShared() );
    }
}
