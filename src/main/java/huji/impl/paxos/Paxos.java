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
    private boolean is_locked = false;
    private PaxosValue value = null;
    private Integer value_view = 0;
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
    protected void on_view_update( int old_view, int view ) {
        if ( is_even_view( old_view ) || (view - old_view) > 1 ) {
            viewResources = new PaxosViewResources(N, F);
        }
    }

    private boolean is_even_view( int view ) {
        return (view % 2) == 0;
    }

    @Override
    protected void send_view_beginning_message() {

        /* Offer */
        if ( is_even_view( view() ) ) {

            if ( ! is_locked ) {
                value = peek_client_message();
                value_view = view();
            }

            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.OFFER)
                            .add_property("offer_view", value_view)
            );
        }

        /* Vote */
        else
            sendToReplicas(
                    msgToReplicas(null, PaxosMessageType.VOTE)
                            .add_property("share", secretShare.encode(view(), id))
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

    protected void unlock() {
        is_locked = false;
    }

    protected void lock( PaxosValue val, int view ) {
        is_locked = true;
        value = val;
        value_view = view;
    }

    /*
     * Handle
     */
    @Override
    protected boolean handle(Message<PaxosValue> msg) {
        if ( super.handle(msg) )
            return true;

        if ( ! (msg instanceof PaxosMessage) )
            return false;

        PaxosMessage paxos_msg = (PaxosMessage) msg;

        switch(paxos_msg.ptype){
            case OFFER:
                handle_offer_message(paxos_msg);
                break;
            case ACK_OFFER:
                handle_ackOffer_message(paxos_msg);
                break;
            case LOCK:
                handle_lock_message(paxos_msg);
                break;
            case ACK_LOCK:
                handle_ackLock_message();
                break;
            case DONE:
                handle_done_message(paxos_msg);
                break;
            case VOTE:
                handle_vote_message(paxos_msg);
                break;
            case VC:
                handle_vc_message(paxos_msg);
                break;
            default:
                return false;
        }

        return true;
    }

    private void handle_offer_message(PaxosMessage message) {
        if ( is_locked && value_view > message.get_int_property("offer_view") )
            send(
                    msgToOne(message.from, value, PaxosMessageType.ACK_OFFER)
                            .add_property("offer_view", value_view)
                            .add_property("locked", true)
            );
        else
            send(
                    msgToOne(message.from, message.body, PaxosMessageType.ACK_OFFER)
                            .add_property("offer_view", message.get_int_property("offer_view"))
                            .add_property("locked", false)
            );
    }

    private void handle_ackOffer_message(PaxosMessage message){
        if ( message.get_bool_property("locked") )
            viewResources.put_locked_ack(message.body, message.get_int_property("offer_view"));

        if ( viewResources.countdown_AckOffer() ) {
            if ( viewResources.exists_locked_ack() )
                lock( viewResources.getLock(), viewResources.getLockView() );

            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.LOCK)
            );
        }
    }

    private void handle_lock_message(PaxosMessage message) {
        viewResources.lock(message.from, message.body);
        send(
                msgToOne(message.from, null, PaxosMessageType.ACK_LOCK)
        );
    }

    private void handle_ackLock_message() {
        if ( viewResources.countdown_AckLock() )
            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.DONE)
            );
    }

    private void handle_done_message(PaxosMessage message){
        viewResources.done(message.from, message.body);
        if ( viewResources.countdown_Done() ) {
            view_update();
        }
    }

    private void handle_vote_message(PaxosMessage message){
        viewResources.put_secret(message.from, message.get_int_property("share"));
        if ( viewResources.countdown_Vote() ) {
            compute_leader();
            sendToReplicas(
                    msgToReplicas(viewResources.getPartyVal(L), PaxosMessageType.VC)
                            .add_property("state", viewResources.getPartyState(L))
            );
        }
    }

    private void handle_vc_message(PaxosMessage message) {
        viewResources.putVCState(message.get_state_property("state"), message.body, L);

        if ( viewResources.countdown_VCState() ) {
            switch( viewResources.VCState() ) {
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
