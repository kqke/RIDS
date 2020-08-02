package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.impl.paxos.resources.PaxosViewResources;

import huji.node.ReplicaNode;


public class Paxos extends ReplicaNode<PaxosMessage, PaxosValue> {

    // Protocol parameters
    private int N;
    private int F;

    // Protocol vars
    private int storage = 0;
    private int view = 0;
    private boolean sentThisView;

    // View resources
    private PaxosViewResources viewResources;


    public Paxos(CommunicationChannel<PaxosMessage, PaxosValue> channel, int n, int f) {
        super(channel);
        N = n;
        F = f;
        viewResources = new PaxosViewResources(N, F);
        view_update(0);
        view_change();
    }

    @Override
    protected void running_process() {
        if( ! sentThisView )
            handleFreshView();
        super.running_process();
    }

    @Override
    protected boolean running_condition() {
        return super.running_condition() || !sentThisView;
    }

    /*
     * On fresh view
     */
    private void handleFreshView() {

        if ( is_even_view() )
            offer();
        else
            vote();

        sentThisView = true;
    }

    private boolean is_even_view() {
        return (view % 2) == 0;
    }

    private void offer() {
        sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view, storage, PaxosMessageType.OFFER));
    }

    private void vote() {
        sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view, storage, PaxosMessageType.VOTE));
    }

    /*
     * View change
     */
    private void view_change_if_needed(PaxosMessage msg){
        boolean is_view_changed = false;

        if ( msg.view > this.view ) {
            view_update(msg.view);
            is_view_changed = true;
        }

        if ( msg.storage > this.storage ) {
            storage_update(msg.storage);
            is_view_changed = true;
        }

        if (is_view_changed)
            view_change();
    }

    private void view_update(int view){
        this.view = view;
    }

    private void storage_update(int storage){
        this.storage = storage;
        unlock();
        // TODO: get history
    }

    private void view_change() {

        // TODO - fetch a new value if not locked and hold it in resources

        viewResources.VC();
        sentThisView = false;
    }

    /*
     * Locked value
     */
    private boolean is_locked(){
        return viewResources.isLocked(this.view);
    }

    private void unlock(){
        // TODO - fetch a new value?? VC?
    }

    private void lock(PaxosValue val){
        // TODO - lock via resources
    }

    /*
     * Handle
     */
    private boolean to_ignore(PaxosMessage msg) {
        return (msg.view < this.view) || (msg.storage < this.storage);
    }

    @Override
    protected boolean handle(PaxosMessage msg) {
        if ( super.handle(msg) || to_ignore(msg) )
            return true;

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

        send(new PaxosMessage(id, message.from, val, view, storage, PaxosMessageType.ACK_OFFER));
    }

    private void ackOfferMessage(PaxosMessage message){


        if (viewResources.countdownAckOffer()) {

            sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view, storage, PaxosMessageType.LOCK));
        }
    }

    private void lockMessage(PaxosMessage message){
        send(new PaxosMessage(id, message.from, message.body, view, storage, PaxosMessageType.ACK_LOCK));
    }

    private void ackLockMessage(PaxosMessage message){
        if (viewResources.countdownAckLock())
            sendToAll(new PaxosMessage(id, -1, viewResources.getViewVal(), view, storage, PaxosMessageType.DONE));
    }

    private void doneMessage(){
        if(viewResources.countdownDone()) {
            view_update(view++);
            view_change();
        }
    }

    private void voteMessage(PaxosMessage message){
        viewResources.putShare(message.from, message.body.getIntVal());
        if(viewResources.countdownVote()){
            computeLeader();
            // TODO - find a way to attach state to message
            sendToAll(new PaxosMessage(id, -1, viewResources.getLeaderVal(), view, storage, PaxosMessageType.VC_STATE));
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
            view_update(view++);
            view_change();
        }
    }

    private void computeLeader(){}

    private void commit(){};

    private void historyReqMessage(PaxosMessage message){}

    private void historyMessage(PaxosMessage message){}
}
