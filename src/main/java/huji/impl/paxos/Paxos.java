package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.ViewChangeAble.ViewChangeAbleNode;
import huji.impl.ViewChangeAble.messages.ViewAbleMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.impl.paxos.resources.PaxosViewResources;
import huji.interfaces.SecretShare;


public class Paxos extends ViewChangeAbleNode<PaxosValue> {

    // Protocol parameters
    private final int N;
    private final int F;

    //Secret share
    private final SecretShare secretShare;

    // View resources
    private PaxosValue locked_value = null;
    private PaxosValue my_val = null;
    private PaxosViewResources viewResources;

    // Leader
    private int L;

    public Paxos(CommunicationChannel<ViewAbleMessage, PaxosValue> channel, int N, SecretShare secretShare) {
        super(channel);
        this.N = N;
        this.F = N / 2;
        this.secretShare = secretShare;
        this.viewResources = new PaxosViewResources(N, F);
    }

    /*
     * On fresh view
     */
    @Override
    protected void on_view_update( int old_view, int view) {
        if ( is_even_view( old_view ) || (view - old_view) > 1 ) {
            this.viewResources = new PaxosViewResources(N, F);
            if(is_locked())
                my_val = locked_value();
            else
                my_val = client_messages.peek();
        }
    }

    private boolean is_even_view( int view ) {
        return (view % 2) == 0;
    }

    @Override
    protected void send_view_beginning_message() {
        if ( is_even_view( view() ) )
            sendToAll(
                    new ViewAbleMessage(id, -1, my_val, view(), storage(), PaxosMessageType.OFFER)
            );
        else
            sendToAll(
                    new ViewAbleMessage(id, -1, my_val.setIntVal(secretShare.encode(view(), id)), view(), storage(), PaxosMessageType.VOTE)
            );
    }

    /*
     * Locked value
     */
    @Override
    protected void on_storage_update() {
        this.viewResources = new PaxosViewResources(N, F);
        unlock();
    }

    protected boolean is_locked() {
        return locked_value != null;
    }

    protected void unlock() {
        locked_value = null;
    }

    protected void lock( PaxosValue val ) {
        locked_value = val;
        my_val = val;
    }

    protected PaxosValue locked_value() {
        return locked_value;
    }

    /*
     * Handle
     */
    @Override
    protected boolean handle(ViewAbleMessage msg) {
        if ( super.handle(msg) )
            return true;

        switch(msg.type){
            case OFFER:
                offerMessage(msg);
                return true;
            case ACK_OFFER_LOCKED:
                ackOfferLockedMessage(msg);
            case ACK_OFFER:
                ackOfferMessage();
                return true;
            case LOCK:
                lockMessage(msg);
                return true;
            case ACK_LOCK:
                ackLockMessage();
                return true;
            case DONE:
                doneMessage(msg);
                return true;
            case VOTE:
                voteMessage(msg);
                return true;
            case VC_STATE:
            case VC_STATE_LOCK:
            case VC_STATE_DONE:
                vcStateMessage(msg);
                return true;
        }

        return false;
    }

    private void offerMessage(ViewAbleMessage message){

        viewResources.putVal(message.from, message.body);

        if(this.is_locked() && locked_value().view > message.body.view)
            send(
                    new ViewAbleMessage(id, message.from, locked_value(), view(), storage(), PaxosMessageType.ACK_OFFER_LOCKED)
            );

        else
            send(
                    new ViewAbleMessage(id, message.from, message.body, view(), storage(), PaxosMessageType.ACK_OFFER)
            );
    }

    private void ackOfferLockedMessage(ViewAbleMessage message){
        viewResources.putLockedVal(message.body);
    }

    private void ackOfferMessage(){
        if (viewResources.countdownAckOffer()) {
            if(viewResources.changeLock())
                lock(viewResources.getLock());
            sendToAll(new ViewAbleMessage(id, -1, my_val, view(), storage(), PaxosMessageType.LOCK));
        }
    }

    private void lockMessage(ViewAbleMessage message){
        viewResources.lock(message.from, message.body);
        send(new ViewAbleMessage(id, message.from, message.body, view(), storage(), PaxosMessageType.ACK_LOCK));
    }

    private void ackLockMessage(){
        if (viewResources.countdownAckLock())
            sendToAll(new ViewAbleMessage(id, -1, my_val, view(), storage(), PaxosMessageType.DONE));
    }

    private void doneMessage(ViewAbleMessage message){
        viewResources.done(message.from, message.body);
        if(viewResources.countdownDone()) {
            view_update();
            view_change();
        }
    }

    private void voteMessage(ViewAbleMessage message){
        viewResources.putShare(message.from, message.body.getIntVal());
        if(viewResources.countdownVote()){
            compute_leader();
            PaxosMessageType type;
            switch(viewResources.getPartyState(L)){
                case DONE:
                    type = PaxosMessageType.VC_STATE_DONE;
                    break;
                case LOCK:
                    type = PaxosMessageType.VC_STATE_LOCK;
                    break;
                default:
                    type = PaxosMessageType.VC_STATE;
                    break;
            }
            sendToAll(
                    new ViewAbleMessage(id, -1, viewResources.getPartyVal(L), view(), storage(), type)
            );
        }
    }

    private void vcStateMessage(ViewAbleMessage message){
        viewResources.putVCState(message.type);
        if(viewResources.countdownVCState()){
            switch(viewResources.VCState()){
                case DONE:
                    commit();
                    break;
                case LOCK:
                    lock(viewResources.getPartyVal(L));
                    break;
                case NONE:
                    unlock();
                    break;
            }
            view_update();
        }
    }

    private void compute_leader(){
        L = secretShare.decode(view(), viewResources.getShares());
    }

    private void commit(){
        // TODO - overrride the method and check if we have to take a new client message!
    }

}
