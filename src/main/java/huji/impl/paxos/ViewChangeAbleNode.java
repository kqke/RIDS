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

    protected void view_change_if_needed(PaxosMessage msg) {
        if ( msg.view > this.view() )
            view_update(msg.view);

        if ( msg.storage > this.storage )
            storage_update(msg.storage);
    }

    protected boolean to_ignore(PaxosMessage msg) {
        return (msg.view < this.view) || (msg.storage < this.storage);
    }

    /*
     * Storage able
     */

    protected int storage() {
        return storage;
    }

    private void storage_update(int storage){
        this.storage = storage;
        sent_this_view = false;
    }

    /*
     * View Change able
     */
    @Override
    protected boolean running_condition() {
        return super.running_condition() || !sent_this_view;
    }

    @Override
    protected boolean handle(PaxosMessage msg){
        if( ! sent_this_view ) {
            send_view_beginning_message();
            sent_this_view = true;
        }

        return super.handle(msg);
    }

    protected void view_update(){
        ++this.view;
    }

    protected void view_update(int view){
        this.view = view;
        sent_this_view = false;
    }

    protected int view() {
        return view;
    }

    protected abstract void send_view_beginning_message();
}
