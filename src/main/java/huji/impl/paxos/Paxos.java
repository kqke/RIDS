package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.viewChangeAble.ViewChangeAbleNode;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.resources.PaxosViewResources;
import huji.interfaces.SecretShare;
import huji.message.Message;

public class Paxos<T extends Comparable<T>> extends ViewChangeAbleNode<T> {

    // Protocol parameters
    private final int N;
    private final int F;

    //Secret share
    private final SecretShare secretshare;

    // View resources
    private boolean is_locked = false;
    private T value = null;
    private Integer value_view = 0;
    private PaxosViewResources<T> resources;

    // Leader
    private int leader;

    public Paxos(CommunicationChannel<T> channel, int N, SecretShare secretshare) {
        super(channel);
        this.N = N;
        this.F = N / 2;
        this.secretshare = secretshare;
        this.resources = new PaxosViewResources<>(N, F);
    }

    /*
     * On fresh view
     */
    @Override
    protected void onViewUpdate(int old_view, int view ) {
        if ( isEvenView( old_view ) || (view - old_view) > 1 ) {
            resources = new PaxosViewResources<>(N, F);
        }
    }

    private boolean isEvenView(int view ) {
        return (view % 2) == 0;
    }

    @Override
    protected void sendViewBeginningMessage() {

        /* Offer */
        if ( isEvenView( view() ) ) {

            if ( ! is_locked ) {
                value = peekClientMessage();
                value_view = view();
            }

            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.OFFER)
                            .addProperty("offer_view", value_view)
            );
        }

        /* Vote */
        else
            sendToReplicas(
                    msgToReplicas(null, PaxosMessageType.VOTE)
                            .addProperty("share", secretshare.encode(view(), id))
            );
    }

    /*
     * Locked value
     */
    @Override
    protected void onStorageUpdate() {
        this.resources = new PaxosViewResources<>(N, F);
        unlock();
    }

    protected void unlock() {
        is_locked = false;
    }

    protected void lock( T val, int view ) {
        is_locked = true;
        value = val;
        value_view = view;
    }

    /*
     * Handle
     */
    @Override
    protected boolean handle(Message<T> msg) {
        if ( super.handle(msg) )
            return true;

        if ( ! (msg instanceof PaxosMessage) )
            return false;

        PaxosMessage<T> paxos_msg = (PaxosMessage<T>) msg;

        switch(paxos_msg.ptype){
            case OFFER:
                handleOfferMessage(paxos_msg);
                break;
            case ACK_OFFER:
                handleAckOfferMessage(paxos_msg);
                break;
            case LOCK:
                handleLockMessage(paxos_msg);
                break;
            case ACK_LOCK:
                handleAckLockMessage();
                break;
            case DONE:
                handleDoneMessage(paxos_msg);
                break;
            case VOTE:
                handleVoteMessage(paxos_msg);
                break;
            case VC:
                handleVcMessage(paxos_msg);
                break;
            default:
                return false;
        }

        return true;
    }

    private void handleOfferMessage(PaxosMessage<T> message) {
        if ( is_locked && value_view > message.getIntProperty("offer_view") )
            send(
                    msgToOne(message.from, value, PaxosMessageType.ACK_OFFER)
                            .addProperty("offer_view", value_view)
                            .addProperty("locked", true)
            );
        else
            send(
                    msgToOne(message.from, message.body, PaxosMessageType.ACK_OFFER)
                            .addProperty("offer_view", message.getIntProperty("offer_view"))
                            .addProperty("locked", false)
            );
    }

    private void handleAckOfferMessage(PaxosMessage<T> message){
        if ( message.getBoolProperty("locked") )
            resources.putLockedAck(message.body, message.getIntProperty("offer_view"));

        if ( resources.countdownAckOffer() ) {
            if ( resources.isExistsLockedAck() )
                lock( resources.getLock(), resources.getLockView() );

            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.LOCK)
            );
        }
    }

    private void handleLockMessage(PaxosMessage<T> message) {
        resources.lock(message.from, message.body);
        send(
                msgToOne(message.from, null, PaxosMessageType.ACK_LOCK)
        );
    }

    private void handleAckLockMessage() {
        if ( resources.countdownAckLock() )
            sendToReplicas(
                    msgToReplicas(value, PaxosMessageType.DONE)
            );
    }

    private void handleDoneMessage(PaxosMessage<T> message){
        resources.done(message.from, message.body);
        if ( resources.countdownDone() ) {
            viewUpdate();
        }
    }

    private void handleVoteMessage(PaxosMessage<T> message){
        resources.putSecret(message.from, message.getIntProperty("share"));
        if ( resources.countdownVote() ) {
            computeLeader();
            sendToReplicas(
                    msgToReplicas(resources.getPartyValue(leader), PaxosMessageType.VC)
                            .addProperty("state", resources.getPartyState(leader))
            );
        }
    }

    private void handleVcMessage(PaxosMessage<T> message) {
        resources.putVCState(message.getStateProperty("state"), message.body);

        if ( resources.countdownVCState() ) {

            if ( resources.isExistsDone() || resources.isAllLock() )
                commit(resources.getVCValue(leader));

            else if ( resources.isExistsLock() )
                lock(resources.getVCValue(leader), view());

            else
                unlock();

            viewUpdate();
        }
    }

    private void computeLeader(){
        leader = secretshare.decode(view(), resources.getShares());
    }

    private PaxosMessage<T> msgToOne(int to, T body, PaxosMessageType type){
        return
                new PaxosMessage<>(
                id,
                to,
                body,
                view(),
                storage(),
                type);
    }

    private PaxosMessage<T> msgToReplicas(T body, PaxosMessageType type){
        return
                new PaxosMessage<>(
                        id,
                        -1,
                        body,
                        view(),
                        storage(),
                        type
                );
    }

}
