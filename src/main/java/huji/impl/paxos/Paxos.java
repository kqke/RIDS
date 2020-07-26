package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.impl.paxos.resources.PaxosViewResources;
import huji.node.ReplicaNode;

public class Paxos extends ReplicaNode<PaxosMessage, PaxosValue> {

    private int storage = 0;
    private int view = 0;
    private PaxosValue myVal;
    private PaxosValue lock;
    private boolean sentThisView;

    private PaxosViewResources resources;

    public Paxos(CommunicationChannel<PaxosMessage, PaxosValue> channel, int n, int f) {
        super(channel);
        myVal = client_messages.peek();  // TODO What happens if empty? Blocking queue?
        resources = new PaxosViewResources(n, f);
        resources.reset(myVal);
        sentThisView = false;
        lock = null;
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
    }

    private boolean is_even_view() {
        return (view % 2) == 0;
    }

    private void offer() {
        sendToAll(new PaxosMessage(id, -1, myVal, view, storage, PaxosMessageType.OFFER));
        sentThisView = true;
    }

    private void vote() {
        // vote
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
        if(!is_locked())
            myVal = client_messages.peek();
        else
            myVal = lock;
        resources.reset(myVal);
        sentThisView = false;
    }

    /*
     * Locked value
     */
    private boolean is_locked(){
        return lock != null;
    }

    private void unlock(){
        this.lock = null;
    }

    private void lock(PaxosValue val){
        this.lock = val;
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
        if ( is_locked() )
            val = lock;
        send(new PaxosMessage(id, message.from, val, view, storage, PaxosMessageType.ACK_OFFER));
    }

    private void ackOfferMessage(PaxosMessage message){
        resources.checkVal(message.body);
        if (resources.countdownAckOffer()) {
            if (resources.changeLock(myVal)){
                lock(resources.getViewVal());
            }
            sendToAll(new PaxosMessage(id, -1, resources.getViewVal(), view, storage, PaxosMessageType.LOCK));
        }
    }

    private void lockMessage(PaxosMessage message){
        send(new PaxosMessage(id, message.from, message.body, view, storage, PaxosMessageType.ACK_LOCK));
    }

    private void ackLockMessage(PaxosMessage message){
        if (resources.countdownAckLock())
            sendToAll(new PaxosMessage(id, -1, resources.getViewVal(), view, storage, PaxosMessageType.DONE));
    }

    private void doneMessage(){
        if(resources.countdownDone()) {
            view_update(view++);
            view_change();
        }
    }

    private void voteMessage(PaxosMessage message){}

    private void vcStateMessage(PaxosMessage message){}

    private void historyReqMessage(PaxosMessage message){}

    private void historyMessage(PaxosMessage message){}
}
