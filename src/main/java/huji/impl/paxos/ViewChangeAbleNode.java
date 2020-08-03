package huji.impl.paxos;

import huji.channel.CommunicationChannel;
import huji.impl.paxos.messages.PaxosMessage;
import huji.impl.paxos.messages.PaxosValue;
import huji.node.ReplicaNode;

public abstract class ViewChangeAbleNode extends ReplicaNode<PaxosMessage, PaxosValue>  {
    private int storage = 0;
    private int view = 0;
    private boolean sent_this_view = false;

    public ViewChangeAbleNode(CommunicationChannel<PaxosMessage, PaxosValue> channel) {
        super(channel);
    }

    @Override
    protected boolean running_condition() {
        return super.running_condition() || !sent_this_view;
    }

    @Override
    protected boolean handle(PaxosMessage msg){
        if ( super.handle(msg) )
            return true;

        view_change_if_needed(msg);
        if( ! sent_this_view ) {
            send_view_beginning_message();
            sent_this_view = true;
        }

        if ( to_ignore(msg) )
            return true;

        switch (msg.type) {
            case HISTORY_REQ:
                historyReqMessage(msg);
                return true;
            case HISTORY:
                historyMessage(msg);
                return true;
        }

        return false;
    }

    protected boolean to_ignore(PaxosMessage msg) {
        return (msg.view < this.view) || (msg.storage < this.storage);
    }

    protected void view_change_if_needed(PaxosMessage msg) {
        if ( msg.view > this.view() )
            view_update(msg.view);

        if ( msg.storage > this.storage ) {
            get_history(this.storage, msg.storage);
            storage_update(msg.storage);
        }
    }

    private void view_changed() {
        sent_this_view = false;
        on_view_change();
    }

    protected void on_view_change(){}

    /*
     * Storage able
     */
    protected int storage() {
        return storage;
    }

    private void storage_update() {
        ++this.storage;
        view_changed();
        on_storage_update();
    }

    private void storage_update(int storage) {
        this.storage = storage;
        view_changed();
        on_storage_update();
    }

    protected void on_storage_update(){}

    /*
     * View Change able
     */
    protected void view_update(){
        ++this.view;
        view_changed();
        on_view_update( this.view, view );
    }

    protected void view_update(int view){
        this.view = view;
        view_changed();
        on_view_update( this.view, view );
    }

    protected int view() {
        return view;
    }

    protected void on_view_update( int old_view, int view ){}
    protected abstract void send_view_beginning_message();

    /*
     * History
     */
    protected void get_history(int storage, int storage1) {
        // TODO
    }

    protected void historyReqMessage(PaxosMessage message) {
        // TODO
    }

    protected void historyMessage(PaxosMessage message) {
        // TODO
    }
}
