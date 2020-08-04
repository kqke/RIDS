package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.ViewChangeAble.ViewChangeAbleNode;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.impl.paxos.resources.PaxosViewResources;
import huji.interfaces.SecretShare;
import huji.message.Message;

public class Paxos extends ViewChangeAbleNode<PaxosValue> {

    // Protocol parameters
    private final int N;
    private final int F;

    //Secret share
    private final SecretShare secretShare;

    // View resources
    private PaxosValue locked_value = null;
    private Integer locked_value_view;
    private PaxosValue my_val = null;
    private PaxosViewResources viewResources;

    // Leader
    private int L;

    public Paxos(CommunicationChannel<PaxosValue> channel, int N, SecretShare secretShare) {
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
                my_val = peek_client_message();
        }
    }

    private boolean is_even_view( int view ) {
        return (view % 2) == 0;
    }

    @Override
    protected void send_view_beginning_message() {
        if ( is_even_view( view() ) )
            sendToReplicas(msgToReplicas(my_val, PaxosMessageType.OFFER));
        else
            sendToReplicas(
                    msgToReplicas(my_val, PaxosMessageType.VOTE)
                            .add_property("share",
                                    secretShare.encode(view(), id))
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
        locked_value_view = null;
    }

    protected void lock( PaxosValue val, int view ) {
        locked_value = val;
        locked_value_view = view;
        my_val = val;
    }

    protected PaxosValue locked_value() {
        return locked_value;
    }

    /*
     * Handle
     */
    @Override
    protected boolean handle(Message<PaxosValue> msg) {
        if ( super.handle(msg) )
            return true;

        if ( !(msg instanceof PaxosMessage) )
            return false;

        PaxosMessage paxos_msg = (PaxosMessage) msg;

        switch(paxos_msg.ptype){
            case OFFER:
                offerMessage(paxos_msg);
                return true;
            case ACK_OFFER_LOCKED:
                ackOfferLockedMessage(paxos_msg);
            case ACK_OFFER:
                ackOfferMessage();
                return true;
            case LOCK:
                lockMessage(paxos_msg);
                return true;
            case ACK_LOCK:
                ackLockMessage();
                return true;
            case DONE:
                doneMessage(paxos_msg);
                return true;
            case VOTE:
                voteMessage(paxos_msg);
                return true;
            case VC_STATE:
            case VC_STATE_LOCK:
            case VC_STATE_DONE:
                vcStateMessage(paxos_msg);
                return true;
        }

        return false;
    }

    private void offerMessage(PaxosMessage message){

        viewResources.putVal(message.from, message.body);

        if(this.is_locked() && locked_value_view > message.get_int_property("locked_view"))
            send(msgToOne(message.from, locked_value(), PaxosMessageType.ACK_OFFER_LOCKED));
        else
            send(msgToOne(message.from, message.body, PaxosMessageType.ACK_OFFER));
    }

    private void ackOfferLockedMessage(PaxosMessage message){
        viewResources.putLockedVal(message.body, message.get_int_property("locked_view"));
    }

    private void ackOfferMessage(){
        if (viewResources.countdownAckOffer()) {
            if(viewResources.changeLock())
                lock(viewResources.getLock(), viewResources.getLockView());
            sendToReplicas(msgToReplicas(my_val, PaxosMessageType.LOCK));
        }
    }

    private void lockMessage(PaxosMessage message){
        viewResources.lock(message.from, message.body);
        send(msgToOne(message.from, message.body, PaxosMessageType.ACK_LOCK));
    }

    private void ackLockMessage(){
        if (viewResources.countdownAckLock())
            sendToReplicas(msgToReplicas(my_val, PaxosMessageType.DONE));
    }

    private void doneMessage(PaxosMessage message){
        viewResources.done(message.from, message.body);
        if(viewResources.countdownDone()) {
            view_update();
        }
    }

    private void voteMessage(PaxosMessage message){
        viewResources.putShare(message.from, message.get_int_property("share"));
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
            sendToReplicas(msgToReplicas(viewResources.getPartyVal(L), type));
        }
    }

    private void vcStateMessage(PaxosMessage message){
        viewResources.putVCState(message.ptype, message.body, L);
        if(viewResources.countdownVCState()){
            switch(viewResources.VCState()){
                case DONE:
                    commit(viewResources.getPartyVal(L));
                    break;
                case LOCK:
                    lock(viewResources.getPartyVal(L), view());
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

    protected void commit(PaxosValue value) {
        super.commit(storage(), value);
        if(value == my_val)
            clear_client_message();
    }

    private PaxosMessage msgToOne(int to, PaxosValue body, PaxosMessageType type){
        return
                new PaxosMessage(
                id,
                to,
                body,
                view(),
                storage(),
                type);
    }

    private PaxosMessage msgToReplicas(PaxosValue body, PaxosMessageType type){
        return
                new PaxosMessage(
                        id,
                        -1,
                        body,
                        view(),
                        storage(),
                        type
                );
    }

}
