package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import huji.node.ReplicaNode;

public class Paxos extends ReplicaNode<PaxosMessage, PaxosValue> {

    private int storage = 0;
    private int view = 0;
    private PaxosValue lock;
    private boolean sentThisView;

    public Paxos(CommunicationChannel<PaxosMessage, PaxosValue> channel) {
        super(channel);
        sentThisView = false;
        lock = null;
    }

    @Override
    protected void running_process() {
        if( !sentThisView )
            handleView();

        super.running_process();
    }

    @Override
    protected boolean running_condition() {
        return super.running_condition() || !sentThisView;
    }

    private void handleView(){
        if ( offerView() )
            offer();
        else
            vote();
    }

    private boolean offerView(){
        return (view % 2) == 0;
    }

    private void offer(){

        if(isLocked())
            sendToAll(new PaxosMessage(id, -1, lock, view, storage, PaxosMessageType.OFFER));
        else
            sendToAll(new PaxosMessage(id, -1, client_messages.peek(), view, storage, PaxosMessageType.OFFER));

        sentThisView = true;

    }

    private void vote(){
        // vote
    }

    @Override
    protected boolean handle(PaxosMessage msg) {

        if ( super.handle(msg) || ignore(msg) || checkUpdate(msg) )
            return true;

        switch(msg.type){

        }

        return true;
    }

    private boolean ignore(PaxosMessage msg){
        return ( msg.view < this.view || msg.storage < this.storage );
    }

    private boolean checkUpdate(PaxosMessage msg){
        if ( msg.view > this.view ){
            viewUpdate(msg.view);
            return true;
        }

        if ( msg.storage > this.storage ){
            storageUpdate(msg.storage);
            return true;
        }

        return false;
    }

    private void viewUpdate(int view){

        this.view = view;

        // start new view???
    }

    private void storageUpdate(int storage){

        // request history

        this.storage = storage;
        unlockVal();

        // start current view again?
    }

    private boolean isLocked(){
        return lock != null;
    }

    private void unlockVal(){
        this.lock = null;
    }

    private void lockVal(PaxosValue val){
        this.lock = val;
    }

}
