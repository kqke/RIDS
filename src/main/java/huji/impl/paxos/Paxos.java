package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.impl.paxos.resources.PaxosViewResources;


public class Paxos extends ViewChangeAbleNode {

    // Protocol parameters
    private final int N;
    private final int F;

    // View resources
    private PaxosValue locked_value = null;
    private PaxosViewResources viewResources;

    public Paxos(CommunicationChannel<PaxosMessage, PaxosValue> channel, int N) {
        super(channel);
        this.N = N;
        this.F = N / 2;
        this.viewResources = new PaxosViewResources(N, F);
    }

    /*
     * On fresh view
     */
    private boolean is_even_view() {
        return (view() % 2) == 0;
    }

    @Override
    protected void send_view_beginning_message() {
        if ( is_even_view() )
            sendToAll(
                    new PaxosMessage(id, -1, viewResources.getViewVal(), view(), storage(), PaxosMessageType.OFFER)
            );
        else
            sendToAll(
                    new PaxosMessage(id, -1, viewResources.getViewVal(), view(), storage(), PaxosMessageType.VOTE)
            );
    }

    /*
     * Locked value
     */
    protected boolean is_locked() {
        return locked_value != null;
    }

    protected void unlock () {
        locked_value = null;
    }

    protected void lock( PaxosValue val ) {
        locked_value = val;
    }

    protected PaxosValue locked_value() {
        return locked_value;
    }

    /*
     * Handle
     */
    @Override
    protected boolean handle(PaxosMessage msg) {
        if ( super.handle(msg) || to_ignore(msg) )
            return false;

        view_change_if_needed(msg);
        switch(msg.type){
            case OFFER:
                offerMessage(msg);
                break;
            case ACK_OFFER:
                ackOfferMessage(msg);
                break;
            case LOCK:
                lockMessage(msg);
                break;
            case ACK_LOCK:
                ackLockMessage(msg);
                break;
            case DONE:
                doneMessage();
                break;
            case VOTE:
                voteMessage(msg);
                break;
            case VC_STATE:
                vcStateMessage(msg);
                break;
            case HISTORY_REQ:
                historyReqMessage(msg);
                break;
            case HISTORY:
                historyMessage(msg);
                break;
        }

        return true;
    }

    private void offerMessage(PaxosMessage message){

        PaxosValue val = message.body;

        viewResources.putVal(message.from, val);

        if(viewResources.isLocked(val.view))
            val = viewResources.getViewVal();

        send(new PaxosMessage(id, message.from, val, view(), storage(), PaxosMessageType.ACK_OFFER));
    }

    private void ackOfferMessage(PaxosMessage message){


        if (viewResources.countdownAckOffer()) {

            sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view(), storage(), PaxosMessageType.LOCK));
        }
    }

    private void lockMessage(PaxosMessage message){
        send(new PaxosMessage(id, message.from, message.body, view(), storage(), PaxosMessageType.ACK_LOCK));
    }

    private void ackLockMessage(PaxosMessage message){
        if (viewResources.countdownAckLock())
            sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view(), storage(), PaxosMessageType.DONE));
    }

    private void doneMessage(){
        if(viewResources.countdownDone()) {
            view_update();
            view_change();
        }
    }

    private void voteMessage(PaxosMessage message){
        viewResources.putShare(message.from, message.body.getIntVal());
        if(viewResources.countdownVote()){
            computeLeader();
            // TODO - find a way to attach state to message
            sendToAll(new PaxosMessage(id, -1, viewResources.getLeaderVal(), view(), storage(), PaxosMessageType.VC_STATE));
        }
    }

    private void vcStateMessage(PaxosMessage message){
        viewResources.putVCState(message.body);
        if(viewResources.countdownVCState()){
            switch(viewResources.VCState()){
                case COMMIT:
                    commit();
                    break;
                case LOCK:
                    lock(viewResources.getLeaderVal());
                    break;
                default:
                    unlock();
                    break;
            }
            view_update();
            view_change();
        }
    }

    private void computeLeader(){}

    private void commit(){};

    private void historyReqMessage(PaxosMessage message){}

    private void historyMessage(PaxosMessage message){}
}
